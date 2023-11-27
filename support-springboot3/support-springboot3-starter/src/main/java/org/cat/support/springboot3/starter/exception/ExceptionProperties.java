package org.cat.support.springboot3.starter.exception;

import org.cat.support.exception3.generator.constants.ExceptionSupportConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月30日 下午4:38:03
 * @version 1.0
 * @description 异常配置参数类
 * 		默认开关为开启
 *
 */
@ConfigurationProperties(prefix="cat.support3.exception")
@Getter
@Setter
public class ExceptionProperties {
	
	//是否初始化Exception相关的IdGenerator和codeGenerator
	private boolean enabled;
	
	private String idGeneratorName = ExceptionSupportConstants.IdGenerator.NO_USE;
	
	//选择系统中的多个实现之一，如果系统中存在用户的实现，则不会使用support的默认实现
	private String codeGeneratorType = ExceptionSupportConstants.CodeGenerator.DEFAULT;
	
	
}
