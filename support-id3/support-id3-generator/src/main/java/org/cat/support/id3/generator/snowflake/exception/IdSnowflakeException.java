package org.cat.support.id3.generator.snowflake.exception;

public class IdSnowflakeException extends RuntimeException {

	private static final long serialVersionUID = 5575345549133880355L;

	public IdSnowflakeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IdSnowflakeException(String message) {
		super(message);
	}
	
	public IdSnowflakeException(String msgFormat, Object... args) {
        this(String.format(msgFormat, args));
    }

}
