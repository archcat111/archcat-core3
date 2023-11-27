package org.cat.support.springcloud3.starter.gateway;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import cn.hutool.core.collection.CollUtil;

/**
 * 
 * @author 王云龙
 * @date 2022年7月26日 上午10:34:54
 * @version 1.0
 * @description Gateway跨域配置（暂时不使用）
 * 		目前使用spring.cloud.gateway.globalcors进行全局跨域配置
 *
 */
@Configuration
@ConditionalOnProperty(prefix = "cat.support3.gateway.cors", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({ArchGatewayProperties.class})
public class ArchGatewayCorsAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Gateway Cors初始化：";
	
	@Autowired
	private ArchGatewayProperties archGatewayProperties;
	
	@Bean
	public CorsWebFilter corsWebFilter() {
		List<ArchGatewayCorsProperties> corsConfigs = archGatewayProperties.getCorsConfigs();
		if(CollUtil.isEmpty(corsConfigs)) {
			throw new IllegalArgumentException(this.logPrefix+"您开启了Gateway Cors的开关，但是没有指定具体的规则");
		}
		
		Map<String, CorsConfiguration> configMap = corsConfigs.stream().collect(
				Collectors.toMap(
						entry -> entry.getPath(),
						entry -> {
							List<String> allowedOrigins = entry.getAllowedOrigins();
							List<String> allowedMethods = entry.getAllowedMethods();
							List<String> allowedHeaders = entry.getAllowedHeaders();
							Boolean allowCredentials = entry.getAllowCredentials();
							
							CorsConfiguration corsConfiguration = new CorsConfiguration();
							if(CollUtil.isNotEmpty(allowedOrigins)) {corsConfiguration.setAllowedOrigins(allowedOrigins);}
							if(CollUtil.isNotEmpty(allowedMethods)) {corsConfiguration.setAllowedMethods(allowedMethods);}
							if(CollUtil.isNotEmpty(allowedHeaders)) {corsConfiguration.setAllowedHeaders(allowedHeaders);}
							if(allowCredentials!=null && allowCredentials==true) {corsConfiguration.setAllowCredentials(allowCredentials);}
							return corsConfiguration;
						}
				)
		);
		
		//UrlBasedCorsConfigurationSource匹配url的顺序可能不确定，就是foreach configMap进行匹配而已
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.setCorsConfigurations(configMap);
		
		CorsWebFilter corsWebFilter = new CorsWebFilter(urlBasedCorsConfigurationSource);
//		new CorsWebFilter(configSource, processor);
		return corsWebFilter;
	}
	
}
