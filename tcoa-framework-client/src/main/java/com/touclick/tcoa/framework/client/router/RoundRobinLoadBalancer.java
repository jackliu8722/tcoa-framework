package com.touclick.tcoa.framework.client.router;

import com.touclick.tcoa.framework.client.exception.TcoaException;
import com.touclick.tcoa.framework.registry.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The round robin load balance
 */
public class RoundRobinLoadBalancer implements LoadBalancer{

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoundRobinLoadBalancer.class);

    private Map<String,AtomicInteger> counterMap = new ConcurrentHashMap<String,AtomicInteger>();

    @Override
    public Node getNode(String serviceId,String version,List<Node> nodes)
        throws TcoaException{

        String key = constructNodeKey(serviceId,version);

        AtomicInteger counter = counterMap.get(key);

        if(counter == null){
            synchronized (key.intern()){
                counter = counterMap.get(key);
                if(counter == null){
                    counter = new AtomicInteger();
                    counterMap.put(key,counter);
                }
            }
        }

        if(counter.get() > Integer.MAX_VALUE - 1000000){
            counter.set(0);
        }

        Node node = null;
        int steps = 0;
        int index = counter.getAndIncrement() % nodes.size();

        while(steps ++ < nodes.size()){
            node = nodes.get((index++) % nodes.size());
            if(!node.isDisabled() && node.isHealthy()){
                return node;
            }

            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("node " + node.getHost() + ":" + node.getPort()
                    + " neglected, distable=" + node.isDisabled() + ", healthy=" + node.isHealthy());
            }
        }
        LOGGER.error("Node available node for service : " + serviceId + ", version=" +
            version + ", service status : " + nodes);

        throw new TcoaException("Node avaliable node for service : " + serviceId);
    }

    private String constructNodeKey(String serviceId,String version){
        return serviceId + "-" + version;
    }
}
