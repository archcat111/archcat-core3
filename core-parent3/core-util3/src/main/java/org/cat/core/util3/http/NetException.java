package org.cat.core.util3.http;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午2:17:30
 * @version 1.0
 * @description Net包相关RuntimeException
 *
 */
public class NetException extends RuntimeException {

	private static final long serialVersionUID = 4441296562456484636L;

	public NetException() {
		super();
	}

	public NetException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetException(String message) {
		super(message);
	}
	
}
