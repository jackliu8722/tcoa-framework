package com.touclick.tcoa.framework.registry;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Node factory which manage the node
 *
 * @author bing.liu
 * @date 2015-08-13
 * @version 1.0
 */
public class NodeFactory {

    /** Node map */
    private static final ConcurrentHashMap<String,Node> nodeMap = new ConcurrentHashMap<String, Node>();

    /**
     * Get global standalone Node object for nodekey
     * @param nodeKey
     * @return
     */
    public static Node getNode(String nodeKey){
        return getNode(nodeKey,false,true);
    }

    /**
     * Get global Node object for nodeKey
     * @param nodeKey
     * @param disable
     * @param healthy
     * @return
     */
    public static Node getNode(String nodeKey,boolean disable,boolean healthy){
        String[] ipPort = Node.parseNodeKey(nodeKey);
        return getNode(ipPort[0],Integer.parseInt(ipPort[1]),disable,healthy);
    }

    /**
     * Get global Node object for nodekey
     * @param host
     * @param port
     * @param disable
     * @param healthy
     * @return
     */
    public static Node getNode(String host,int port,boolean disable,boolean healthy){
        String nodeKey = Node.generateNodeKey(host,port);
        Node node = nodeMap.get(nodeKey);

        if(node == null){
            nodeMap.putIfAbsent(nodeKey,new Node(host,port,disable,healthy));
            node = nodeMap.get(nodeKey);
        }
        return node;
    }
}
