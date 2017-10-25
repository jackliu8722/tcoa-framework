package com.touclick.tcoa.framework.client.core;

import com.touclick.tcoa.framework.client.exception.TcoaException;

/**
 * Service factory interface
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public interface IServiceFactory {

    public <T> T getService(Class<T> serviceInterface) throws TcoaException;

    public <T> T getService(Class<T> serviceInterface,long timeout) throws TcoaException;
}
