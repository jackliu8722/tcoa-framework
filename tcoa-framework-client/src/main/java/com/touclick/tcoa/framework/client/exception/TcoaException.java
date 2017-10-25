package com.touclick.tcoa.framework.client.exception;

/**
 * Thrift exception
 *
 * @author bing.liu
 * @date 2015-08-05
 * @version 1.0
 */
public class TcoaException extends Exception{

    public TcoaException(String s){
        super(s);
    }

    public TcoaException(Throwable e){
        super(e);
    }

    public TcoaException(String s, Throwable e){
        super(s,e);
    }
}
