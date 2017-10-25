package com.touclick.tcoa.framework.registry.accessor.producer;

import com.touclick.tcoa.framework.registry.Node;
import com.touclick.tcoa.framework.registry.accessor.AccessorException;

/**
 * Service registry interface
 *
 * @author bing.liu
 * @date 2015-08-16
 * @version 1.0
 */
public interface ServiceRegistryProducer {

    /**
     * Publish the service
     * @param serviceId
     * @param version
     * @param node
     * @param data
     * @param isTemp
     */
    public void publishService(String serviceId,
                               String version,
                               Node node,
                               byte[] data,
                               boolean isTemp) throws AccessorException;

    /**
     * Update the node data
     * @param serviceId
     * @param version
     * @param node
     * @param data
     */
    public void updateNode(String serviceId,
                           String version,
                           Node node,byte[] data) throws AccessorException;

    /**
     * Destroy ServiceRegistry producer
     */
    public void destroy();
}
