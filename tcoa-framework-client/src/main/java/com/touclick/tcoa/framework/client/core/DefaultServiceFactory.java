package com.touclick.tcoa.framework.client.core;

import com.touclick.tcoa.framework.client.core.definition.ClassDefinition;
import com.touclick.tcoa.framework.client.router.CommonServiceRouter;

import java.lang.reflect.InvocationHandler;

/**
 * The default service factory implement for creating Service instance
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public class DefaultServiceFactory extends AbstractServiceFactory{

    public DefaultServiceFactory(){
        super(CommonServiceRouter.getInstance());
    }

    @Override
    protected InvocationHandler createInocationHandler(ClassDefinition serviceDefinition,long timeout){
        return new ServiceInvocationHandler(getServiceRouter(),serviceDefinition,timeout);
    }
}
