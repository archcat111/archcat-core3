package org.cat.core.web3.log.bean;

import org.cat.core.web3.log.constants.BaseLogConstants;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author 王云龙
 * @date 2021年8月18日 上午10:52:36
 * @version 1.0
 * @description 带有链路透传的EventLogBean
 *
 */
public class EventTrackLogBean extends EventLogBean {

	private static final long serialVersionUID = 861799981344252088L;
	
	///////////////////////////////////////////////////////////////覆盖父类
	
	@JsonProperty(value = "log_type")
	private String logType=BaseLogConstants.Type.EVENT_TRACK;
	
	///////////////////////////////////////////////////////////////自己的属性
	
	@JsonProperty(value = "log_track_id") //在整个服务链路透传
	private String logTrackId;//日志链路Id，会透传给后面的服务

}
