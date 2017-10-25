package com.touclick.tcoa.framework.server;

import com.touclick.tcoa.framework.server.core.TcoaServerFactory;
import com.touclick.tcoa.framework.server.exception.TcoaServerException;

/**
 * Server factory
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public class ServerFactory {

    public static void startServer(String serviceId,String version,boolean isRegistry) throws TcoaServerException{
        TcoaServerFactory.getInstance().startServer(serviceId,version,isRegistry);
    }

    public static void startServer(String serviceId,String version) throws TcoaServerException{
        startServer(serviceId,version,true);
    }
    public static void stopServer(String serviceId,String version) throws TcoaServerException{
        TcoaServerFactory.getInstance().stopServer(serviceId, version);
    }

    public static void startAllServer() throws TcoaServerException{
        TcoaServerFactory.getInstance().startAllServer();
    }

    public static void stopAllServer() throws TcoaServerException{
        TcoaServerFactory.getInstance().stopAllServer();
    }
}
