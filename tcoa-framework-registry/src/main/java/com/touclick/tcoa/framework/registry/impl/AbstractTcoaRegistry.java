package com.touclick.tcoa.framework.registry.impl;

import com.touclick.tcoa.framework.registry.Service;
import com.touclick.tcoa.framework.registry.TcoaRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A base implement class of the TcoaRegistry
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public abstract class AbstractTcoaRegistry implements TcoaRegistry{
    /** Logger */
    protected Logger logger = LoggerFactory.getLogger(AbstractTcoaRegistry.class);

    /** Service map*/
    private Map<String,Service> serviceMap = new ConcurrentHashMap<String, Service>();

    /**
     * Get the service by given serviceId and version
     * @param serviceId
     * @param version
     * @return
     */
    private Service getService(String serviceId,String version){
        String serviceUid = serviceId + ":" + version;
        Service service = serviceMap.get(serviceUid);

        if(service == null){
            synchronized (serviceId.intern()){
                service = serviceMap.get(serviceUid);

                if(service == null){
                    service = loadService(serviceId,version);

                    if(service != null){
                        serviceMap.put(serviceUid,service);
                    }
                }
            }
        }

        return service;
    }

    /**
     * Query the service
     * @param serviceId
     * @param version
     * @return
     */
    @Override
    public Service queryService(String serviceId,String version){
        return getService(serviceId,version);
    }

    /**
     * Load service by given serviceId and version
     * @param serviceId
     * @param version
     * @return
     */
    protected abstract Service loadService(String serviceId,String version);
}
