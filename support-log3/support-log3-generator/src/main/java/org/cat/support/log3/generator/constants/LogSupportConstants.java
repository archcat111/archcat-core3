package org.cat.support.log3.generator.constants;

/**
 * 
 * @author 王云龙
 * @date 2021年9月2日 下午4:16:07
 * @version 1.0
 * @description Log相关的Starter使用到的常量的常量类
 *
 */
public class LogSupportConstants {

	/**
	 * 
	 * @author 王云龙
	 * @date 2021年9月2日 下午4:16:48
	 * @version 1.0
	 * @description 
	 *
	 */
	public static final class IdGenerator {
		/**
		 * 当cat.support3.web.log.audit.logIdGeneratorName为noUse的时候会初始化LogIdGeneratorForNoUse
		 * or 当cat.support3.web.log.event.logIdGeneratorName为noUse的时候会初始化LogIdGeneratorForNoUse
		 * 否则初始化LogIdGeneratorImpl
		 */
		public static final String NO_USE="noUse";
	}
	
	public static final class Out {
		public static final String LOCAL = "local";
		public static final String MYSQL = "mysql";
		public static final String APPENDER = "appender";
		public static final String UDP = "udp";
		public static final String KAFKA = "kafka";
	}
}
