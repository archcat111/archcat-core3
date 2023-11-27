package org.cat.core.web3.exception;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 上午10:52:33
 * @version 1.0
 * @description Resp相关异常
 *
 */
public class RespException extends RuntimeException {

	private static final long serialVersionUID = 3047534587809464240L;

	public RespException() {
		super();
	}

	public RespException(String message, Throwable cause) {
		super(message, cause);
	}

	public RespException(String message) {
		super(message);
	}

}
