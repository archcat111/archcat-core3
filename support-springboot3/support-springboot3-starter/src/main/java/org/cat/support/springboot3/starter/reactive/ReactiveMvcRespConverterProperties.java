package org.cat.support.springboot3.starter.reactive;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2022年7月25日 下午8:39:27
 * @version 1.0
 * @description Reactive MVC中，关于结果字段转换的配置类
 *
 */
@Getter
@Setter
public class ReactiveMvcRespConverterProperties {
	private boolean enabled;
	private boolean converterDateToString = true;
	private boolean converterLongToString = true;
	private boolean converterLocalDateTime = true;
}
