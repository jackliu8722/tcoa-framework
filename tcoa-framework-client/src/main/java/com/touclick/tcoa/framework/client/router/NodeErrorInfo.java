package com.touclick.tcoa.framework.client.router;

import com.touclick.tcoa.framework.registry.Node;

/**
 *Node error info
 *
 * @author bing.liu
 * @date 2015-08-13
 * @version 1.0
 */
public class NodeErrorInfo {
	
	private String serviceId;

    private String version;
    
    private int count;

    private Node node;

	NodeErrorInfo(String serviceId, String version, Node node, int count) {
	    this.serviceId = serviceId;
	    this.version = version;
	    this.count = count;
	    this.node = node;
	}
	
	void addCount(int delta) {
	    count += delta;
	}
	
	String getServiceId() {
	    return serviceId;
	}
	
	String getVersion(){
		return version;
	}
	Node getNode() {
	    return node;
	}
	
	/**
     * @param node the node to set
     */
    public void setNode(Node node) {
        this.node = node;
    }

    int getCount() {
	    return count;
	}
	
	void setCount(int count) {
	    this.count = count;
	}
}
