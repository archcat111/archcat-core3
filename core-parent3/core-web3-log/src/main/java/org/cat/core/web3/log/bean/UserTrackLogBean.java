package org.cat.core.web3.log.bean;

import org.cat.core.web3.log.bean.common.UserLogBean;
import org.cat.core.web3.log.constants.BaseLogConstants;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 下午3:14:11
 * @version 1.0
 * @description 用户请求的链路追踪日志，会将该bean转化为Json格式并进行输出
 *
 */
@Getter
@Setter
public class UserTrackLogBean extends TrackLogBean {

	private static final long serialVersionUID = -8302005757049299558L;
	
	///////////////////////////////////////////////////////////////覆盖父类
	
	@JsonProperty(value = "log_type")
	private String logType=BaseLogConstants.Type.USER_TRACK;
	
	@JsonProperty(value="user") //在整个服务链路透传
	private UserLogBean userLogBean;

}
