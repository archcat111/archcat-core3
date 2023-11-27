package org.cat.support.id3.generator.pool.exception;

public class IdPoolException extends RuntimeException {

	private static final long serialVersionUID = 8610799337978594821L;

	public IdPoolException(String message, Throwable cause) {
		super(message, cause);
	}

	public IdPoolException(String message) {
		super(message);
	}

}
