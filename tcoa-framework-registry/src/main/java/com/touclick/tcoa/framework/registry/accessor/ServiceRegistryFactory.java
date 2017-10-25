package com.touclick.tcoa.framework.registry.accessor;

import com.touclick.tcoa.framework.registry.accessor.consumer.ServiceRegistryConsumer;
import com.touclick.tcoa.framework.registry.accessor.producer.ServiceRegistryProducer;

/**
 * ServiceRegistry factory
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public interface ServiceRegistryFactory {

    /**
     * Create service registry accessor
     * @param config
     * @param service
     * @param version
     * @return
     */
    public ServiceRegistryConsumer getServiceRegistryConsumer(Configuration config,
                                                              String service,
                                                              String version);

    /**
     * Get the service registry producer
     * @param config
     * @return
     */
    public ServiceRegistryProducer getServiceRegistryProducer(Configuration config);
}
