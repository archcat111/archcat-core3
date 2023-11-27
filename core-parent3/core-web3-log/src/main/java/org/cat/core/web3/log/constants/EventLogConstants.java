package org.cat.core.web3.log.constants;

import org.cat.core.web3.constants.ResultRespContants;
import org.cat.core.web3.log.bean.EventLogBean;
import org.cat.core.web3.resp.ArchResultResp;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 上午11:40:01
 * @version 1.0
 * @description Event日志相关常量类
 *
 */
public class EventLogConstants {
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月25日 下午3:44:21
	 * @version 1.0
	 * @description EventLog.apiName中记录的接口名称
	 * 		{@linkplain EventLogBean#setApiName(String)}
	 * 		{@linkplain EventLogConstants.ApiName}
	 *
	 */
	public final class ApiName {
		public static final String UNKONWN="unknown";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月16日 下午2:09:11
	 * @version 1.0
	 * @description EventLog.status中记录的请求事件的状态
	 * 		{@linkplain EventLogBean#setEventLogStatus(String)}
	 * 		{@linkplain ResultRespContants.Status}
	 * 		{@linkplain ArchResultResp#setStatus(String)}
	 *
	 */
	public final class Status {
		public static final String NORMAL=ResultRespContants.Status.NORMAL; //正常
		public static final String SYSTEM_EXCEPTION=ResultRespContants.Status.SYSTEM_EXCEPTION; //系统异常
		public static final String BUSINESS_EXCEPTION=ResultRespContants.Status.BUSINESS_EXCEPTION; //业务异常
		public static final String UNKNOWN=ResultRespContants.Status.UNKNOWN; //未知
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月17日 下午3:33:24
	 * @version 1.0
	 * @description EventLog.type中的细分类型
	 *
	 */
	public final class Type {
		public static final String HTTP_CONTROLLER_EXECUTE="http.controller.execute";//基于Http协议的controller执行日志
		public static final String HTTP_API_EXECUTE="http.api.execute";//基于Http协议的后端API执行日志
		public static final String DUBBO_CONTROLLER_EXECUTE="dubbo.controller.execute";//基于Dubbo协议的controller执行日志
		public static final String DUBBO_API_EXECUTE="dubbo.api.execute";//基于Dubbo协议的后端API执行日志
		public static final String UNKNOWN="unknown";//未知
	}
}
