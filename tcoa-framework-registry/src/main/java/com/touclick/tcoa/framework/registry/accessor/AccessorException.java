package com.touclick.tcoa.framework.registry.accessor;

import com.touclick.tcoa.framework.registry.find.core.FindException;

public class AccessorException extends Exception {

	public static enum Code {
		NOAUTH, CONNECTIONLOSS, NONODE, OTHER
	}

	private static final long serialVersionUID = 110436013251266311L;

	private Code code;

	public Code getCode() {
		return code;
	}

	public AccessorException() {
		super();
	}

	public AccessorException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessorException(String message) {
		super(message);
	}

	public AccessorException(Throwable cause) {
		super(cause);
	}

	public AccessorException(Code code, String message, Exception e) {
		this(message, e);
		this.code = code;
	}

	public AccessorException(Code code, String message) {
		this(message);
		this.code = code;
	}

	public static AccessorException valueOf(Exception e) {
		AccessorException ze;
		if (e instanceof FindException) {
			FindException fe = (FindException) e;
			FindException.Code code = fe.getCode();
			switch (code) {
			case DISCONNECTED:
				ze = new AccessorException(Code.CONNECTIONLOSS, e.getMessage(), e);
				break;
			case NOAUTH:
				ze = new AccessorException(Code.NOAUTH, e.getMessage(), e);
				break;
			case NONODE:
				ze = new AccessorException(Code.NONODE, e.getMessage(), e);
				break;
			default:
				ze = new AccessorException(Code.OTHER, e.getMessage(), e);
				break;
			}
		} else {
			ze = new AccessorException(Code.OTHER, e.getMessage(), e);
		}
		
		return ze;
	}
}
