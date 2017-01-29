package com.dalvik.utils.json;

/**
 *
 */
public class SerializationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerializationException(String message, Throwable cause) {
		super(message, cause);

	}

	public SerializationException(String message) {
		super(message);

	}

	public SerializationException(Throwable cause) {
		super(cause);

	}

}
