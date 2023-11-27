package org.cat.core.web3.log.constants;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 上午10:16:15
 * @version 1.0
 * @description 日志相关常量类
 *
 */
public class BaseLogConstants {
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年9月2日 下午4:21:51
	 * @version 1.0
	 * @description 日志Id
	 *
	 */
	public static final class Id {
		public static final String NOT_WRITE="-1";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月17日 上午11:04:53
	 * @version 1.0
	 * @description 日志类型
	 *
	 */
	public static final class Type {
		public static final String EVENT="event";//eventLog
		public static final String EVENT_TRACK="eventTrack";//eventTrackLog
		public static final String TRACK="track";//trackLog
		public static final String USER_TRACK="userTrack";//userTrackLog
		public static final String BUSINESS="business";//businessLog
		public static final String EXCEPTION="exception";//exceptionLog
		public static final String DEBUG="debug";//debugLog
		public static final String AUDIT="audit";//auditLog
		public static final String AUDIT_TRACK="auditTrack";//auditTrackLog
	}
	
}
