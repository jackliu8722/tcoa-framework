package com.touclick.tcoa.framework.server.core;

import com.touclick.tcoa.framework.server.exception.TcoaServerException;

/**
 * Server factory interface
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public interface IServerFactory {

    public void startServer(String serviceId,String version,boolean isRegistry) throws TcoaServerException;

    public void stopServer(String serviceId,String version) throws TcoaServerException;

    public void startAllServer() throws TcoaServerException;

    public void stopAllServer() throws TcoaServerException;
}
