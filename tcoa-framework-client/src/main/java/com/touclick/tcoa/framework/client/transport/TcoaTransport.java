package com.touclick.tcoa.framework.client.transport;

import com.touclick.tcoa.framework.registry.Node;
import org.apache.thrift.transport.TTransport;

/**
 * @author bing.liu
 * @date 2015-08-05
 * @version 1.0
 */
public class TcoaTransport {

    /**
     * Transport
     */
    private TTransport transport;

    /**
     * Service node
     */
    private Node serviceNode;

    /**
     * disable status
     */
    private boolean disabled = false;

    public TcoaTransport(){

    }

    public TcoaTransport(TTransport transport,Node node){
        this.transport = transport;
        this.serviceNode = node;
    }

    public TTransport getTransport() {
        return transport;
    }

    public void setTransport(TTransport transport) {
        this.transport = transport;
    }

    public Node getServiceNode() {
        return serviceNode;
    }

    public void setServiceNode(Node serviceNode) {
        this.serviceNode = serviceNode;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
