package org.cat.support.security3.generator.spring.login.success;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.core.ComplexRequestMatcher;
import org.cat.support.security3.generator.spring.exception.SecurityAuthException;
import org.cat.support.security3.generator.spring.login.AuthRequestMatcher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import lombok.Getter;
import lombok.Setter;

public class ComplexAuthSuccessHandler implements AuthenticationSuccessHandler {
	
	@Getter @Setter private ComplexRequestMatcher<AuthRequestMatcher> complexAuthRequestMatcher;
	
	@Getter @Setter private IAuthMatcherSuccessHandler defaultAuthSuccessHandler;
	
	public ComplexAuthSuccessHandler(ComplexRequestMatcher<AuthRequestMatcher> complexAuthRequestMatcher) {
		this.complexAuthRequestMatcher = complexAuthRequestMatcher;
	}

	//用于扩展LogoutSuccessHandler的特殊化处理
	@Getter @Setter private LinkedHashMap<AuthRequestMatcher, IAuthMatcherSuccessHandler> extendMatcherToSuccessHandler;

	
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		if(this.extendMatcherToSuccessHandler!=null && !this.extendMatcherToSuccessHandler.isEmpty()) {
			for (Map.Entry<AuthRequestMatcher, IAuthMatcherSuccessHandler> entry : this.extendMatcherToSuccessHandler.entrySet()) {
				AuthRequestMatcher matcher = entry.getKey();
				if (matcher.matches(request)) {
					IAuthMatcherSuccessHandler handler = entry.getValue();
					handler.onAuthenticationSuccess(request, response, authentication, matcher);
					return;
				}
			}
		}
		
		AuthRequestMatcher authRequestMatcher = this.complexAuthRequestMatcher.getMatchReqeustMatcher(request);
		this.defaultAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication, authRequestMatcher);
		
		if(authRequestMatcher==null) {
			throw new SecurityAuthException("没有找到对应的AuthenticationSuccessHandler");
		}
	}

}
