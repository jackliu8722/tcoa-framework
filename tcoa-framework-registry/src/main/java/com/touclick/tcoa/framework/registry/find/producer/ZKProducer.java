package com.touclick.tcoa.framework.registry.find.producer;

import com.touclick.tcoa.framework.registry.find.consumer.ZKConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The producer client for zookeeper
 *
 * @author bing.liu
 * @date 2015-08-16
 * @version 1.0
 */
public class ZKProducer extends ZKConsumer implements Producer{

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZKProducer.class);

    public ZKProducer(String cluserAddress, int sessionTimeout) throws IOException {
        super(cluserAddress,sessionTimeout);
    }

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
        if(this.root == null){
            throw new IllegalArgumentException("Zookeeper root not set.");
        }

        if(version == null || version.length() == 0 ){
            throw new IllegalArgumentException("Service version not given.");
        }

        StringBuilder path = new StringBuilder();
        path.append("/").append(root).append("/").append(serviceId);
        path.append("/").append(version).append("/").append(address);

        LOGGER.info("Try go publish address: " + path);

        this.createNode(path.toString(), config, isTmp, isSequential, doWatch);
    }

    /**
     * Delete the service
     * @param serviceId
     * @param version
     * @param address
     */
    @Override
    public void deleteService(String serviceId,String version,String address){
        if(this.root == null){
            throw new IllegalArgumentException("Zookeeper root not set.");
        }

        if(version == null || version.length() == 0 ){
            throw new IllegalArgumentException("Service version not given.");
        }

        StringBuilder path = new StringBuilder();
        path.append("/").append(root).append("/").append(serviceId);
        path.append("/").append(version).append("/").append(address);

        LOGGER.info("Try go delete address: " + path);

        this.deleteNode(path.toString());
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
        if(this.root == null){
            throw new IllegalArgumentException("Zookeeper root not set.");
        }

        if(version == null || version.length() == 0 ){
            throw new IllegalArgumentException("Service version not given.");
        }

        StringBuilder path = new StringBuilder();
        path.append("/").append(root).append("/").append(serviceId);
        path.append("/").append(version).append("/").append(address);

        LOGGER.info("Try to update service: " + path);

        super.updateNode(path.toString(),config);
    }
}
