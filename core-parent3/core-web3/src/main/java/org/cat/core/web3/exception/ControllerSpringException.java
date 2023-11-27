package org.cat.core.web3.exception;

/**
 * 
 * @author 王云龙
 * @date 2021年8月31日 下午6:45:32
 * @version 1.0
 * @description ControllerSpringUtil中抛出的运行时异常
 *
 */
public class ControllerSpringException extends RuntimeException {

	private static final long serialVersionUID = 446086771355207332L;

	public ControllerSpringException() {
		super();
	}

	public ControllerSpringException(String message, Throwable cause) {
		super(message, cause);
	}

	public ControllerSpringException(String message) {
		super(message);
	}
	
}
