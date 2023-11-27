package org.cat.core.web3.log.bean;

import org.cat.core.web3.log.constants.BaseLogConstants;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 下午4:00:55
 * @version 1.0
 * @description 异常日志，会将该bean转化为Json格式并进行输出
 *
 */
@Getter
@Setter
public class ExceptionLogBean extends BaseLogBean {

	private static final long serialVersionUID = -5351638529567140113L;
	
	///////////////////////////////////////////////////////////////覆盖父类
	
	@JsonProperty(value = "log_type")
	private String logType=BaseLogConstants.Type.EXCEPTION;
	
	@JsonProperty("exception")
	private ExceptionLogBean exceptionLogBean;

}
