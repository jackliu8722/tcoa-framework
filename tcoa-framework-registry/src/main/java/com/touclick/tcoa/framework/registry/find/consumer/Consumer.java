package com.touclick.tcoa.framework.registry.find.consumer;

import com.touclick.tcoa.framework.registry.find.core.FindException;
import com.touclick.tcoa.framework.registry.find.core.ZKConnector;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * The consumer interface
 *
 * @author bing.liu
 * @date 2015-10-24
 * @version 1.0
 */
public interface Consumer{
    /**
     * Get all the stats and listener to chagnes
     * @param service
     * @param version
     * @param listener
     * @return
     */
    public List<String> getAllStatAndListenChange(String service,String version,NodeChangeListener listener);


    /**
     * Get the meta data under the version node and listen changes
     * @param service
     * @param version
     * @param listener
     * @return
     */
    public byte[] getMetaDataAndListenChange(String service,String version,DataChangeListener listener);

    /**
     * Get address and listen changes.
     * @param service
     * @param version
     * @param listener
     * @return
     */
    public List<String> getAddressAndListenChange(String service,String version,
                                                  NodeChangeListener listener);

    /**
     * Get the node data by given service
     * @param service
     * @param version
     * @param node
     * @param listener
     * @return
     */
    public byte[] getAddressAndListenChange(String service,String version,
                                            String node, DataChangeListener listener);
}
