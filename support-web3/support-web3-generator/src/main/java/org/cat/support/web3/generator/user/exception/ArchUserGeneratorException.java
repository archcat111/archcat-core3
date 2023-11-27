package org.cat.support.web3.generator.user.exception;

/**
 * 
 * @author 王云龙
 * @date 2022年6月1日 下午2:01:34
 * @version 1.0
 * @description UserGenerator自定义的运行时异常
 *
 */
public class ArchUserGeneratorException extends RuntimeException {

	private static final long serialVersionUID = -4386536736949165835L;

	public ArchUserGeneratorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArchUserGeneratorException(String message) {
		super(message);
	}

	public ArchUserGeneratorException(Throwable cause) {
		super(cause);
	}

}
