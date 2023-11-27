package org.cat.support.security3.generator.spring.logout.success;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.core.ComplexRequestMatcher;
import org.cat.support.security3.generator.spring.exception.SecurityLogoutException;
import org.cat.support.security3.generator.spring.logout.LogoutRequestMatcher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.Getter;
import lombok.Setter;

public class ComplexLogoutSuccessHandler implements LogoutSuccessHandler {
	
	@Getter @Setter private ComplexRequestMatcher<LogoutRequestMatcher> complexLogoutRequestMatcher;
	
	@Getter @Setter private DefaultLogoutSuccessHandler defaultLogoutSuccessHandler;
	//用于扩展LogoutSuccessHandler的特殊化处理
	@Getter @Setter private LinkedHashMap<LogoutRequestMatcher, LogoutSuccessHandler> extendMatcherToSuccessHandler;
	
	public ComplexLogoutSuccessHandler(ComplexRequestMatcher<LogoutRequestMatcher> complexLogoutRequestMatcher) {
		this.complexLogoutRequestMatcher = complexLogoutRequestMatcher;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		if(this.extendMatcherToSuccessHandler!=null && !this.extendMatcherToSuccessHandler.isEmpty()) {
			for (Map.Entry<LogoutRequestMatcher, LogoutSuccessHandler> entry : this.extendMatcherToSuccessHandler.entrySet()) {
				RequestMatcher matcher = entry.getKey();
				if (matcher.matches(request)) {
					LogoutSuccessHandler handler = entry.getValue();
					handler.onLogoutSuccess(request, response, authentication);
					return;
				}
			}
		}
		
		LogoutRequestMatcher logoutRequestMatcher = this.complexLogoutRequestMatcher.getMatchReqeustMatcher(request);
		defaultLogoutSuccessHandler.setLogoutRequestMatcher(logoutRequestMatcher);
		defaultLogoutSuccessHandler.onLogoutSuccess(request, response, authentication);
		
		if(logoutRequestMatcher==null) {
			throw new SecurityLogoutException("没有找到对应的LogoutSuccessHandler");
		}
	}

}
