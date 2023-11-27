package org.cat.support.security3.generator.spring.exceptionhandling;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import lombok.Getter;
import lombok.Setter;

public class AuthRedirectEntryPointImpl implements AuthenticationEntryPoint {

	@Getter @Setter private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	@Getter @Setter private String redirectLoginUrl = "/";
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		this.redirectStrategy.sendRedirect(request, response, this.redirectLoginUrl);

	}

}
