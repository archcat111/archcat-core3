package org.cat.support.security3.generator.spring.exception;

public class SecurityLogoutException extends RuntimeException {

	private static final long serialVersionUID = 101502350748935291L;

	public SecurityLogoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecurityLogoutException(String message) {
		super(message);
	}

}
