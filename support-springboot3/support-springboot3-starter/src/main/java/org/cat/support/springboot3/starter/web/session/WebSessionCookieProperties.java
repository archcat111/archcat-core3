package org.cat.support.springboot3.starter.web.session;

import java.time.Duration;

import org.cat.core.web3.constants.SessionConstants;
import org.cat.support.security3.generator.session.constants.ArchSessionConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2022年8月2日 下午3:29:22
 * @version 1.0
 * @description 使用cookie实现session时的配置文件
 *
 */
@Getter
@Setter
public class WebSessionCookieProperties {
	
	private String sessionAttrName = SessionConstants.ATTR_NAME_DEFAULT;
	private String domainName;
	private String cookiePath;
	private boolean useHttpOnlyCookie = true;
	private Boolean useSecureCookie;
	private Integer cookieMaxAge;
	private Duration maxAge = Duration.ofSeconds(3600);
	private String sameSite = ArchSessionConstants.CookieSameSite.NONE;
	
	
}
