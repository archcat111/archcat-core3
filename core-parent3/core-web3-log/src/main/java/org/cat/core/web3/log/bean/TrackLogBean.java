package org.cat.core.web3.log.bean;

import org.cat.core.web3.log.constants.BaseLogConstants;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 下午3:09:19
 * @version 1.0
 * @description 链路追踪日志Bean，继承BaseLogBean，会将该bean转化为Json格式并进行输出
 * 		额外增加log_track_id，边界为某一次请求，在服务内透传，在服务间透传
 * 		额外增加log_span_id，边界为某一次请求，在服务内透传
 * 		日志打印时间为BaseLogBean.logCreateTimetamp
 *
 */
@Getter
@Setter
public class TrackLogBean extends BaseLogBean {

	private static final long serialVersionUID = -282884041168908167L;
	
	///////////////////////////////////////////////////////////////覆盖父类
		
	@JsonProperty(value = "log_type")
	private String logType=BaseLogConstants.Type.TRACK;
	
	@JsonProperty(value = "log_track_id") //在整个服务链路透传
	private String logTrackId;//日志链路Id，会透传给后面的服务
	
	@JsonProperty(value = "log_span_id") //在服务内部透传
	private String logSpanId;//日志链路Id，服务内会透传

}
