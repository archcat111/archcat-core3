package org.cat.core.util3.file;

/**
 * 
 * @author 王云龙
 * @date 2021年7月14日 下午3:31:58
 * @version 1.0
 * @description 创建目录失败异常
 *
 */
public class CreateDirFailException extends RuntimeException {

	private static final long serialVersionUID = 5999227221498561186L;

	public CreateDirFailException(String message) {
		super(message);
	}

	public CreateDirFailException() {
		super();
	}

	public CreateDirFailException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
