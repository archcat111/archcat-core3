package org.cat.support.security3.generator.session.constants;

public class ArchSessionConstants {
	
	//使用何种方式实现session，默认为cookie，会初始化不同的HttpSessionIdResolver
	//例如：CookieHttpSessionIdResolver、HeaderHttpSessionIdResolver
	public static final class Impl {
		public static final String HEADER="header";
		public static final String COOKIE="cookie";
	}
	
	//chrome 51开始的一个标准但是默认关闭，80开始强制开启
	public static final class CookieSameSite {
		public static final String STRICT="Strict";
		public static final String LAX="Lax";
		public static final String NONE="None";
	}

}
