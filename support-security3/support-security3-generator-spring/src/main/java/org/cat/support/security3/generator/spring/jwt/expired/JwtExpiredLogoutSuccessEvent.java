package org.cat.support.security3.generator.spring.jwt.expired;

import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class JwtExpiredLogoutSuccessEvent extends AbstractAuthenticationEvent {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	public JwtExpiredLogoutSuccessEvent(Authentication authentication) {
		super(authentication);
	}
}
