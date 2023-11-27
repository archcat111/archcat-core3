package org.cat.support.springcloud3.starter.gateway;

import java.time.Duration;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.security3.generator.session.constants.ArchSessionConstants;
import org.cat.support.security3.generator.session.id.ArchCookieWebSessionIdResolver;
import org.cat.support.springcloud3.starter.gateway.ArchGatewaySessionProperties.ArchGatewaySessionCookieProperties;
import org.cat.support.springcloud3.starter.gateway.ArchGatewaySessionProperties.ArchGatewaySessionHeaderProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;

/**
 * 
 * @author 王云龙
 * @date 2022年8月2日 下午5:42:53
 * @version 1.0
 * @description Gateway的session处理
 *
 */
@Configuration
@ConditionalOnProperty(prefix = "cat.support3.gateway.session", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({ArchGatewaySessionProperties.class})
public class ArchGatewaySessionAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Gateway Cors初始化：";
	
	@Autowired
	private ArchGatewaySessionProperties archGatewaySessionProperties;
	
	@ConditionalOnProperty(prefix = "cat.support3.gateway.session", name = "impl", havingValue = ArchSessionConstants.Impl.COOKIE, matchIfMissing = false)
	@Bean
	public CookieWebSessionIdResolver archCookieWebSessionIdResolver() {
		ArchGatewaySessionCookieProperties archGatewaySessionCookieProperties = archGatewaySessionProperties.getCookieImpl();
		boolean decodeSession = archGatewaySessionCookieProperties.isDecodeSession();
		String sessionAttrName = archGatewaySessionCookieProperties.getSessionAttrName();
		Duration maxAge = archGatewaySessionCookieProperties.getMaxAge();
		
		
		CookieWebSessionIdResolver cookieWebSessionIdResolver = null;
		if(decodeSession) {
			cookieWebSessionIdResolver = new ArchCookieWebSessionIdResolver();
		}else {
			cookieWebSessionIdResolver = new CookieWebSessionIdResolver();
		}
		cookieWebSessionIdResolver.setCookieName(sessionAttrName);
		cookieWebSessionIdResolver.setCookieMaxAge(maxAge);
		
		return cookieWebSessionIdResolver;
	}
	
	@ConditionalOnProperty(prefix = "cat.support3.gateway.session", name = "impl", havingValue = ArchSessionConstants.Impl.HEADER, matchIfMissing = false)
	@Bean
	public HeaderWebSessionIdResolver headerWebSessionIdResolver() {
		ArchGatewaySessionHeaderProperties archGatewaySessionHeaderProperties = archGatewaySessionProperties.getHeaderImpl();
		String sessionAttrName = archGatewaySessionHeaderProperties.getSessionAttrName();
		
		HeaderWebSessionIdResolver headerWebSessionIdResolver = new HeaderWebSessionIdResolver();
		headerWebSessionIdResolver.setHeaderName(sessionAttrName);
		
		return headerWebSessionIdResolver;
	}
	
}
