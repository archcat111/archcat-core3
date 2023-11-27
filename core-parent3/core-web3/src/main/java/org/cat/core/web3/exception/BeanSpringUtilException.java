package org.cat.core.web3.exception;

/**
 * 
 * @author 王云龙
 * @date 2021年8月31日 下午5:31:29
 * @version 1.0
 * @description BeanSpringUtil中抛出的运行时异常
 *
 */
public class BeanSpringUtilException extends RuntimeException {

	private static final long serialVersionUID = -6659579461533949172L;

	public BeanSpringUtilException() {
		super();
	}

	public BeanSpringUtilException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeanSpringUtilException(String message) {
		super(message);
	}
	
}
