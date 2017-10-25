/**
 * @(#)AddressChangeListener.java, 2012-6-21. 
 * 
 * Copyright 2012 RenRen, Inc. All rights reserved.
 */
package com.touclick.tcoa.framework.registry.find.consumer;

import java.util.List;

/**
 * This class is for User to listen to the nodes(stat nodes list or address nodes list) changes.
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public interface NodeChangeListener {

    /**
     * Fire when nodes list changed.<p>
     * <B>Notice</B>: This method may be called concurrently, make sure you have add lock around code that should be executed sequentially.
     * @param nodeList The changed nodes List.
     */
    public void listChanged(List<String> nodeList);
}
