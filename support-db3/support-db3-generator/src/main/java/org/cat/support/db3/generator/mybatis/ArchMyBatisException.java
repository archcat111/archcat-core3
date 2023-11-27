package org.cat.support.db3.generator.mybatis;

/**
 * 
 * @author 王云龙
 * @date 2021年10月9日 下午2:44:38
 * @version 1.0
 * @description MyBatis自定义的运行时异常
 *
 */
public class ArchMyBatisException extends RuntimeException {

	private static final long serialVersionUID = -4386536736949165835L;

	public ArchMyBatisException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArchMyBatisException(String message) {
		super(message);
	}

	public ArchMyBatisException(Throwable cause) {
		super(cause);
	}

}
