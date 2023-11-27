package org.cat.core.web3.log.bean;

import org.cat.core.web3.log.bean.common.UserLogBean;
import org.cat.core.web3.log.constants.AuditLogConstants;
import org.cat.core.web3.log.constants.BaseLogConstants;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月7日 下午5:10:55
 * @version 1.0
 * @description 审计日志，会将该bean转化为Json格式并进行输出
 *
 */
@Getter
@Setter
public class AuditLogBean extends BaseLogBean {

	private static final long serialVersionUID = -5013685253237005325L;
	
	///////////////////////////////////////////////////////////////覆盖父类
		
	@JsonProperty(value = "log_type")
	private String logType=BaseLogConstants.Type.AUDIT;
	
	///////////////////////////////////////////////////////////////自己的属性
	
	@JsonProperty(value = "module")
	private String module;
	
	@JsonProperty(value = "user")
	private UserLogBean  userLogBean;
	
	@JsonProperty(value = "action")
	private String action;
	
	@JsonProperty(value = "audit_msg")
	private String auditMsg = null;
	
	@JsonProperty(value = "result")
	private String result = AuditLogConstants.Result.UNKNOWN;

}
