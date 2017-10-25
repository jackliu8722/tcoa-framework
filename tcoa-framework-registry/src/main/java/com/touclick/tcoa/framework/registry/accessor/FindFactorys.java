package com.touclick.tcoa.framework.registry.accessor;

import com.touclick.tcoa.framework.registry.find.consumer.Consumer;
import com.touclick.tcoa.framework.registry.find.consumer.HostConsumer;
import com.touclick.tcoa.framework.registry.find.consumer.ZKConsumer;
import com.touclick.tcoa.framework.registry.find.producer.HostProducer;
import com.touclick.tcoa.framework.registry.find.producer.Producer;
import com.touclick.tcoa.framework.registry.find.producer.ZKProducer;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Get the ZKConsumer and producer of the factory class
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public class FindFactorys {

    /** ZKConsumer cache */
    private static ConcurrentHashMap<Configuration,Consumer> consumerCache =
            new ConcurrentHashMap<Configuration, Consumer>();

    /** ZKProducer cache */
    private static ConcurrentHashMap<Configuration,Producer> producerCache =
            new ConcurrentHashMap<Configuration, Producer>();

    /** ZKConsumer timeout */
    private static final int CONSUMER_TIMEOUT = 20 * 1000;

    /** ZKProducer timeout */
    private static final int PRODUCER_TIMEOUT = 5000;

    public static Consumer getConsumer(Configuration configuration)
        throws IOException{
        Consumer consumer = consumerCache.get(configuration);
        if(consumer == null){
            synchronized (configuration){
                consumer = consumerCache.get(configuration);

                if(consumer == null){
                    if(configuration.getType() == Configuration.Type.zookeeper){
                        ZKConsumer zkConsumer = createZkConsumer(configuration);
                        consumerCache.put(configuration, zkConsumer);
                        consumer = zkConsumer;
                    }else if(configuration.getType() == Configuration.Type.host){
                        HostConsumer hostConsumer = createHostConsumer(configuration);
                        consumerCache.put(configuration,hostConsumer);
                        consumer = hostConsumer;
                    }
                }
            }
        }
        return consumer;
    }

    /**
     * Create host consumer
     * @param conf
     * @return
     */
    private static HostConsumer createHostConsumer(Configuration conf){
        HostConsumer hostConsumer = new HostConsumer(conf.getCluster());
        return hostConsumer;
    }

    /**
     * Create zookeeper consumer.
     * @param conf
     * @return
     * @throws IOException
     */
    private static ZKConsumer createZkConsumer(Configuration conf) throws IOException{
        ZKConsumer zkConsumer = new ZKConsumer(conf.getCluster(),CONSUMER_TIMEOUT);
        zkConsumer.addAuthInfo(conf.getClientUsername(),
                conf.getClientPassword());
        zkConsumer.setRoot(conf.getRoot());
        return zkConsumer;
    }

    /**
     *
     * @param configuration
     * @return
     */
    public static Producer getProducer(Configuration configuration) throws IOException{
        Producer producer = producerCache.get(configuration);

        if(producer == null){
            synchronized (configuration){
                producer = producerCache.get(configuration);
                if(producer == null){
                    if(configuration.getType() == Configuration.Type.zookeeper){
                        ZKProducer zkProducer = createZKProducer(configuration);
                        producerCache.put(configuration, zkProducer);
                        producer = zkProducer;
                    }else if(configuration.getType() == Configuration.Type.host){
                        HostProducer hostProducer = createHostProducer();
                        producerCache.put(configuration,hostProducer);
                        producer = hostProducer;
                    }
                }
            }
        }

        return producer;
    }

    /**
     * Create zookeeper Producer.
     * @param configuration
     * @return
     * @throws IOException
     */
    private static ZKProducer createZKProducer(Configuration configuration) throws IOException{
        ZKProducer zkProducer = new ZKProducer(configuration.getCluster(),PRODUCER_TIMEOUT);
        zkProducer.addAuthInfo(configuration.getServerUsername(),
                configuration.getServerPassword());
        zkProducer.setRoot(configuration.getRoot());
        return zkProducer;
    }

    /**
     * Create host producer
     * @return
     */
    private static HostProducer createHostProducer(){
        return new HostProducer();
    }
}
