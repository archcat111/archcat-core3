package org.cat.core.web3.log.bean;

import org.cat.core.web3.log.bean.common.ExceptionLogBean;
import org.cat.core.web3.log.bean.common.UserLogBean;
import org.cat.core.web3.log.constants.BaseLogConstants;
import org.cat.core.web3.log.constants.EventLogConstants;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 上午10:54:15
 * @version 1.0
 * @description Event Log，会将该bean转化为Json格式并进行输出
 *
 */
@Getter
@Setter
public class EventLogBean extends BaseLogBean {
	private static final long serialVersionUID = 56615520418813641L;
	
	///////////////////////////////////////////////////////////////覆盖父类
	
	@JsonProperty(value = "log_type")
	private String logType=BaseLogConstants.Type.EVENT;
	
	///////////////////////////////////////////////////////////////log相关
	
	@JsonProperty(value = "event_log_type")
	private String eventLogType;//EventLogConstants.Type（http.controller.execute、http.api.execute、dubbo.api.execute、unknow）
	
	@JsonProperty(value = "event_log_status") 
	private String eventLogStatus=EventLogConstants.Status.UNKNOWN;//ResultRespContants.Status（normal、system-exception、business-exception、unknow）
	
	///////////////////////////////////////////////////////////////客户端
	
	@JsonProperty(value="user")
	private UserLogBean userLogBean;
	
	///////////////////////////////////////////////////////////////api、class
	
	@JsonProperty(value = "api_name")
	private String apiName;
	
	@JsonProperty(value = "api_version")
	private String apiVersion;
	
	@JsonProperty(value = "api_params")
	private String apiParams;
	
	@JsonProperty(value = "clazz_name")
	private String clazzName;
	
	@JsonProperty(value = "method_name")
	private String methodName;
	
	@JsonProperty(value = "api_execution_time")
	private long apiExecutionTime;//API执行时长
	
	///////////////////////////////////////////////////////////////HTTP
	
	@JsonProperty(value = "http_uri")
	private String httpUri;
	
	@JsonProperty(value = "http_method")
	private String httpMethod;
	
	@JsonProperty(value = "http_status")
	private String httpStatus;
	
	///////////////////////////////////////////////////////////////异常
	
	@JsonProperty(value="exception")
	private ExceptionLogBean exceptionLogBean;
	
}
