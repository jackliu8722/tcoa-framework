package com.touclick.tcoa.framework.client.transport;

import com.touclick.tcoa.framework.client.exception.TcoaException;
import com.touclick.tcoa.framework.registry.Node;
import org.apache.thrift.transport.TTransport;

/**
 * Thrift connection provider
 *
 * @author bing.liu
 * @version 1.0
 */
public interface ConnectionProvider {

    /**
     *
     * @param node
     * @param conTimeOut
     * @return
     * @throws TcoaException
     */
    public TTransport getConnection(Node node,long conTimeOut) throws TcoaException;

    /**
     * Return the connection
     * @param transport
     * @throws TcoaException
     */
    public void returnConnection(TcoaTransport transport) throws TcoaException;

    /**
     * Invalidate the connection
     * @param tcoaTransport
     */
    public void invalidateConnection(TcoaTransport tcoaTransport);

    /**
     * Clear the connection given service node
     * @param node
     */
    public void clearConnections(Node node);
}
