package org.cat.support.db3.generator.sharding;

/**
 * 
 * @author 王云龙
 * @date 2021年9月18日 下午5:56:53
 * @version 1.0
 * @description ShardingSphere相关的异常
 *
 */
public class ShardingException extends RuntimeException {

	private static final long serialVersionUID = 1720974488319096083L;

	public ShardingException() {
		super();
	}

	public ShardingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ShardingException(String message) {
		super(message);
	}
	
	

}
