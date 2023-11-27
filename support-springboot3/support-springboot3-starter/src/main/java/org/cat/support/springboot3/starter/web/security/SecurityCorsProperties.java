package org.cat.support.springboot3.starter.web.security;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityCorsProperties {
	private boolean enabled = false;
	private boolean allowAll = false;
	private Map<String, Customize> customizes; //key为需要访问的url，例如："/**"
	
	@Getter
	@Setter
	public class Customize{
		private String allowedOrigins; //"https://example.com", "https://example1.com"
		private String allowedMethods = "GET,HEAD,POST"; 
		private String allowedHeaders = "*";
		private String exposedHeaders =null;
		private long maxAge = 1800L;
		private boolean allowCredentials = false;
	}
}
