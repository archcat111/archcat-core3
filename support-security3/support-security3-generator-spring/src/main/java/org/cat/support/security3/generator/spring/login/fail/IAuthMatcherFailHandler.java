package org.cat.support.security3.generator.spring.login.fail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.login.AuthRequestMatcher;
import org.springframework.security.core.AuthenticationException;

public interface IAuthMatcherFailHandler {
	void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception, 
			AuthRequestMatcher authRequestMatcher) throws IOException, ServletException;
}
