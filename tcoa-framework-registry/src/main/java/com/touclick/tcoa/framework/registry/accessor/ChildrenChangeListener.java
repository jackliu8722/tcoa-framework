package com.touclick.tcoa.framework.registry.accessor;

import java.util.List;

/**
 * The listener for the node children changed.
 * 
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public interface ChildrenChangeListener {

	/**
	 *
	 * @param children
	 */
	public void childrenChanged(List<String> children);
}
