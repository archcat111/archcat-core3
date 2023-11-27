package org.cat.support.springboot3.starter.web.session;

import org.apache.commons.lang3.StringUtils;
import org.cat.support.security3.generator.session.constants.ArchSessionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;

@Configuration
@ConditionalOnClass({CookieSerializer.class,HeaderHttpSessionIdResolver.class})
@ConditionalOnProperty(prefix = "cat.support3.web.session", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebSessionProperties.class)
public class WebSessionConfiguration {
	
	@Autowired
	private WebSessionProperties webSessionProperties;
	
	@ConditionalOnProperty(prefix = "cat.support3.web.session", name = "impl", havingValue = ArchSessionConstants.Impl.COOKIE, matchIfMissing = false)
	@Bean
	CookieSerializer cookieSerializer() {
		//CookieHttpSessionIdResolver会使用该CookieSerializer处理cookie
		WebSessionCookieProperties webSessionCookieProperties = webSessionProperties.getCookieImpl();
		
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		cookieSerializer.setCookieName(webSessionCookieProperties.getSessionAttrName());
		cookieSerializer.setDomainName(webSessionCookieProperties.getDomainName());
		cookieSerializer.setCookiePath(webSessionCookieProperties.getCookiePath());
		cookieSerializer.setUseHttpOnlyCookie(webSessionCookieProperties.isUseHttpOnlyCookie());
		if(webSessionCookieProperties.getUseSecureCookie()!=null) {
			cookieSerializer.setUseSecureCookie(webSessionCookieProperties.getUseSecureCookie());
		}
		cookieSerializer.setCookieMaxAge((int) webSessionCookieProperties.getMaxAge().getSeconds());
		if(StringUtils.isBlank(webSessionCookieProperties.getSameSite())) {
			cookieSerializer.setSameSite(null);
		}else {
			cookieSerializer.setSameSite(webSessionCookieProperties.getSameSite());
		}
		
		return cookieSerializer;
	}
	
	@ConditionalOnProperty(prefix = "cat.support3.web.session", name = "impl", havingValue = ArchSessionConstants.Impl.HEADER, matchIfMissing = false)
	@Bean
	HeaderHttpSessionIdResolver headerHttpSessionIdResolver() {
		WebSessionHeaderProperties webSessionHeaderProperties = webSessionProperties.getHeaderImpl();
		String sessionAttrName = webSessionHeaderProperties.getSessionAttrName();
		
		HeaderHttpSessionIdResolver headerHttpSessionIdResolver = new HeaderHttpSessionIdResolver(sessionAttrName);
		
		return headerHttpSessionIdResolver;
	}
	
	

}
