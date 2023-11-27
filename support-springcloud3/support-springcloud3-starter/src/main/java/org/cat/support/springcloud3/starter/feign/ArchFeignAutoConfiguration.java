package org.cat.support.springcloud3.starter.feign;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.user.IUserGenerator;
import org.cat.support.springcloud3.nacos.ArchNacosServiceDiscovery;
import org.cat.support.springcloud3.web.feign.interceptor.UserGeneratorRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;

import feign.RequestInterceptor;


@Configuration
@AutoConfigureBefore(NacosDiscoveryAutoConfiguration.class)
@ConditionalOnClass(ArchNacosServiceDiscovery.class)
@ConditionalOnProperty(prefix = "cat.support3.feign", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({ArchFeignProperties.class})
public class ArchFeignAutoConfiguration {
	
	@Autowired
	private ArchFeignProperties archFeignProperties;
	
	@Autowired
	private IUserGenerator userGenerator;
	
	@ConditionalOnProperty(prefix = "cat.support3.feign.requestInterceptor.userGenerator", name = "enabled", havingValue = "true", matchIfMissing = false)
	@Bean
	public RequestInterceptor userGeneratorRequestInterceptor() {
		
		ArchUsergeneratorReqInterceptorFeignProperties archUsergeneratorReqInterceptorFeignProperties = archFeignProperties.getRequestInterceptor().getUserGenerator();
		
		UserGeneratorRequestInterceptor userGeneratorRequestInterceptor = new UserGeneratorRequestInterceptor();
		
		String sessionAttrName = archUsergeneratorReqInterceptorFeignProperties.getSessionAttrName();
		if(StringUtils.isNotBlank(sessionAttrName)) {
			userGeneratorRequestInterceptor.setSessionAttrName(sessionAttrName);
		}
		userGeneratorRequestInterceptor.setUserGenerator(userGenerator);
		
		userGeneratorRequestInterceptor.openSwitch();
		return userGeneratorRequestInterceptor;
		
	}

}
