package org.cat.support.springcloud3.web.feign;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 
 * @author 王云龙
 * @date 2022年8月15日 上午11:23:34
 * @version 1.0
 * @description 可以自定义如下实例：
 * 		RequestMappingHandlerMapping
 * 		RequestMappingHandlerAdapter
 * 		ExceptionHandlerExceptionResolver
 *
 */
public class SpringCloudWebMvcRegistrations implements WebMvcRegistrations {

	@Override
	public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
		SpringCloudRequestMappingHandlerMapping springCloudRequestMappingHandlerMapping
			= new SpringCloudRequestMappingHandlerMapping();
		
		return springCloudRequestMappingHandlerMapping;
	}

	
}
