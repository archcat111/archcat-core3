package org.cat.support.security3.generator.spring.exception;

public class SecurityAuthException extends RuntimeException {

	private static final long serialVersionUID = 101502350748935291L;

	public SecurityAuthException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecurityAuthException(String message) {
		super(message);
	}

}
