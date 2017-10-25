package com.touclick.tcoa.framework.server.core;

import com.touclick.tcoa.framework.registry.Node;
import com.touclick.tcoa.framework.registry.NodeFactory;
import com.touclick.tcoa.framework.registry.TcoaRegistryFactory;
import com.touclick.tcoa.framework.server.conf.ServiceConf;
import com.touclick.tcoa.framework.server.exception.TcoaServerException;
import org.apache.thrift.TException;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * ServerFactory implementation.
 *
 * @author bing.liu
 * @date 2015-08-18
 * @version 1.0
 */
public class TcoaServerFactory implements IServerFactory{


    private static final TcoaServerFactory instance = new TcoaServerFactory();

    public static TcoaServerFactory getInstance() {
        return instance;
    }

    /** service builder */
    private IServiceBuilder serviceBuilder = new SimpleServiceBuilder();

    /** service cache */
    private Map<ServiceConf,TService> serviceCache = new HashMap<ServiceConf, TService>();

    /** Weather the service conf loaded.*/
    private volatile boolean isServiceConfLoad = false;

    /**
     * Load the service conf if not load.
     */
    private void loadServiceConf(){
        if(!isServiceConfLoad){
            synchronized (ServiceConf.class){
                if(!isServiceConfLoad){
                    ServiceConf.load();
                    isServiceConfLoad = true;
                }
            }
        }
    }

    @Override
    public void startServer(String serviceId,String version,boolean isRegistry) throws TcoaServerException{

        loadServiceConf();

        ServiceConf conf = ServiceConf.getServiceConf(serviceId,version);

        if(conf == null){
            throw new TcoaServerException("The service conf does not exist for serviceId:" + serviceId+
                ", version: " + version + ", please check it.");
        }

        TService service = serviceCache.get(conf);
        if(service == null){
            service = serviceBuilder.build(conf);
            if(service != null){

                Node node = NodeFactory.getNode(getLocalIp(),conf.getServicePort(),false,true);
                service.setNode(node);
                service.setRegistry(TcoaRegistryFactory.getInstance().getRegistry());
                serviceCache.put(conf,service);
            }else{
                throw new TcoaServerException("Build service error for conf: " + conf);
            }
        }

        if(isRegistry){
            try {
                service.connectZK();
            } catch (TException e) {
                throw new TcoaServerException("Connect to zookeeper error.",e);
            }
        }
        new Thread(service).start();
    }

    @Override
    public void stopServer(String serviceId,String version) throws TcoaServerException{
        ServiceConf conf = ServiceConf.getServiceConf(serviceId,version);

        if(conf == null){
            throw new TcoaServerException("The service conf does not exist for serviceId:" + serviceId+
                    ", version: " + version + ", please check it.");
        }

        TService service = serviceCache.get(conf);
        if(service == null){
            throw new TcoaServerException("The service is not running,conf: " + conf);
        }

        service.stopServer();
    }

    @Override
    public void startAllServer()throws TcoaServerException{
        Map<String,ServiceConf> serviceConfMap = ServiceConf.getServiceConfCache();
        for(ServiceConf conf : serviceConfMap.values()){
            startServer(conf.getServiceId(),conf.getServiceVersion(),true);
        }
    }

    @Override
    public void stopAllServer() throws TcoaServerException{
        Map<String,ServiceConf> serviceConfMap = ServiceConf.getServiceConfCache();
        for(ServiceConf conf : serviceConfMap.values()){
            stopServer(conf.getServiceId(), conf.getServiceVersion());
        }
    }

    public IServiceBuilder getServiceBuilder() {
        return serviceBuilder;
    }

    public void setServiceBuilder(IServiceBuilder serviceBuilder) {
        this.serviceBuilder = serviceBuilder;
    }

    private String getLocalIp(){
        Enumeration e;
        try{
            e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements()){
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while(ee.hasMoreElements()){
                    InetAddress i = (InetAddress) ee.nextElement();
                    if(i instanceof Inet4Address){
                        String ip = i.getHostAddress();
                        if(ip.startsWith("11") || ip.startsWith("192")){
                            return ip;
                        }
                    }
                }
            }
        }catch (SocketException e1){

        }
        return "127.0.0.1";
    }
}
