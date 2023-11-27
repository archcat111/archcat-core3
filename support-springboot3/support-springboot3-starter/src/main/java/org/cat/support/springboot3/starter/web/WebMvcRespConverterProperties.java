package org.cat.support.springboot3.starter.web;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月30日 下午4:40:51
 * @version 1.0
 * @description Web MVC中，关于结果字段转换的配置类
 *
 */
@Getter
@Setter
public class WebMvcRespConverterProperties {
	private boolean enabled;
	private boolean converterDateToString = true;
	private boolean converterLongToString = true;
	private boolean converterLocalDateTime = true;
}
