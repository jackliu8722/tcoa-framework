package com.touclick.tcoa.framework.registry;

/**
 * Node info
 *
 * @author bing.liu
 * @date 2015-08-13
 * @version 1.0
 */
public final class Node {

    /** host */
    private String host;

    /** port */
    private int port;

    /** Weather the node is enable */
    private boolean disabled = false;

    /** Weather the node is health */
    private boolean healthy = true;

    Node(String host,int port,boolean disabled,boolean healthy){
        super();
        this.host = host;
        this.port = port;
        this.disabled = disabled;
        this.healthy = healthy;
    }

    @Override
    public String toString(){
        return "Node [host=" + host + ",port=" + port +
                ",disable=" + disabled + ",healthy=" + healthy + "]";
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        Node other = (Node)obj;
        if(host == null){
            if(other.host != null){
                return false;
            }
        }else if(!host.equals(other.host)){
            return false;
        }
        if(port != other.port){
            return false;
        }
        return true;
    }

    public int compareTo(Node o){
        int cmp = this.host.compareTo(o.host);
        if(cmp != 0){
            return cmp;
        }

        if(this.port < o.port){
            return -1;
        }else if(this.port > o.port){
            return 1;
        }

        return 0;
    }

    /**
     * Get the node key
     * @return
     */
    public String getNodeKey(){
        return host + ":" + port;
    }

    /**
     * Parse the node key
     * @param nodeKey
     * @return
     */
    public static String[] parseNodeKey(String nodeKey){
        return nodeKey.split(":",2);
    }

    /**
     * Generate node key
     * @param host
     * @param port
     * @return
     */
    public static String generateNodeKey(String host,int port){
        return host + ":" + port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }
}
