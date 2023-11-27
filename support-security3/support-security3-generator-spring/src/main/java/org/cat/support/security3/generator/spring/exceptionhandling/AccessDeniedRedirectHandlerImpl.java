package org.cat.support.security3.generator.spring.exceptionhandling;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;

import lombok.Getter;
import lombok.Setter;

public class AccessDeniedRedirectHandlerImpl implements AccessDeniedHandler {
	
	@Getter @Setter private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	@Getter @Setter private String redirectUrl = "/";

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		this.redirectStrategy.sendRedirect(request, response, this.redirectUrl);

	}

}
