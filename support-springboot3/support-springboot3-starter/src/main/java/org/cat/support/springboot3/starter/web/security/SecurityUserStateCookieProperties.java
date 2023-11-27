package org.cat.support.springboot3.starter.web.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUserStateCookieProperties {
	
	private String cookiesToClear = null; //<cookieA>,<cookieB>,...
}
