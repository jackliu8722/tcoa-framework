package com.touclick.tcoa.framework.registry.accessor.producer;

import com.touclick.tcoa.framework.registry.Node;
import com.touclick.tcoa.framework.registry.accessor.AccessorException;
import com.touclick.tcoa.framework.registry.accessor.Configuration;
import com.touclick.tcoa.framework.registry.accessor.FindFactorys;
import com.touclick.tcoa.framework.registry.find.producer.Producer;
import com.touclick.tcoa.framework.registry.find.producer.ZKProducer;

import java.io.IOException;

/**
 * Service registry interface implementation.
 *
 * @author bing.liu
 * @date 2015-08-16
 * @version 1.0
 */
public class FindServiceRegistryProducer implements ServiceRegistryProducer{

    private Configuration config;

    private Producer producer;

    public FindServiceRegistryProducer(Configuration config){
        try{
            this.config = config;
            producer = FindFactorys.getProducer(config);
        }catch (IOException e){
            throw new RuntimeException("Failed to connect to zookeeper cluster '"
                + this.config.getCluster() + "'.",e);
        }
    }

    @Override
    public void publishService(String serviceId,String version,Node node,byte[] data,boolean isTemp)
        throws AccessorException{
        try{
            producer.publishService(serviceId,version,node.getNodeKey(),data,isTemp,false);
        }catch (Exception e){
            throw AccessorException.valueOf(e);
        }
    }

    @Override
    public void updateNode(String serviceId,String version,Node node,byte[] data) throws AccessorException{
        try{
            producer.updateService(serviceId, version, node.getNodeKey(), data);
        }catch (Exception e){
            throw AccessorException.valueOf(e);
        }
    }

    @Override
    public void destroy(){
        try{
            if(config.getType() == Configuration.Type.zookeeper){
                ((ZKProducer)producer).close();
            }
        }catch (Exception e){
            throw new RuntimeException("Error to destroy.",e);
        }
    }
}
