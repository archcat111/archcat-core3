package org.cat.core.util3.json;

/**
 * 
 * @author 王云龙
 * @date 2021年8月25日 下午1:47:44
 * @version 1.0
 * @description JsonUtil的运行时异常
 *
 */
public class ArchJsonUtilException extends RuntimeException {

	private static final long serialVersionUID = 2390867436255167571L;

	public ArchJsonUtilException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArchJsonUtilException(String message) {
		super(message);
	}

}
