package com.touclick.tcoa.framework.client.router;

import com.touclick.tcoa.framework.client.exception.TcoaException;
import com.touclick.tcoa.framework.client.transport.ConnectionProvider;
import com.touclick.tcoa.framework.client.transport.TTransportConnectionProvider;
import com.touclick.tcoa.framework.client.transport.TcoaTransport;
import com.touclick.tcoa.framework.registry.Node;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The core service router implementation
 */
public class CommonServiceRouter implements ServiceRouter{

    /** Logger */
    private final static Logger LOGGER = LoggerFactory.getLogger(CommonServiceRouter.class);

    /** Instance */
    private static CommonServiceRouter instance = new CommonServiceRouter();

    /** Connection provider */
    private ConnectionProvider connectionProvider = new TTransportConnectionProvider();

    /** Load balancer */
    private LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    /** Node loader */
    private NodeLoader nodeLoader = new RegistryNodeLoader();

    /** Node error map */
    private ConcurrentHashMap<String,NodeErrorInfo> errorMap = new ConcurrentHashMap<String, NodeErrorInfo>();

    protected void setConnectionProvider(ConnectionProvider connectionProvider){
        this.connectionProvider = connectionProvider;
    }

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);

    private final static int DISABLE_THRESHOLD = 1000;

    private final static int MAX_GET_RETRY = 2;

    private CommonServiceRouter(){
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try{
                    for(Map.Entry<String,NodeErrorInfo> entry : errorMap.entrySet()){
                        NodeErrorInfo err = entry.getValue();

                        List<Node> curNodes = nodeLoader.load(err.getServiceId(), err.getVersion());

                        Node n = err.getNode();

                        String nodeKey = entry.getKey();

                        if(n == null || !curNodes.contains(n)){
                            errorMap.remove(nodeKey);
                            continue;
                        }

                        if((!n.isHealthy() ) && (!n.isDisabled())){
                            try{
                                Socket sock = new Socket();
                                SocketAddress endpoint = new InetSocketAddress(n.getHost(),
                                        n.getPort());

                                sock.connect(endpoint,500);
                                sock.close();
                            }catch (Exception e){
                                String messgae = "Connect to unhealthy node " + nodeKey + " failed";
                                LOGGER.error("Distabled tcoa node " + nodeKey + " connect fail before retry");
                                continue;
                            }

                            errorMap.remove(nodeKey);
                            n.setHealthy(true);
                            LOGGER.warn("Re-enable tcoa node: "+ nodeKey);
                        }else{
                            errorMap.remove(nodeKey);
                            if(LOGGER.isDebugEnabled()){
                                LOGGER.debug("Check " + nodeKey + " healthy=" + n.isHealthy() +
                                    " disabled=" + n.isDisabled() + " err=" + err.getCount()
                                    + "/" + DISABLE_THRESHOLD);
                            }
                        }
                    }
                }catch (Throwable e){
                    LOGGER.error("Node check error.",e);
                }
            }
        },3,3, TimeUnit.SECONDS);
    }

    public static CommonServiceRouter getInstance(){
        return instance;
    }

    /**
     * Route the service and return the tcoatransport
     * @param serviceId
     * @param version
     * @param timeout
     * @return
     * @throws Exception
     */
    @Override
    public TcoaTransport routeService(String serviceId,String version,long timeout) throws Exception{
        Node node = null;

        TTransport transport = null;
        TcoaTransport tcoaTransport = null;

        List<Node> nodes = nodeLoader.load(serviceId,version);

        int retry = 0;

        while(true){
            try{
                node = loadBalancer.getNode(serviceId, version, nodes);
                transport = connectionProvider.getConnection(node,timeout);
                break;
            }catch (TcoaException e){
                disableNode(serviceId, version, node, DISABLE_THRESHOLD / 200);
                LOGGER.warn("Get service error : " + node.getNodeKey() + " "
                    + serviceId + "," +version) ;
                if(++retry >= MAX_GET_RETRY){
                    String message = "Cannot get transport in " + MAX_GET_RETRY
                            + "th try";
                    throw new TcoaException(message,e);
                }
            }
        }

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Get service '" + serviceId + "', node: " + node);
        }

        tcoaTransport = new TcoaTransport();
        tcoaTransport.setServiceNode(node);
        tcoaTransport.setTransport(transport);

        return tcoaTransport;
    }

    /**
     * Return the transport
     * @param tcoaTransport
     * @throws Exception
     */
    @Override
    public void returnConn(TcoaTransport tcoaTransport) throws Exception{
        String key = tcoaTransport.getServiceNode().getNodeKey();
        NodeErrorInfo err = errorMap.get(key);
        if(err != null){
            err.setCount(0);
        }

        connectionProvider.returnConnection(tcoaTransport);
    }

    /**
     * Disable the service node
     * @param serviceId
     * @param version
     * @param node
     * @param delta
     */
    private void disableNode(String serviceId,String version,Node node,int delta){
        if(node == null){
            return ;
        }

        String key = node.getNodeKey();
        NodeErrorInfo err = errorMap.get(key);

        connectionProvider.clearConnections(node);

        if(err == null){
            err = new NodeErrorInfo(serviceId,version,node,delta);
            errorMap.put(key,err);
        }else{
            if(err.getNode() != node){
                err.setNode(node);
            }
            err.addCount(delta);
        }

        if(LOGGER.isWarnEnabled()){
            LOGGER.warn(key + " disabled " + err.getCount() + "/" + DISABLE_THRESHOLD);
        }

        if(err.getCount() >= DISABLE_THRESHOLD){
            node.setHealthy(false);
            LOGGER.warn("service node [" + key + "] of " + serviceId + " was set to unhealthy.");
        }
    }

    @Override
    public void serviceException(String serviceId,String version,Throwable e,TcoaTransport tcoaTransport){
        String key = "null-service";
        Node node = null;

        if(tcoaTransport != null){
            node = tcoaTransport.getServiceNode();
            key = node.getNodeKey();

            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("Invalidate addr=" + key + ",prov=" + connectionProvider
                        + ", tcoaTransport=" + tcoaTransport);
            }
            connectionProvider.invalidateConnection(tcoaTransport);
        }

        int delta = 1;

        if(e instanceof TTransportException){
            Throwable cause = e.getCause();
            if(cause == null){
                int type = ((TTransportException)e).getType();
                switch (type){
                    case TTransportException.END_OF_FILE:
                        LOGGER.error("tcoa service=" + serviceId + " addr=" + key + " ex=" +
                        "RPC TTransportException END_OF_FILE");
                        delta = DISABLE_THRESHOLD / 200;
                        break;
                    default:
                        LOGGER.error("tcoa service=" + serviceId + " addr=" + key + " ex=" +
                        "RPC TTransportException type=" + type);
                        delta = DISABLE_THRESHOLD / 200;
                        break;
                }
            }else{
                if (cause instanceof java.net.SocketTimeoutException) {
                    delta = DISABLE_THRESHOLD / 500;
                    LOGGER.error("tcoa service=" + serviceId + " addr=" + key + " ex="
                            + "RPC TTransportException SocketTimeoutException");
                } else {
                    delta = DISABLE_THRESHOLD / 200;
                    LOGGER.error("tcoa service=" + serviceId + " addr=" + key + " ex="
                            + "RPC TTransportException " + cause.getMessage());
                }
            }
        } else if (e instanceof TcoaException) {
            delta = DISABLE_THRESHOLD / 200;
            LOGGER.error("xoa2 service=" + serviceId + " addr=" + key + " ex="
                    + "TcoaException " + e.getMessage());
        }
        if (delta <= 0) {
            delta = 1;
        }

        disableNode(serviceId, version, node, delta);
    }

    /**
     * Init loading the nodes from the registry by given serviceId and version
     * This method avoids being called when call the service interface,
     * @param serviceId
     * @param version
     */
    @Override
    public void initLoadNodes(String serviceId,String version){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Start loading the nodes for serviceId = " + serviceId + ", version = " + version);
        }
        List<Node> nodes = nodeLoader.load(serviceId,version);

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Load the nodes from registry successfully for serviceId = " + serviceId +
                ", version = " + version + ", node number = " +
                    (nodes != null ? nodes.size() : 0));
        }
    }
}
