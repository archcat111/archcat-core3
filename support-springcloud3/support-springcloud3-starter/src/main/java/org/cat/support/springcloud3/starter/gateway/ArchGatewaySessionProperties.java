package org.cat.support.springcloud3.starter.gateway;

import java.time.Duration;

import org.cat.core.web3.constants.SessionConstants;
import org.cat.support.security3.generator.session.constants.ArchSessionConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.gateway.session")
@Getter
@Setter
public class ArchGatewaySessionProperties {
	
	private String impl = ArchSessionConstants.Impl.COOKIE;
	
	private ArchGatewaySessionCookieProperties cookieImpl = new ArchGatewaySessionCookieProperties();
	private ArchGatewaySessionHeaderProperties headerImpl = new ArchGatewaySessionHeaderProperties();
	
	@Getter
	@Setter
	public class ArchGatewaySessionCookieProperties {
		private String sessionAttrName = SessionConstants.ATTR_NAME_DEFAULT;
		private Duration maxAge = Duration.ofSeconds(3600);
		private boolean decodeSession = false;
	}
	
	@Getter
	@Setter
	public class ArchGatewaySessionHeaderProperties {
		private String sessionAttrName = "SESSION";
	}
	
}
