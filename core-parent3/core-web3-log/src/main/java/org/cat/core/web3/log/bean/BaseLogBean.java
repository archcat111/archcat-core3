package org.cat.core.web3.log.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 上午11:01:33
 * @version 1.0
 * @description 打印日志的基础Bean
 *
 */
@Getter
@Setter
public class BaseLogBean implements Serializable {

	private static final long serialVersionUID = 7698109824736387866L;
	
	///////////////////////////////////////////////////////////////log相关
	
	@JsonProperty(value = "log_id")
	private String logId;
	
	@JsonProperty(value = "log_type") //每种类型的日志都会覆盖该属性并设置好，不需要单独设置
	private String logType;//event、track、business、exception、debug
	
	@JsonProperty(value = "log_create_timetamp")
	private String logCreateTimetamp;//日志生成的时间戳（毫秒级）
	
	@JsonProperty(value = "log_self_execution_time")
	private long logSelfExecutionTime;//日志本身执行时长（毫秒级）
	
	///////////////////////////////////////////////////////////////公司、平台、服务、主机
		
	@JsonProperty(value = "company")
	private String company;//所属公司
	
	@JsonProperty(value = "platform")
	private String platform;//公司中的某个平台
	
	@JsonProperty(value = "app_name")
	private String appName;//平台中的某个微服务，在非微服务体系中可以理解为某个应用或者服务
	
	@JsonProperty(value = "app_version")
	private String appVersion;
	
	@JsonProperty(value = "host_name")
	private String hostName;//主机设置的名称
	
	@JsonProperty(value = "host_ip")
	private String hostIp;//主机IP
	
	@JsonProperty(value = "host_port")
	private String hostPort;//主机端口
	
	///////////////////////////////////////////////////////////////
	
	@JsonProperty(value = "framework")
	private String framework;//LogConstants.Framework（Java、Python）
	
	@JsonProperty(value = "framework-sub")
	private String frameworkSub;//LogConstants.FrameworkSub（SpringCloud、Dubbo）
	
}
