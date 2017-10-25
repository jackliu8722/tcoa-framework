package com.touclick.tcoa.framework.registry.accessor;

/**
 * The listener for the node datat changed.
 * 
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 *
 */
public interface DataChangeListener {
	/**
	 *
	 * @param data
	 */
	public void dataChanged(byte[] data);
}
