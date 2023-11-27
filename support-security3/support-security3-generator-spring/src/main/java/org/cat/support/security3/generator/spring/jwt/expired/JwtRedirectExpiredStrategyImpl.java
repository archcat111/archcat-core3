package org.cat.support.security3.generator.spring.jwt.expired;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import lombok.Getter;
import lombok.Setter;

public class JwtRedirectExpiredStrategyImpl implements IJwtExpiredStrategy {
	
	@Getter @Setter private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	@Getter @Setter private String redirectUrl = "/";

	@Override
	public void onExpiredSessionDetected(JwtExpiredEvent event) throws IOException, ServletException {
		this.redirectStrategy.sendRedirect(event.getRequest(), event.getResponse(), this.redirectUrl);
	}

}
