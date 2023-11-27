package org.cat.support.security3.generator.spring.jwt.expired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.SpringSecurityCoreVersion;

import lombok.Getter;

public class JwtExpiredEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	@Getter private final HttpServletRequest request;
	@Getter private final HttpServletResponse response;
	@Getter private final String jwtToken;

	public JwtExpiredEvent(Object source, HttpServletRequest request, HttpServletResponse response, String jwtToken) {
		super(source);
		this.request = request;
		this.response = response;
		this.jwtToken = jwtToken;
	}

}
