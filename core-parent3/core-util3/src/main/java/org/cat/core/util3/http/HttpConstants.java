package org.cat.core.util3.http;

import org.apache.http.HttpStatus;

/**
 * 
 * @author 王云龙
 * @date 2021年7月21日 下午4:54:42
 * @version 1.0
 * @description HTTP工具包常量类
 *
 */
public class HttpConstants {
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年7月21日 下午4:58:10
	 * @version 1.0
	 * @description HttpResponse的status值 {@linkplain HttpResponse#setStatus(int)}
	 * 		其他状态参考：{@linkplain HttpStatus}
	 *
	 */
	public static class Status{
		public static final int HTTP_CLIENT_EXCEPTION=-1;
		public static final int NOT_WRITE=0;
	}
	
	public static class Content{
		public static final String NOT_WRITE=null;
	}
	
	public static class ExceptionClass{
		public static final Class<?> NOT_WRITE=null;
	}
	
	public static class ExceptionMsg{
		public static final String NOT_WRITE=null;
	}
	
	public static class Https{
		public static final String TSLv1="TLSv1";
	}
}
