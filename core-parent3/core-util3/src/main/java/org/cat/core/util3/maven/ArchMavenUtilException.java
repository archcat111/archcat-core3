package org.cat.core.util3.maven;

/**
 * 
 * @author 王云龙
 * @date 2022年5月11日 下午4:28:59
 * @version 1.0
 * @description ArchMavenUtil的运行时异常
 *
 */
public class ArchMavenUtilException extends RuntimeException {

	private static final long serialVersionUID = 2390867436255167571L;

	public ArchMavenUtilException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArchMavenUtilException(String message) {
		super(message);
	}

}
