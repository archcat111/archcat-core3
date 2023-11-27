package org.cat.support.springcloud3.starter.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.feign")
@Getter
@Setter
public class ArchFeignProperties {
	
	private ArchReqInterceptorFeignProperties requestInterceptor;
}
