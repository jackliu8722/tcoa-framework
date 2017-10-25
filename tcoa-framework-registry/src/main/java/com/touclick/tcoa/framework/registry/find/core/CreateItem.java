/**
 * @(#)CreateItem.java, 2012-9-17. 
 * 
 * Copyright 2012 RenRen, Inc. All rights reserved.
 */
package com.touclick.tcoa.framework.registry.find.core;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

/**
 * The item to save create node information, used when reconnect.
 * 
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 *
 */
public class CreateItem {

    /**
     * The path to watch.
     */
    private String path;
    private CreateMode mode;
    private byte[] data;
    private Watcher watcher;

    /**
     * @param path
     * @param mode
     * @param data
     */
    public CreateItem(String path, CreateMode mode, byte[] data) {
        super();
        this.path = path;
        this.mode = mode;
        this.data = data;
    }
    
    public Watcher getWatcher() {
        return watcher;
    }
    
    public void setWatcher(Watcher watcher) {
        this.watcher = watcher;
    }
    
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    /**
     * @return the mode
     */
    public CreateMode getMode() {
        return mode;
    }
    /**
     * @param mode the mode to set
     */
    public void setMode(CreateMode mode) {
        this.mode = mode;
    }
    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }
    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }
    
}
