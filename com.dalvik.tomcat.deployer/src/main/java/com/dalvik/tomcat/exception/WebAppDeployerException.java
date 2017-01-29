package com.dalvik.tomcat.exception;

public class WebAppDeployerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WebAppDeployerException(String message) {
		super(message);
	}

	public WebAppDeployerException(String message, Throwable cause) {
		super(message, cause);
	}
}
