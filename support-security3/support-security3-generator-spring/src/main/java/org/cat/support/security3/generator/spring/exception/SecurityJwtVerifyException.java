package org.cat.support.security3.generator.spring.exception;

public class SecurityJwtVerifyException extends RuntimeException {

	private static final long serialVersionUID = 101502350748935291L;

	public SecurityJwtVerifyException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecurityJwtVerifyException(String message) {
		super(message);
	}

}
