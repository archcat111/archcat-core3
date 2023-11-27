package org.cat.core.exception3.exception;

import org.cat.core.exception3.bean.ExceptionBean;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午3:20:22
 * @version 1.0
 * @description 自定义标准异常本身发生的异常
 *
 */
public class StandardRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 6323747108059278671L;
	
	@Setter @Getter 
	private Throwable throwable; //原始异常
	@Setter @Getter 
	private ExceptionBean exceptionBean;

	public StandardRuntimeException() {
		super();
	}

	public StandardRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public StandardRuntimeException(String message) {
		super(message);
	}

}
