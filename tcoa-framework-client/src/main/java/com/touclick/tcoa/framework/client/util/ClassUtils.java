package com.touclick.tcoa.framework.client.util;

/**
 * Class utils
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public class ClassUtils {

    /**
     * Return the default classloader to use
     * @return
     */
    public static ClassLoader getDefaultClassLoader(){
        ClassLoader cl = null;

        try{
            cl = Thread.currentThread().getContextClassLoader();
        }catch (Throwable ex){

        }

        if(cl == null){
            cl = ClassUtils.class.getClassLoader();
        }

        return cl;
    }
}
