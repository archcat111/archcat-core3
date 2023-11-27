package org.cat.core.exception3.exception;

import org.cat.core.exception3.bean.ExceptionBean;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2022年4月20日 下午2:45:05
 * @version 1.0
 * @description 自定义标准异常本身发生的异常
 *
 */
public class StandardException extends Exception {

	private static final long serialVersionUID = 6323747108059278671L;
	
	@Setter @Getter 
	private Throwable throwable; //原始异常
	@Setter @Getter 
	private ExceptionBean exceptionBean;

	public StandardException() {
		super();
	}

	public StandardException(String message, Throwable cause) {
		super(message, cause);
	}

	public StandardException(String message) {
		super(message);
	}

}
