package com.touclick.tcoa.framework.client;

import com.touclick.tcoa.framework.client.core.DefaultServiceFactory;
import com.touclick.tcoa.framework.client.core.IServiceFactory;

/**
 * Service factory
 *
 * @author bing.liu
 * @date 205-08-17
 * @version 1.0
 */
public class ServiceFactory {

    private static IServiceFactory factory = new DefaultServiceFactory();

    protected  static void setFactory(IServiceFactory factory){
        ServiceFactory.factory = factory;
    }

    public static <T> T getService(Class<T> serviceClass) throws Exception{
        return getService(serviceClass,250);
    }

    public static <T> T getService(Class<T> serviceClass,int timeout) throws Exception{
        return factory.getService(serviceClass,timeout);
    }
}
