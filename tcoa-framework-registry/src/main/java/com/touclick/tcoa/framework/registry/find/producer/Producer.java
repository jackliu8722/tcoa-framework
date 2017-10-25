package com.touclick.tcoa.framework.registry.find.producer;

import com.touclick.tcoa.framework.registry.find.consumer.ZKConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The producer interface
 *
 * @author bing.liu
 * @date 2015-10-24
 * @version 1.0
 */
public interface Producer {
    /**
     * publish the service
     * @param serviceId
     * @param version
     * @param address
     * @param isTmp
     */
    public void publishService(String serviceId,String version,String address,boolean isTmp);

    /**
     * publish the service
     * @param serviceId
     * @param version
     * @param address
     * @param config
     * @param isTmp
     */
    public void publishService(String serviceId,String version,
                               String address,byte[] config,boolean isTmp);

    /**
     * publish the service
     * @param serviceId
     * @param version
     * @param address
     * @param config
     * @param isTmp
     * @param isSequential
     */
    public void publishService(String serviceId,String version,
                               String address,byte[] config,boolean isTmp,boolean isSequential);

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
    public void publishService(String serviceId,String version,
                               String address,byte[] config,
                               boolean isTmp,boolean isSequential, boolean doWatch);

    /**
     * Delete the service
     * @param serviceId
     * @param version
     * @param address
     */
    public void deleteService(String serviceId,String version,String address);

    /**
     * Update the service
     * @param serviceId
     * @param version
     * @param address
     * @param config
     */
    public void updateService(String serviceId,String version,String address,
                              byte[] config);
}
