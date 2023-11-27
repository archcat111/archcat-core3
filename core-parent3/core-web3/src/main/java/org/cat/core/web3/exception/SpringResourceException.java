package org.cat.core.web3.exception;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午12:32:27
 * @version 1.0
 * @description Spring Resource相关异常
 *
 */
public class SpringResourceException extends RuntimeException {

	private static final long serialVersionUID = 2731045212629895939L;

	public SpringResourceException() {
		super();
	}

	public SpringResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public SpringResourceException(String message) {
		super(message);
	}
	
	

}
