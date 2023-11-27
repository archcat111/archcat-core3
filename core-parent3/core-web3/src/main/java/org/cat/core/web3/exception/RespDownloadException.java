package org.cat.core.web3.exception;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午4:25:38
 * @version 1.0
 * @description 提供下载能力时的异常类
 *
 */
public class RespDownloadException extends RuntimeException {

	private static final long serialVersionUID = 3935581312871404331L;

	public RespDownloadException() {
		super();
	}

	public RespDownloadException(String message, Throwable cause) {
		super(message, cause);
	}

	public RespDownloadException(String message) {
		super(message);
	}
	
}
