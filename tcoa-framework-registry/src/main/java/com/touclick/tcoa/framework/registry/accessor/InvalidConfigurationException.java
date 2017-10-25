package com.touclick.tcoa.framework.registry.accessor;

/**
 * The exception when load configuation error.
 *
 * @author bing.liu
 * @date 2015-08-14
 * @version 1.0
 *
 */
public class InvalidConfigurationException extends Exception{

	private static final long serialVersionUID = -3402536374224209460L;

	public InvalidConfigurationException(String message, Exception e) {
		super(message, e);
	}

	public InvalidConfigurationException(String message) {
		super(message);
	}

}
