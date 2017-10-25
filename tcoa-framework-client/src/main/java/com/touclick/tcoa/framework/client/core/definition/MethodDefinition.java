package com.touclick.tcoa.framework.client.core.definition;

import java.lang.reflect.Method;

/**
 * Method definition
 *
 * @author bing.liu
 * @date 2015-08-15
 * @version 1.0
 */
public class MethodDefinition {

    private Method method;

    public MethodDefinition(Method method){
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
