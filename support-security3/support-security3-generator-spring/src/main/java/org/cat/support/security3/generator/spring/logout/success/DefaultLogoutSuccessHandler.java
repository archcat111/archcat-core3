package org.cat.support.security3.generator.spring.logout.success;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.support.security3.generator.spring.constants.SecurityLogoutConstants;
import org.cat.support.security3.generator.spring.exception.SecurityLogoutException;
import org.cat.support.security3.generator.spring.logout.LogoutRequestMatcher;
import org.cat.support.security3.generator.spring.user.UserExt;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import lombok.Getter;
import lombok.Setter;

public class DefaultLogoutSuccessHandler implements LogoutSuccessHandler {
	
	@Getter @Setter private LogoutRequestMatcher logoutRequestMatcher;
	@Getter @Setter private ISpringRespGenerator springRespGenerator;
	
	@Getter @Setter private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	public DefaultLogoutSuccessHandler() {
	}
	
	public DefaultLogoutSuccessHandler(LogoutRequestMatcher logoutRequestMatcher) {
		this.logoutRequestMatcher = logoutRequestMatcher;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		UserExt userExt = (UserExt) authentication.getPrincipal();
		List<String> roles = userExt.getRoles();
		String targetUrl = getTargetUrl(request, roles);
		
		String requestWay = this.logoutRequestMatcher.getRequestWay();
		switch (requestWay) {
		case SecurityLogoutConstants.RequestWay.FORM:
			onLogoutSuccessForForm(request, response, targetUrl);
			break;
		case SecurityLogoutConstants.RequestWay.AJAX:
			onLogoutSuccessForAjax(request, response, userExt);
			break;
		case SecurityLogoutConstants.RequestWay.API:
			onLogoutSuccessForApi(request, response, userExt);
			break;
		default:
			throw new SecurityLogoutException("Logout时请配置合理的RequestWay");
		}

	}
	
	protected void onLogoutSuccessForForm(
			HttpServletRequest request, HttpServletResponse response,
			String targetUrl) throws IOException {
		
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
	
	protected void onLogoutSuccessForAjax(
			HttpServletRequest request, HttpServletResponse response,
			UserExt userExt) throws IOException, ServletException {
		
		//返回结果
		springRespGenerator.doResponse(response, HttpStatus.OK, null, null);
		
	}
	
	protected void onLogoutSuccessForApi(
			HttpServletRequest request, HttpServletResponse response,
			UserExt userExt) throws IOException, ServletException {
		onLogoutSuccessForAjax(request, response, userExt);
	}
	
	private String getTargetUrl(HttpServletRequest request, List<String> roles) {
		String defaultTargetUrl = this.logoutRequestMatcher.getSuccess().getDefaultTargetUrl();
		boolean alwaysUseDefaultTargetUrl = this.logoutRequestMatcher.getSuccess().isAlwaysUseDefaultTargetUrl();
		String targetUrlParameter = this.logoutRequestMatcher.getSuccess().getTargetUrlParameter();
		boolean useReferer = this.logoutRequestMatcher.getSuccess().isUseReferer();
		Map<String, String> roleTargetUrlMap = this.logoutRequestMatcher.getSuccess().getRoleTargetUrl();
		
		if(alwaysUseDefaultTargetUrl) {
			return defaultTargetUrl;
		}
		String targetUrlValue = request.getParameter(targetUrlParameter);
		if(StringUtils.isNotBlank(targetUrlValue)) {
			return targetUrlValue;
		}
		if(!roles.isEmpty()) {
			for (String role : roles) {
				String roleTargetUrl = roleTargetUrlMap.get(role);
				if(StringUtils.isNotBlank(roleTargetUrl)) {
					return roleTargetUrl;
				}
			}
		}
		if(useReferer) {
			String targetUrl = request.getHeader(HttpHeaders.REFERER);
			if(StringUtils.isNotBlank(targetUrl)) {
				return targetUrl;
			}
		}
		return defaultTargetUrl;
	}

}
