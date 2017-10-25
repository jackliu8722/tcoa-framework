package com.touclick.tcoa.framework.registry.find.consumer;

import com.touclick.tcoa.framework.registry.find.core.FindException;
import com.touclick.tcoa.framework.registry.find.core.ZKConnector;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * This is the main class clients used to get servie address list
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public class ZKConsumer extends ZKConnector implements Consumer{

    public ZKConsumer(String clusterAddress, int sessionTimeout) throws IOException{
        super(clusterAddress,sessionTimeout);
    }

    /**
     * Get all the stats and listener to chagnes
     * @param service
     * @param version
     * @param listener
     * @return
     */
    @Override
    public List<String> getAllStatAndListenChange(String service,String version,NodeChangeListener listener){
        StringBuilder path = new StringBuilder();
        path.append("/").append(root).append("/").append(service).append("/").append(version);

        try{
            LOGGER.info("Try to watch children of : " + path);
            List<String> list = (List<String>)this.submitWatcher(path.toString(),
                    new NodeWatcher(listener),true);
            return list;
        }catch (Exception e){
            if(e instanceof RuntimeException){
                throw (RuntimeException)e;
            }

            throw FindException.makeInstance("Failed to get Stat list.",e);
        }
    }

    /**
     * Get the meta data under the version node and listen changes
     * @param service
     * @param version
     * @param listener
     * @return
     */
    @Override
    public byte[] getMetaDataAndListenChange(String service,String version,DataChangeListener listener){
        StringBuilder path = new StringBuilder();
        path.append("/").append(root).append("/").append(service).append("/").append(version);

        try{
            LOGGER.info("Try to watch data of: " + path);
            return (byte[]) this.submitWatcher(path.toString(),
                    new DataWatcher(listener),false);
        }catch (Exception e){
            if(e instanceof  RuntimeException){
                throw (RuntimeException)e;
            }

            throw FindException.makeInstance("Failed to get meta data.",e);
        }
    }

    /**
     * Get address and listen changes.
     * @param service
     * @param version
     * @param listener
     * @return
     */
    @Override
    public List<String> getAddressAndListenChange(String service,String version,
                                                  NodeChangeListener listener){
        StringBuilder path = new StringBuilder();
        path.append("/").append(root).append("/").append(service).append("/").append(version);

        try{
            LOGGER.info("Try to watch children of: " + path);
            List<String> list = (List<String>) this.submitWatcher(path.toString(),
                    new NodeWatcher(listener),true);
            return list;
        }catch (Exception e){
            if(e instanceof RuntimeException){
                throw  (RuntimeException) e;
            }
            throw FindException.makeInstance("Failed to get address List.",e);
        }
    }

    /**
     * Get the node data by given service
     * @param service
     * @param version
     * @param node
     * @param listener
     * @return
     */
    @Override
    public byte[] getAddressAndListenChange(String service,String version,
                                            String node, DataChangeListener listener){
        StringBuilder path = new StringBuilder();
        path.append("/").append(root).append("/").append(service);
        path.append("/").append(version).append("/").append(node);

        try{
            LOGGER.info("Try to watch data of: " + path.toString());

            return (byte[]) this.submitWatcher(path.toString(),new DataWatcher(listener),false);
        }catch (Exception e){
            if(e instanceof RuntimeException){
                throw (RuntimeException)e;
            }

            throw FindException.makeInstance("Failed to get data.",e);
        }
    }
    /**
     * The class of the node change watcher
     */
    protected class NodeWatcher implements Watcher{
        private NodeChangeListener listener;

        public NodeWatcher(NodeChangeListener listener){
            this.listener = listener;
        }

        @Override
        public void process(WatchedEvent event){
            if(event.getType() != Event.EventType.NodeChildrenChanged){
                return ;
            }
            try{
                List<String> list = zk.getChildren(event.getPath(),this);
                listener.listChanged(list);
            }catch (Exception e){
                LOGGER.error("Falied to do Watch, path=" + event.getPath(),e);
                throw new RuntimeException("Failed to do watch.",e);
            }
        }
    }

    /**
     * The class of the data watcher
     */
    protected class DataWatcher implements Watcher{
        private DataChangeListener listener;

        public DataWatcher(DataChangeListener listener){
            this.listener = listener;
        }

        @Override
        public void process(WatchedEvent event){
            if(event.getType() == Event.EventType.NodeDeleted){
                deleteWatcher(event.getPath(),this,false);
            }

            if(event.getType() != Event.EventType.NodeDataChanged){
                return ;
            }

            try{
                Stat st = new Stat();

                byte[] data = zk.getData(event.getPath(),this,st);

                listener.dataChanged(data);
            }catch (Exception e){
                LOGGER.error("Falied to do watch, path=" + event.getPath(),e);
                throw new RuntimeException("Failed to do watch.",e);
            }
        }
    }
}
