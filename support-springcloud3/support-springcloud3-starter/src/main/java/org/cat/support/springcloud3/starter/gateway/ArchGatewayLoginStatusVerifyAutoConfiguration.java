package org.cat.support.springcloud3.starter.gateway;

import static org.cat.support.springcloud3.gateway.constants.ArchGatewayConstants.LoginConstants.LOGIN_STATUS_STORAGE_SESSION;

import java.util.List;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.springcloud3.gateway.filter.global.LoginStatusVerifySessionGlobalFilter;
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
 * @date 2022年7月25日 下午1:53:05
 * @version 1.0
 * @description 登录状态验证过滤器
 *
 */
@Configuration
@ConditionalOnClass({GlobalFilter.class,WebSessionIdResolver.class})
@ConditionalOnProperty(prefix = "cat.support3.gateway.login-verify", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({ArchGatewayLoginVerifyProperties.class})
public class ArchGatewayLoginStatusVerifyAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "GatewayLoginStatusVerify初始化：";
	
	@Autowired
	private ArchGatewayLoginVerifyProperties archGatewayLoginVerifyProperties;
	
	@ConditionalOnProperty(prefix = "cat.support3.gateway.login-verify", name = "storage", havingValue = LOGIN_STATUS_STORAGE_SESSION, matchIfMissing = false)
	@Bean
	public LoginStatusVerifySessionGlobalFilter loginStatusVerifySessionGlobalFilter() {
		String statusAttrName = archGatewayLoginVerifyProperties.getStatusAttrName();
		int statusGlobalFilterOrder = archGatewayLoginVerifyProperties.getFilterOrder();
		List<String> excludeRouteIds = archGatewayLoginVerifyProperties.getExcludeRouteIds();
		List<String> excludeUris = archGatewayLoginVerifyProperties.getExcludeUris();
		LoginStatusVerifySessionGlobalFilter loginStatusVerifySessionGlobalFilter = new LoginStatusVerifySessionGlobalFilter(statusAttrName,statusGlobalFilterOrder);
		loginStatusVerifySessionGlobalFilter.setExcludeRouteIds(excludeRouteIds);
		loginStatusVerifySessionGlobalFilter.setExcludePaths(excludeUris);
		coreLogger.info(logPrefix+"创建完成LoginStatusVerifySessionGlobalFilter");
		return loginStatusVerifySessionGlobalFilter;
	}

}
