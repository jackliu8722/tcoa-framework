package com.touclick.tcoa.framework.client.router;

import com.touclick.tcoa.framework.client.exception.TcoaException;
import com.touclick.tcoa.framework.registry.Node;

import java.util.List;

/**
 * Service load balance
 *
 * @author bing.liu
 * @date 2015-08-13
 * @version 1.0
 */
public interface LoadBalancer {
    /**
     *
     * @param servideId
     * @param nodes
     * @return
     */
    public Node getNode(String servideId,String version,List<Node> nodes) throws TcoaException;
}
