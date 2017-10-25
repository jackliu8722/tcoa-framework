package com.touclick.tcoa.framework.registry.impl;

import com.touclick.tcoa.framework.registry.Node;
import com.touclick.tcoa.framework.registry.NodeData;
import com.touclick.tcoa.framework.registry.NodeFactory;
import com.touclick.tcoa.framework.registry.Service;
import com.touclick.tcoa.framework.registry.accessor.*;
import com.touclick.tcoa.framework.registry.accessor.consumer.ServiceRegistryConsumer;
import com.touclick.tcoa.framework.registry.accessor.producer.ServiceRegistryProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tcoa registry implementation
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public class TcoaRegistryImpl extends AbstractTcoaRegistry{
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(TcoaRegistryImpl.class);

    private ServiceRegistryProducer producer;

    private ConcurrentHashMap<String,ServiceRegistryConsumer> consumerMap;

    private ServiceRegistryFactory serviceRegistryFactory;

    public TcoaRegistryImpl(ServiceRegistryFactory serviceRegistryFactory){
        this.serviceRegistryFactory = serviceRegistryFactory;
        this.consumerMap = new ConcurrentHashMap<String, ServiceRegistryConsumer>();

        if(LOGGER.isInfoEnabled()){
            LOGGER.info("TcoaRegistry initialized.");
        }
    }

    @Override
    protected Service loadService(final String serviceId, final String version){
        ServiceRegistryConsumer registryConsumer = consumerMap.get(constructAccessorKey(serviceId, version));

        if(registryConsumer == null){
            synchronized (constructAccessorKey(serviceId,version).intern()){
                if(registryConsumer == null){
                    try {
                        registryConsumer = serviceRegistryFactory.getServiceRegistryConsumer(
                            new Configuration(serviceId, version), serviceId, version);

                        if(LOGGER.isDebugEnabled()){
                            LOGGER.debug("Create registry consumer succ for serviceId: "
                                    + serviceId + ", version: " + version);
                        }
                        consumerMap.put(constructAccessorKey(serviceId,version),
                            registryConsumer);
                    }catch (InvalidConfigurationException e){
                        LOGGER.error("",e);
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        final ServiceRegistryConsumer consumer = registryConsumer;

        final Service service = new Service(serviceId,version,null);

        List<String> nodeList;

        try{
            nodeList = consumer.listNodesAndListenChange(new ChildrenChangeListener() {
                @Override
                public synchronized void childrenChanged(List<String> children) {
                    List<Node> nodes = service.getNodes();

                    HashSet<String> oldNodes = new HashSet<String>();
                    HashSet<String> newNodes = new HashSet<String>(children);

                    for(Node n : nodes){
                        oldNodes.add(n.getNodeKey());
                    }

                    HashSet<String> nodes2Add = new HashSet<String>();
                    nodes2Add.addAll(newNodes);
                    nodes2Add.remove(oldNodes);

                    HashSet<String> nodes2Del = new HashSet<String>();
                    nodes2Del.addAll(oldNodes);
                    nodes2Del.remove(newNodes);

                    if(nodes2Del.size() + nodes2Add.size() > 0){
                        if(LOGGER.isInfoEnabled()){
                            LOGGER.info("Service: " + service.getServiceId() + ",version:"
                                + version + ", nodes change from " + oldNodes +
                                 " to " + newNodes );
                        }
                    }

                    if(nodes2Add.size() > 0){
                        if(LOGGER.isDebugEnabled()){
                            LOGGER.debug("Service: "+ serviceId +" add nodes:" + nodes2Add);
                        }
                        addNodes(service,consumer,nodes2Add);
                    }

                    if(nodes2Del.size() > 0){
                        if(LOGGER.isDebugEnabled()){
                            LOGGER.debug("Service: "+ serviceId + " remove nodes:" + nodes2Del);
                        }
                        for(String node : nodes2Del){
                            nodes.remove(NodeFactory.getNode(node));
                        }
                    }
                }
            });
        }catch (AccessorException e){
            nodeList = null;
            LOGGER.error(e.getMessage(),e);
        }

        addNodes(service,consumer,new HashSet<String>(nodeList));

        if(LOGGER.isInfoEnabled()){
            LOGGER.info("Load service " + service);
        }
        return service;
    }

    /**
     * Add service node to the service
     * @param service
     * @param accessor
     * @param node2Add
     */
    private void addNodes(final Service service,final ServiceRegistryConsumer accessor,Set<String> node2Add){
        List<Node> nodes = service.getNodes();

        for(String node : node2Add){
            final Node n = NodeFactory.getNode(node);

            byte[] data ;
            try{
                String nodeStr = Node.generateNodeKey(n.getHost(),n.getPort());
                data = accessor.getNodeAndListenChange(nodeStr,
                        new DataChangeListener(){
                            @Override
                            public void dataChanged(byte[] d){
                                updateNodeState(n,d);
                            }
                        });
            }catch (AccessorException e){
                LOGGER.error(e.getMessage(),e);
                return ;
            }

            updateNodeState(n,data);
            nodes.add(n);
        }
    }

    /**
     * Update the node state
     * @param node
     * @param data
     */
    private void updateNodeState(final Node node,byte[] data){
        NodeData nodeData;

        try{
            nodeData = NodeData.valueOf(data);
            if((node.isDisabled() != nodeData.isDisabled()) ||
                    (node.isHealthy() != nodeData.isHealthy())){
                if(LOGGER.isInfoEnabled()){
                    LOGGER.info("Node: " + node + " data changed to: " + nodeData);
                    node.setDisabled(nodeData.isDisabled());
                    node.setHealthy(nodeData.isHealthy());
                }
            }
        }catch (Exception e){
            LOGGER.error("Malformed node data : " + (data == null ? "null" :
                new String(data)) + ", node is " + node.getHost() + ":" + node.getPort(),e);
        }
    }

    private String constructAccessorKey(String serviceId,String version){
        return serviceId + "-" + version;
    }

    @Override
    public void registerNode(String serviceId,String version,Node node){

        if(producer == null){

            try {
                producer = serviceRegistryFactory.getServiceRegistryProducer(
                        new Configuration(serviceId,version));
            } catch (InvalidConfigurationException e) {
                LOGGER.error("Create service registry producer error.",e);
                throw new RuntimeException(e);
            }
        }

        NodeData nodeData = new NodeData(node.isDisabled(),node.isHealthy());
        try{
            if(LOGGER.isInfoEnabled()){
                LOGGER.info("registry a node: " + node + " to service: " + serviceId);
            }
            producer.publishService(serviceId,version,node,nodeData.toBytes(),true);
        }catch (AccessorException e){
            switch (e.getCode()){
                case NONODE:
                    throw new RuntimeException("Register node: " +
                        node + " to service: " + serviceId + ", version: " +
                        version + " failed for: invalid 'service' or 'version'," +
                            "please make sure op have created.",e);

                case NOAUTH:
                    throw new RuntimeException("Register node: " +
                        node + " to service: " + serviceId + ", version:" +
                        version + " failed for no auth to create node.",e);
                default:
                    throw new RuntimeException("Register node: " +
                            node + " to service: " + serviceId + ", version: " +
                            version + " failed.",e);
            }
        }catch (Exception e) {
            throw new RuntimeException("Register node: " +
                    node + " to service: " + serviceId + ", version: " +
                    version + " failed.",e);
        }
    }

    /**
     * Destroy a service node in the registry center
     * @param serviceId
     * @param version
     * @param node
     */
    public void destroyNode(String serviceId,String version,Node node){

    }
}
