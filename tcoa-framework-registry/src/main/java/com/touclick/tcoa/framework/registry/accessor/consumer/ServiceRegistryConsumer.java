package com.touclick.tcoa.framework.registry.accessor.consumer;

import com.touclick.tcoa.framework.registry.accessor.*;
import com.touclick.tcoa.framework.registry.find.consumer.*;
import com.touclick.tcoa.framework.registry.find.consumer.DataChangeListener;

import java.util.List;

/**
 * Service registry accessor interface
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public interface ServiceRegistryConsumer {
    /**
     * Get service id
     * @return
     */
    public String getService();

    /**
     * Get service version
     * @return
     */
    public String getVersion();

    /**
     * Get all the stat of current version and listen changed.
     * @param listener
     * @return
     * @throws AccessorException
     */
    public List<String> listStatesAndListenChange(ChildrenChangeListener listener) throws AccessorException;

    /**
     * Get the given state and listen changed.
     * @param state
     * @param listener
     * @return
     * @throws AccessorException
     */
    public byte[] getStateAndListenChange(String state, com.touclick.tcoa.framework.registry.accessor.DataChangeListener listener) throws AccessorException;

    /**
     * Get all nodes at current state and listen changed.
     * @param listener
     * @return
     * @throws AccessorException
     */
    public List<String> listNodesAndListenChange(ChildrenChangeListener listener) throws AccessorException;

    /**
     * Get node data and listen changed.
     * @param node
     * @param listener
     * @return
     * @throws AccessorException
     */
    public byte[] getNodeAndListenChange(String node, com.touclick.tcoa.framework.registry.accessor.DataChangeListener listener) throws AccessorException;

}
