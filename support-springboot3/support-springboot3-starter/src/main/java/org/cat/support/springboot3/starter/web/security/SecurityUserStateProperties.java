package org.cat.support.springboot3.starter.web.security;

import org.cat.support.security3.generator.spring.constants.SecurityUserStateConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUserStateProperties {
	private String frame = SecurityUserStateConstants.Frame.SESSION;
	private String storage = SecurityUserStateConstants.Storage.LOCAL;
	
	private SecurityUserStateJwtProperties jwt;
	private SecurityUserStateSessionProperties session;
	
	private SecurityUserStateRedisProperties redis;
	
	private SecurityUserStateCookieProperties cookie;
	
	
}
