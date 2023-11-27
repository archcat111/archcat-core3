package org.cat.support.security3.generator.spring.jwt.fail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.exception.SecurityJwtVerifyException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import lombok.Getter;
import lombok.Setter;

public class JwtRedirectVerifyFailStrategyImpl implements IJwtVerifyFailStrategy {
	
	@Getter @Setter private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	@Getter @Setter private String redirectUrl = "/";

	@Override
	public void onVerifyFailure(HttpServletRequest request, HttpServletResponse response,
			SecurityJwtVerifyException exception) throws IOException, ServletException {
		this.redirectStrategy.sendRedirect(request, response, this.redirectUrl);
	}

}
