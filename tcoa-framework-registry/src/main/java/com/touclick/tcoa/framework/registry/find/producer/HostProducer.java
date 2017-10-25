package com.touclick.tcoa.framework.registry.find.producer;

/**
 * Host producer
 *
 * @author bing.liu
 * @date 2015-10-24
 * @version 1.0
 */
public class HostProducer implements Producer {

    /**
     * publish the service
     * @param serviceId
     * @param version
     * @param address
     * @param isTmp
     */
    @Override
    public void publishService(String serviceId,String version,String address,boolean isTmp){
        publishService(serviceId, version, address, null, isTmp, false);
    }

    /**
     * publish the service
     * @param serviceId
     * @param version
     * @param address
     * @param config
     * @param isTmp
     */
    @Override
    public void publishService(String serviceId,String version,
                               String address,byte[] config,boolean isTmp){
        publishService(serviceId, version, address, config, isTmp, false);
    }

    /**
     * publish the service
     * @param serviceId
     * @param version
     * @param address
     * @param config
     * @param isTmp
     * @param isSequential
     */
    @Override
    public void publishService(String serviceId,String version,
                               String address,byte[] config,boolean isTmp,boolean isSequential){
        publishService(serviceId, version, address, config, isTmp, isSequential, true);
    }

    /**
     * publish the service
     * @param serviceId
     * @param version
     * @param address
     * @param config
     * @param isTmp
     * @param isSequential
     * @param doWatch
     */
    @Override
    public void publishService(String serviceId,String version,
                               String address,byte[] config,
                               boolean isTmp,boolean isSequential, boolean doWatch){

    }

    /**
     * Delete the service
     * @param serviceId
     * @param version
     * @param address
     */
    @Override
    public void deleteService(String serviceId,String version,String address){
    }

    /**
     * Update the service
     * @param serviceId
     * @param version
     * @param address
     * @param config
     */
    @Override
    public void updateService(String serviceId,String version,String address,
                              byte[] config){

    }

}
