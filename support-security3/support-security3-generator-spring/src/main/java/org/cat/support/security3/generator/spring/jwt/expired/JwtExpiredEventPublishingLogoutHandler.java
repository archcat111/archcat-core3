package org.cat.support.security3.generator.spring.jwt.expired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class JwtExpiredEventPublishingLogoutHandler implements LogoutHandler, ApplicationEventPublisherAware  {
	
	private ApplicationEventPublisher eventPublisher;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if (this.eventPublisher == null) {
			return;
		}
		if (authentication == null) {
			return;
		}
		this.eventPublisher.publishEvent(new JwtExpiredLogoutSuccessEvent(authentication));
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}
}
