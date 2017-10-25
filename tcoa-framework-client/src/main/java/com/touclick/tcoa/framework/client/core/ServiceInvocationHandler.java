package com.touclick.tcoa.framework.client.core;

import com.touclick.tcoa.framework.client.core.definition.ClassDefinition;
import com.touclick.tcoa.framework.client.core.definition.MethodDefinition;
import com.touclick.tcoa.framework.client.exception.TcoaException;
import com.touclick.tcoa.framework.client.router.ServiceRouter;
import com.touclick.tcoa.framework.client.transport.TcoaTransport;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service InvocationHandler implementation
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public class ServiceInvocationHandler implements InvocationHandler{
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInvocationHandler.class);

    private ClassDefinition serviceDefinition;

    private ConcurrentMap<Method,MethodDefinition> methodCache =
            new ConcurrentHashMap<Method, MethodDefinition>();

    private ServiceRouter serviceRouter;

    private long timeout;

    public ClassDefinition getServiceDefinition(){
        return serviceDefinition;
    }

    public ConcurrentMap<Method,MethodDefinition> getMethodCache(){
        return methodCache;
    }

    public ServiceRouter getServiceRouter(){
        return serviceRouter;
    }

    public ServiceInvocationHandler(ServiceRouter serviceRouter,
                                    ClassDefinition serviceDefinition,long timeout){
        if(serviceDefinition == null || serviceRouter == null){
            throw new NullPointerException();
        }

        this.serviceRouter = serviceRouter;
        this.serviceDefinition = serviceDefinition;
        this.timeout = timeout;

        /** Init load the service node */
        serviceRouter.initLoadNodes(serviceDefinition.getServiceId(),
                serviceDefinition.getVersion());
    }

    public final Object invoke(Object proxy,Method method,Object[] args) throws Throwable{
        return round(proxy,method,args);
    }

    protected Object round(Object proxy,Method method,Object[] args) throws Throwable{
        try{
            beforeInvoke(proxy,method,args);
            return doInvoke(proxy,method,args);
        }finally {
            afterInvoke(proxy,method,args);
        }
    }

    protected void beforeInvoke(Object proxy,Method method,Object[] args){

    }

    protected  void afterInvoke(Object proxy,Method method,Object[] args){

    }

    protected final Object doInvoke(Object proxy,Method method,Object[] args)
        throws TcoaException,Exception{

        String serviceId = serviceDefinition.getServiceId();
        String version = serviceDefinition.getVersion();

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Invoke service: " +  serviceId + ", version: " + version +
                ", method: " + method + ", args: " + Arrays.toString(args));
        }

        TcoaTransport tcoaTransport = null;

        try{
            tcoaTransport = serviceRouter.routeService(serviceId,version,timeout);
        }catch (Exception e){
            throw new TcoaException("Failed to route " + serviceId + " version " + version,e);
        }

        if(tcoaTransport == null){
            throw  new TcoaException("No transport available for " + serviceId + " version " + version);
        }

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Route transport succ at Node:" + tcoaTransport.getServiceNode());
        }

        /** Service return */
        Object result = null;
        Throwable seviceException = null;
        Throwable frameworkException = null;
        try{
            TProtocol protocol = new TBinaryProtocol(tcoaTransport.getTransport());

            Object client=  serviceDefinition.getServiceClientConstructor().newInstance(protocol);

            result = getRealMethod(method).getMethod().invoke(client,args);

            return result;
        }catch (InvocationTargetException e){
            Throwable cause = e.getCause();
            if(cause instanceof org.apache.thrift.TBase<?,?>){
                seviceException = cause;
                return (Exception)cause;
            }else{
                if(cause instanceof org.apache.thrift.TException){
                    frameworkException = cause;
                    if(cause instanceof TTransportException){
                        throw new TcoaException(cause);
                    }
                }
                throw new TcoaException(e);
            }
        }catch (Exception e){
            frameworkException = e;
            throw new TcoaException(e);
        }finally {
            if(frameworkException != null){
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("Occur framework exception: " + frameworkException,frameworkException);
                }

                serviceRouter.serviceException(serviceId,version,frameworkException,tcoaTransport);
            }else{
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("Return value: " + result + ", exception: " + seviceException,seviceException);
                }

                serviceRouter.returnConn(tcoaTransport);
            }
        }
    }

    /**
     * Get real method
     * @param method
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    private MethodDefinition getRealMethod(Method method) throws SecurityException,NoSuchMethodException{

        MethodDefinition methodDefinition = methodCache.get(method);

        if(methodDefinition != null){
            return methodDefinition;
        }

        Method realMethod = serviceDefinition.getServiceClientClass().getMethod(method.getName(),
                            method.getParameterTypes());

        methodDefinition = new MethodDefinition(realMethod);

        methodCache.put(method,methodDefinition);

        return methodDefinition;
    }
}
