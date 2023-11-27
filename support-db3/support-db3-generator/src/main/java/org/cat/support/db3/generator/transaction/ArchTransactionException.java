package org.cat.support.db3.generator.transaction;

/**
 * 
 * @author 王云龙
 * @date 2022年4月28日 下午6:42:11
 * @version 1.0
 * @description Transaction自定义的运行时异常
 *
 */
public class ArchTransactionException extends RuntimeException {

	private static final long serialVersionUID = -4386536736949165835L;

	public ArchTransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArchTransactionException(String message) {
		super(message);
	}

	public ArchTransactionException(Throwable cause) {
		super(cause);
	}

}
