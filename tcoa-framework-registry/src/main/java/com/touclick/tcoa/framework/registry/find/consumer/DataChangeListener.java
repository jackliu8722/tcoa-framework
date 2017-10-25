/**
 * @(#)DataChangeListener.java, 2012-8-8. 
 * 
 * Copyright 2012 RenRen, Inc. All rights reserved.
 */
package com.touclick.tcoa.framework.registry.find.consumer;

/**
 * This class is for User to listen to the node data(content) changes.
 * 
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 *
 */
public interface DataChangeListener {

    /**
     * Fire when node data changed.<p>
     * <B>Notice</B>: This method may be called concurrently, make sure you have add lock around code that should be executed sequentially.
     * @param data The changed data
     */
    public void dataChanged(byte[] data);
}
