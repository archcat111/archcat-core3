package org.cat.support.springboot3.starter.web;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月30日 下午4:32:47
 * @version 1.0
 * @description 跨域请求控制类
 *
 */
@Getter
@Setter
public class WebMvcCorsProperties {
	//跨域请求总体控制开关，默认关闭，即：不启用
	private boolean enabled = false;
	//可以配置多条跨域规则
	private List<WebMvcCorsRuleProperties> corsRules;
}
