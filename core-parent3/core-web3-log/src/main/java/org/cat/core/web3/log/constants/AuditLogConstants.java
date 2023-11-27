package org.cat.core.web3.log.constants;

import org.cat.core.web3.constants.ResultRespContants;
import org.cat.core.web3.log.bean.EventLogBean;
import org.cat.core.web3.resp.ArchResultResp;

/**
 * 
 * @author 王云龙
 * @date 2021年9月8日 下午3:39:14
 * @version 1.0
 * @description Audit日志相关常量类
 *
 */
public class AuditLogConstants {
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年9月8日 下午3:55:38
	 * @version 1.0
	 * @description AuditLog.result中记录的请求事件的状态
	 * 		{@linkplain EventLogBean#setEventLogStatus(String)}
	 * 		{@linkplain ResultRespContants.Status}
	 * 		{@linkplain ArchResultResp#setStatus(String)}
	 *
	 */
	public final class Result {
		public static final String NORMAL=ResultRespContants.Status.NORMAL; //正常
		public static final String SYSTEM_EXCEPTION=ResultRespContants.Status.SYSTEM_EXCEPTION; //系统异常
		public static final String BUSINESS_EXCEPTION=ResultRespContants.Status.BUSINESS_EXCEPTION; //业务异常
		public static final String UNKNOWN=ResultRespContants.Status.UNKNOWN; //未知
	}
}
