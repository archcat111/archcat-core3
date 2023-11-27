package org.cat.core.web3.log.bean;

import java.util.Map;

import org.cat.core.web3.log.constants.BaseLogConstants;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

/**
 * 
 * @author 王云龙
 * @date 2021年8月18日 上午10:55:39
 * @version 1.0
 * @description Debug的LogBean
 *
 */
public class DebugLogBean extends BaseLogBean {

	private static final long serialVersionUID = -6922666498744145803L;
	
	///////////////////////////////////////////////////////////////覆盖父类
		
	@JsonProperty(value = "log_type")
	private String logType=BaseLogConstants.Type.DEBUG;
	
	///////////////////////////////////////////////////////////////自己的属性
	
	@JsonProperty(value = "debug_msg")
	private Map<String, Object> debugMsg = Maps.newHashMap();

}
