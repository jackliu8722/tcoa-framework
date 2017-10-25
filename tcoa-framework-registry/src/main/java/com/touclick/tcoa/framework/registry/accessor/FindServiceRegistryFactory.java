package com.touclick.tcoa.framework.registry.accessor;

import com.touclick.tcoa.framework.registry.accessor.consumer.FindServiceRegistryConsumer;
import com.touclick.tcoa.framework.registry.accessor.consumer.ServiceRegistryConsumer;
import com.touclick.tcoa.framework.registry.accessor.producer.FindServiceRegistryProducer;
import com.touclick.tcoa.framework.registry.accessor.producer.ServiceRegistryProducer;

/**
 * The implementation of the ServiceRegistryFactory based on find
 */
public class FindServiceRegistryFactory implements ServiceRegistryFactory{

    @Override
    public ServiceRegistryConsumer getServiceRegistryConsumer(Configuration configuration,
                                                              String service,
                                                              String version){
        return new FindServiceRegistryConsumer(service,version,configuration);
    }

    @Override
    public ServiceRegistryProducer getServiceRegistryProducer(Configuration configuration){
        return new FindServiceRegistryProducer(configuration);
    }
}
