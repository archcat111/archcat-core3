package org.cat.support.springboot3.starter.web.security;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.web.security")
@Getter
@Setter
public class SecurityProperties {
	private boolean enabled = false;
//	private String webFrame = SecurityConstants.WebFrame.SERVLET;
//	private String implFrame = SecurityConstants.ImplFrame.SPRING;
	
	private SecurityUserStateProperties userState;
	
	private Map<String, SecurityLoginProperties> login;
	private Map<String, SecurityLogoutProperties> logout;
	private SecurityExceptionProperties exception;
	
	private SecurityCorsProperties cors;
	private SecurityCsrfProperties csrf;
	
}
