package org.cat.support.security3.generator.spring.login.fail;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.core.ComplexRequestMatcher;
import org.cat.support.security3.generator.spring.exception.SecurityAuthException;
import org.cat.support.security3.generator.spring.login.AuthRequestMatcher;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import lombok.Getter;
import lombok.Setter;

public class ComplexAuthFailHandler implements AuthenticationFailureHandler {
	
	@Getter @Setter private ComplexRequestMatcher<AuthRequestMatcher> complexAuthRequestMatcher;
	
	@Getter @Setter private IAuthMatcherFailHandler defaultAuthFailHandler;
	
	//用于扩展LogoutSuccessHandler的特殊化处理
	@Getter @Setter private LinkedHashMap<AuthRequestMatcher, IAuthMatcherFailHandler> extendMatcherToFailHandler;
	
	public ComplexAuthFailHandler(ComplexRequestMatcher<AuthRequestMatcher> complexAuthRequestMatcher) {
		this.complexAuthRequestMatcher = complexAuthRequestMatcher;
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if(this.extendMatcherToFailHandler!=null && !this.extendMatcherToFailHandler.isEmpty()) {
			for (Map.Entry<AuthRequestMatcher, IAuthMatcherFailHandler> entry : this.extendMatcherToFailHandler.entrySet()) {
				AuthRequestMatcher matcher = entry.getKey();
				if (matcher.matches(request)) {
					IAuthMatcherFailHandler handler = entry.getValue();
					handler.onAuthenticationFailure(request, response, exception, matcher);
					return;
				}
			}
		}
		
		AuthRequestMatcher authRequestMatcher = this.complexAuthRequestMatcher.getMatchReqeustMatcher(request);
		this.defaultAuthFailHandler.onAuthenticationFailure(request, response, exception, authRequestMatcher);
		
		if(authRequestMatcher==null) {
			throw new SecurityAuthException("没有找到对应的AuthenticationFailureHandler");
		}
	}

}
