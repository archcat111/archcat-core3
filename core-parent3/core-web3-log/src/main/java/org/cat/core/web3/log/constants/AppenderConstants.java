package org.cat.core.web3.log.constants;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 下午3:31:49
 * @version 1.0
 * @description Appender相关常量
 *
 */
public class AppenderConstants {
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月17日 上午10:50:58
	 * @version 1.0
	 * @description Appender的相关常量
	 * 		在初始化Logger时需要
	 * 		编写log.xml配置文件时需要配置
	 *
	 */
	public static final class AppenderName {
		public static final String CORE="arch.core";//核心框架日志
		
		public static final String EVENT="arch.event";//API event输出日志
		public static final String AUDIT="arch.audit";//审计日志
		public static final String TRACK="arch.track";//链路追踪日志
		
		public static final String INFO="arch.info";//业务系统输出日志
		public static final String EXCEPTION="arch.exception";//如果需要单独将异常输出，可采用该Appender
		
		public static final String OTHER_FEIGN="arch.feign";//Feign的日志输出
	}
}
