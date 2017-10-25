/**
 * @(#)FindException.java, 2012-6-21.
 *
 * Copyright 2012 RenRen, Inc. All rights reserved.
 */
package com.touclick.tcoa.framework.registry.find.core;

import org.apache.zookeeper.KeeperException;

/**
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 *
 */
public class FindException extends RuntimeException {

    public static enum Code {
        INTERRUPT, NOAUTH, DISCONNECTED, OTHER, SYSTEMERROR, NONODE
    }

    /**
     * Version UID
     */
    private static final long serialVersionUID = 6217415731238015041L;

    private Code code;

    /**
     * @param message
     * @param cause
     */
    public FindException(String message, Throwable cause, Code code) {
        super(message, cause);
        this.code = code;
    }

    /**
     * @param message
     */
    public FindException(String message, Code code) {
        super(message);
        this.code = code;
    }

    public static final FindException makeInstance(String message, Throwable e) {
        Code code = Code.OTHER;
        if (e instanceof KeeperException) {
            KeeperException ke = (KeeperException) e;
            switch (ke.code()) {
                case NOAUTH:
                case AUTHFAILED:
                    code = Code.NOAUTH;
                    break;
                case SYSTEMERROR:
                    code = Code.SYSTEMERROR;
                    break;
                case CONNECTIONLOSS:
                    code = Code.DISCONNECTED;
                    break;
                case NONODE:
                    code = Code.NONODE;
                    break;
            }
        } else if (e instanceof InterruptedException) {
            code = Code.INTERRUPT;
        }
        return new FindException(message, e, code);
    }

    /**
     * @return the code
     */
    public Code getCode() {
        return code;
    }

	@Override
	public String getMessage() {
		return super.getMessage() + " " + code;
	}

}
