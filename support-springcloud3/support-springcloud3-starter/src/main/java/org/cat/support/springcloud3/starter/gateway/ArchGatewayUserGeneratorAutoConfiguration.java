package org.cat.support.springcloud3.starter.gateway;

import static org.cat.support.springcloud3.gateway.constants.ArchGatewayConstants.LoginConstants.LOGIN_STATUS_STORAGE_SESSION;

import java.util.List;
import java.util.Map;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.springcloud3.gateway.filter.global.UserGeneratorSessionGlobalFilter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.WebSessionIdResolver;

/**
 * 
 * @author 王云龙
 * @date 2022年8月11日 下午5:43:31
 * @version 1.0
 * @description 向后端传递信息的过滤器
 *
 */
@Configuration
@ConditionalOnClass({GlobalFilter.class,WebSessionIdResolver.class})
@ConditionalOnProperty(prefix = "cat.support3.gateway.user-generator", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({ArchGatewayUserGeneratorProperties.class})
public class ArchGatewayUserGeneratorAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "UserGeneratorXXXGlobalFilter初始化：";
	
	@Autowired
	private ArchGatewayUserGeneratorProperties archGatewayUserGeneratorProperties;
	
	@ConditionalOnProperty(prefix = "cat.support3.gateway.user-generator", name = "storage", havingValue = LOGIN_STATUS_STORAGE_SESSION, matchIfMissing = false)
	@Bean
	public UserGeneratorSessionGlobalFilter userGeneratorSessionGlobalFilter() {
		int filterOrder = archGatewayUserGeneratorProperties.getFilterOrder();
		List<String> excludeRouteIds = archGatewayUserGeneratorProperties.getExcludeRouteIds();
		List<String> excludeUris = archGatewayUserGeneratorProperties.getExcludeUris();
		
		String userAttrName = archGatewayUserGeneratorProperties.getUserAttrName();
		Map<String, String> transferAttrs = archGatewayUserGeneratorProperties.getTransferAttrs();
		String transferAttrType = archGatewayUserGeneratorProperties.getTransferAttrType();
		
		
		UserGeneratorSessionGlobalFilter userGeneratorSessionGlobalFilter = new UserGeneratorSessionGlobalFilter(filterOrder);
		userGeneratorSessionGlobalFilter.setExcludeRouteIds(excludeRouteIds);
		userGeneratorSessionGlobalFilter.setExcludeUris(excludeUris);
		userGeneratorSessionGlobalFilter.setUserAttrName(userAttrName);
		userGeneratorSessionGlobalFilter.setTransferAttrs(transferAttrs);
		userGeneratorSessionGlobalFilter.setTransferAttrType(transferAttrType);
		
		coreLogger.info(logPrefix+"创建完成UserGeneratorSessionGlobalFilter");
		return userGeneratorSessionGlobalFilter;
	}

}
