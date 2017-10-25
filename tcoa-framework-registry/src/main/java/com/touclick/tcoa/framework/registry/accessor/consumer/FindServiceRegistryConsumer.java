package com.touclick.tcoa.framework.registry.accessor.consumer;

import com.touclick.tcoa.framework.registry.accessor.*;
import com.touclick.tcoa.framework.registry.find.consumer.Consumer;
import com.touclick.tcoa.framework.registry.find.consumer.ZKConsumer;
import com.touclick.tcoa.framework.registry.find.consumer.NodeChangeListener;

import java.io.IOException;
import java.util.List;

/**
 * The ServiceRegistryConsumer implementation based on find
 */
public class FindServiceRegistryConsumer implements ServiceRegistryConsumer {

    /** Consumer */
    private Consumer consumer;

    private String service;

    private String version;

    private Configuration config;

    @Override
    public String getService(){
        return service;
    }

    @Override
    public String getVersion(){
        return version;
    }

    public FindServiceRegistryConsumer(String service, String version,
                                       Configuration config){
        this.config = config;
        this.service = service;
        this.version = version;

        try{
            consumer = FindFactorys.getConsumer(config);
        }catch (IOException e){
            throw new RuntimeException("Failed to connection to zookeeper cluster '" +
                this.config.getCluster() + "'",e);
        }
    }

    @Override
    public List<String> listStatesAndListenChange(final ChildrenChangeListener listener) throws AccessorException {
        try{
            return consumer.getAllStatAndListenChange(this.service,
                    this.version,listener == null ? null: new NodeChangeListener() {
                        @Override
                        public void listChanged(List<String> nodeList) {
                            listener.childrenChanged(nodeList);
                        }
                    });
        }catch (Exception e){
            throw AccessorException.valueOf(e);
        }
    }

    @Override
    public byte[] getStateAndListenChange(String state,DataChangeListener listener) throws AccessorException{
        throw new IllegalStateException("Not supported.");
    }

    @Override
    public List<String> listNodesAndListenChange(final ChildrenChangeListener listener) throws AccessorException{
        try{
            return consumer.getAddressAndListenChange(this.service,
                    this.version,listener == null ? null:new NodeChangeListener() {
                        @Override
                        public void listChanged(List<String> nodeList) {
                            listener.childrenChanged(nodeList);
                        }
                    });
        }catch (Exception e){
            throw AccessorException.valueOf(e);
        }
    }

    @Override
    public byte[] getNodeAndListenChange(String node,final DataChangeListener listener) throws AccessorException{
        try{
            return consumer.getAddressAndListenChange(this.service,this.version,
                    node,listener == null ? null : new com.touclick.tcoa.framework.registry.find.consumer.DataChangeListener(){
                        @Override
                        public void dataChanged(byte []data){
                            listener.dataChanged(data);
                        }
                    });
        }catch (Exception e){
            throw AccessorException.valueOf(e);
        }
    }
}
