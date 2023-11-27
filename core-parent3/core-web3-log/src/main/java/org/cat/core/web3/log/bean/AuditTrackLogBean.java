package org.cat.core.web3.log.bean;

import org.cat.core.web3.log.constants.BaseLogConstants;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author 王云龙
 * @date 2021年9月7日 下午5:32:27
 * @version 1.0
 * @description 带有链路透传的AuditLogBean
 *
 */
public class AuditTrackLogBean extends AuditLogBean {

	private static final long serialVersionUID = 8882283357424826935L;
	
	///////////////////////////////////////////////////////////////覆盖父类
	
	@JsonProperty(value = "log_type")
	private String logType=BaseLogConstants.Type.AUDIT_TRACK;
	
	///////////////////////////////////////////////////////////////自己的属性
	
	@JsonProperty(value = "log_track_id") //在整个服务链路透传
	private String logTrackId;//日志链路Id，会透传给后面的服务

}
