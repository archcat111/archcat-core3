package org.cat.support.springboot3.starter.reactive;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2022年7月25日 下午8:39:55
 * @version 1.0
 * @description Reactive中的Response相关的配置类
 *
 */
@Getter
@Setter
public class ReactiveMvcRespProperties {
	private boolean openResultRespGenerator=true;
	private ReactiveMvcRespConverterProperties converter = new ReactiveMvcRespConverterProperties();
}
