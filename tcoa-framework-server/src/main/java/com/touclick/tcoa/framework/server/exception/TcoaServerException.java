package com.touclick.tcoa.framework.server.exception;

/**
 * Tcoa server exception
 *
 * @author bing.liu
 * @date 2015-08-17
 * @version 1.0
 */
public class TcoaServerException extends Exception{

    public TcoaServerException(String s){
        super(s);
    }

    public TcoaServerException(Throwable e){
        super(e);
    }

    public TcoaServerException(String s, Throwable e){
        super(s,e);
    }
}
