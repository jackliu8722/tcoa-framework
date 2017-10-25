package com.touclick.tcoa.framework.client.router;

import com.touclick.tcoa.framework.client.transport.TcoaTransport;

/**
 * Service router
 *
 * @author bing.liu
 * @date 2015-08-13
 * @version 1.0
 */
public interface ServiceRouter {

    /**
     * Route the given service
     * @param serviceId
     * @param version
     * @param timeout
     * @return
     * @throws Exception
     */
    public TcoaTransport routeService(String serviceId,String version,
                                      long timeout) throws Exception;

    /**
     * Return the given connection
     * @param transport
     * @throws Exception
     */
    public void returnConn(TcoaTransport transport) throws Exception;

    /**
     *
     * @param serviceId
     * @param version
     * @param e
     * @param transport
     */
    public void serviceException(String serviceId,String version,
                                 Throwable e,TcoaTransport transport);

    /**
     * Init loading the nodes from the registry by given serviceId and version
     * @param serviceId
     * @param version
     */
    public void initLoadNodes(String serviceId,String version);
}
