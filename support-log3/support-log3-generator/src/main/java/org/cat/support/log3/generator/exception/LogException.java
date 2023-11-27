package org.cat.support.log3.generator.exception;

public class LogException extends RuntimeException {

	private static final long serialVersionUID = -6154378964327597725L;

	public LogException(String message, Throwable cause) {
		super(message, cause);
	}

	public LogException(String message) {
		super(message);
	}

}
