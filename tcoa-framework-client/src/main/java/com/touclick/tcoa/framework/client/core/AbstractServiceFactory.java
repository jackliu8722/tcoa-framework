package com.touclick.tcoa.framework.client.core;

import com.touclick.tcoa.framework.client.core.definition.ClassDefinition;
import com.touclick.tcoa.framework.client.exception.TcoaException;
import com.touclick.tcoa.framework.client.router.ServiceRouter;
import com.touclick.tcoa.framework.client.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Abstract service factory
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public abstract class AbstractServiceFactory implements IServiceFactory{

    private ConcurrentMap<Class<?>,Object> serviceCache =
            new ConcurrentHashMap<Class<?>, Object>();

    private ServiceRouter serviceRouter;

    public AbstractServiceFactory(ServiceRouter serviceRouter){
        if(serviceRouter == null){
            throw new NullPointerException("ServiceRouter is null.");
        }

        this.serviceRouter = serviceRouter;
    }

    protected ServiceRouter getServiceRouter(){
        return serviceRouter;
    }

    public <T> T getService(Class<T> serviceInterface) throws TcoaException{
        return getService(serviceInterface,250);
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceInterface,long timeout) throws TcoaException{
        Object serviceInstance = serviceCache.get(serviceInterface);
        if(serviceInstance != null){
            return (T) serviceInstance;
        }

        try{
            ClassDefinition seviceDefinition = new ClassDefinition(serviceInterface);
            InvocationHandler handler = createInocationHandler(seviceDefinition,timeout);

            T proxy = (T) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(),
                        new Class<?>[] {serviceInterface},handler);

            Object tmp = serviceCache.putIfAbsent(serviceInterface,proxy);

            if(tmp != null){
                return (T) tmp;
            }

            return proxy;
        }catch (ClassNotFoundException e){
            throw new TcoaException(e);
        }catch (NoSuchMethodException e){
            throw new TcoaException(e);
        }
    }

    protected abstract InvocationHandler createInocationHandler(ClassDefinition serviceDefinition,long timeout);
}
