package org.cat.core.web3.log.bean;

import java.util.Map;

import org.cat.core.web3.log.constants.BaseLogConstants;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 下午3:38:20
 * @version 1.0
 * @description 业务日志，会将该bean转化为Json格式并进行输出
 *
 */
@Getter
@Setter
public class BuesinessLogBean extends BaseLogBean {

	private static final long serialVersionUID = 6849769011982934916L;
	
	///////////////////////////////////////////////////////////////覆盖父类
	
	@JsonProperty(value = "log_type")
	private String logType=BaseLogConstants.Type.BUSINESS;
	
	@JsonProperty(value = "business_msg")
	private Map<String, Object> businessMsg = Maps.newHashMap();

}
