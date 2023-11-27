package org.cat.support.search3.generator.exception;

public class ArchSearch3Exception extends RuntimeException {

	private static final long serialVersionUID = 5575345549133880355L;

	public ArchSearch3Exception(String message, Throwable cause) {
		super(message, cause);
	}

	public ArchSearch3Exception(String message) {
		super(message);
	}
	
	public ArchSearch3Exception(String msgFormat, Object... args) {
        this(String.format(msgFormat, args));
    }

}
