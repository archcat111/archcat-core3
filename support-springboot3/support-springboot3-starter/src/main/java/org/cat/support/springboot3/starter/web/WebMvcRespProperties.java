package org.cat.support.springboot3.starter.web;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月30日 下午4:42:35
 * @version 1.0
 * @description Response相关的配置类
 *
 */
@Getter
@Setter
public class WebMvcRespProperties {
	private boolean openDownloadGenerator=false;
	private boolean openResultRespGenerator=true;
	private boolean openSpringRespGenerator=false;
	private boolean openWsrsRespGenerator=false;
	private WebMvcRespConverterProperties converter = new WebMvcRespConverterProperties();
}
