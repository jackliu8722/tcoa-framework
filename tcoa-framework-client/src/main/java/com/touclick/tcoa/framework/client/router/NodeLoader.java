package com.touclick.tcoa.framework.client.router;

import com.touclick.tcoa.framework.registry.Node;

import java.util.List;

/**
 * Node loader for loading the nodes
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 */
public interface NodeLoader {

    /**
     * Load the nodes by serviceid and version
     * @param serviceId
     * @param version
     * @return
     */
    public List<Node> load(String serviceId,String version);


}
