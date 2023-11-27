package org.cat.support.security3.generator.spring.login.fail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.core.web3.util.CookieUtil;
import org.cat.support.security3.generator.spring.constants.SecurityLoginConstants;
import org.cat.support.security3.generator.spring.exception.SecurityAuthException;
import org.cat.support.security3.generator.spring.login.AuthRequestMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;

import lombok.Getter;
import lombok.Setter;

public class DefaultAuthFailHandler implements IAuthMatcherFailHandler {

	@Getter @Setter private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	@Getter @Setter private ISpringRespGenerator springRespGenerator;
	
	
	public DefaultAuthFailHandler() {
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception,
			AuthRequestMatcher authRequestMatcher) throws IOException, ServletException {
		
		String requestWay = authRequestMatcher.getRequestWay();
		switch (requestWay) {
		case SecurityLoginConstants.RequestWay.FORM:
			onAuthenticationFailForForm(request, response, exception, authRequestMatcher);
			break;
		case SecurityLoginConstants.RequestWay.AJAX:
			onAuthenticationFailForAjax(response, exception, authRequestMatcher);
			break;
		case SecurityLoginConstants.RequestWay.API:
			onAuthenticationFailForApi(response, exception, authRequestMatcher);
			break;
		default:
			break;
		}
		
		throw new SecurityAuthException("登录成功，但不能确认具体的RequestWay");
		
	}
	
	private void onAuthenticationFailForForm(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception, AuthRequestMatcher authRequestMatcher) throws IOException {
		saveException(request, response, exception);
		String targetUrl = getTargetUrl(request, authRequestMatcher);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
		
	}

	protected void onAuthenticationFailForAjax(HttpServletResponse response, Throwable throwable,
			AuthRequestMatcher authRequestMatcher) throws IOException {
		
		LoginFailResp loginFailResp = new LoginFailResp();
		this.springRespGenerator.doResponse(response, HttpStatus.UNAUTHORIZED, throwable, loginFailResp);
		
	}
	
	protected void onAuthenticationFailForApi(HttpServletResponse response, Throwable throwable,
			AuthRequestMatcher authRequestMatcher) throws IOException {
		onAuthenticationFailForAjax(response, throwable, authRequestMatcher);
	}
	
	protected final void saveException(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
		CookieUtil.addCookie(response, WebAttributes.AUTHENTICATION_EXCEPTION, exception.getMessage(), -1);
	}

	private String getTargetUrl(HttpServletRequest request, AuthRequestMatcher authRequestMatcher) {
		String defaultTargetUrl = authRequestMatcher.getFail().getDefaultTargetUrl();
		if(StringUtils.isNotBlank(defaultTargetUrl)) {
			return defaultTargetUrl;
		}
		String targetUrl = getRequestPath(request);
		return targetUrl;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年11月25日 上午11:12:59
	 * @version 1.0
	 * @description 从AuthRequestMatcher中copy出 
	 * @param request
	 * @return
	 */
	private String getRequestPath(HttpServletRequest request) {
		String url = request.getServletPath();
		String pathInfo = request.getPathInfo();
		if (pathInfo != null) {
			url = StringUtils.isNotBlank(url) ? url + pathInfo : pathInfo;
		}
		return url;
	}
	

}
