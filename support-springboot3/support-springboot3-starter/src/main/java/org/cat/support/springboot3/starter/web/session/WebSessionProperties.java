package org.cat.support.springboot3.starter.web.session;

import org.cat.support.security3.generator.session.constants.ArchSessionConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.web.session")
@Getter
@Setter
public class WebSessionProperties {
	private boolean enabled;
	
	//使用何种方式实现session，默认为cookie，会初始化不同的HttpSessionIdResolver
		//例如：CookieHttpSessionIdResolver、HeaderHttpSessionIdResolver
	private String impl = ArchSessionConstants.Impl.COOKIE;
	
	private WebSessionCookieProperties cookieImpl = new WebSessionCookieProperties();
	private WebSessionHeaderProperties headerImpl = new WebSessionHeaderProperties();
	
}
