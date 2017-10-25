/**
 * @(#)WatcherItem.java, 2012-6-21. Copyright 2012 RenRen, Inc. All rights
 *                       reserved.
 */
package com.touclick.tcoa.framework.registry.find.core;

import org.apache.zookeeper.Watcher;

/**
 * The item to save watcher information, used when reconnect.
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public class WatcherItem {

    /**
     * The path to watch.
     */
    String path;

    private Watcher wat;

    private boolean isChildren;
    
    private int failedTimes;

    /**
     * @param path
     * @param wat
     * @param isChildren
     */
    public WatcherItem(String path, Watcher wat, boolean isChildren) {
        super();
        this.path = path;
        this.wat = wat;
        this.isChildren = isChildren;
        this.failedTimes = 0;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the wat
     */
    public Watcher getWat() {
        return wat;
    }

    /**
     * @return the isChildren
     */
    public boolean isChildren() {
        return isChildren;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isChildren ? 1231 : 1237);
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((wat == null) ? 0 : wat.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj instanceof WatcherItem) {
	        WatcherItem other = (WatcherItem) obj;
	        return other.isChildren == this.isChildren && 
	            this.wat.equals(other.wat) && path.equals(other.path);
	    } else {
	        return false;	        
	    }
	}

    public int getFailedTimes() {
        return failedTimes;
    }

    public void setFailedTimes(int failedTimes) {
        this.failedTimes = failedTimes;
    }
}
