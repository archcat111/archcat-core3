package org.cat.support.security3.generator.spring.login.success;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.login.AuthRequestMatcher;
import org.springframework.security.core.Authentication;

public interface IAuthMatcherSuccessHandler {
	void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication, AuthRequestMatcher authRequestMatcher) throws IOException, ServletException;
}
