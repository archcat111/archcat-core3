package org.cat.support.security3.generator.spring.login.success;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.support.security3.generator.spring.constants.SecurityLoginConstants;
import org.cat.support.security3.generator.spring.exception.SecurityAuthException;
import org.cat.support.security3.generator.spring.login.AuthRequestMatcher;
import org.cat.support.security3.generator.spring.user.UrlAuthority;
import org.cat.support.security3.generator.spring.user.UserExt;
import org.cat.support.security3.generator.spring.user.resp.RoleResp;
import org.cat.support.security3.generator.spring.user.resp.UrlAuthorityResp;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

public class DefaultAuthSuccessHandler implements IAuthMatcherSuccessHandler {
	
	@Getter @Setter private ISpringRespGenerator springRespGenerator;
	
	@Getter @Setter private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	public DefaultAuthSuccessHandler() {
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication, AuthRequestMatcher authRequestMatcher) throws IOException, ServletException {
		
		UserExt userExt = (UserExt) authentication.getPrincipal();
		List<String> roles = userExt.getRoles();
		String targetUrl = getTargetUrl(request, authRequestMatcher, roles);
		
		String requestWay = authRequestMatcher.getRequestWay();
		switch (requestWay) {
		case SecurityLoginConstants.RequestWay.FORM:
			onAuthenticationSuccessForForm(request, response, targetUrl);
			break;
		case SecurityLoginConstants.RequestWay.AJAX:
			onAuthenticationSuccessForAjax(request, response, userExt);
			break;
		case SecurityLoginConstants.RequestWay.API:
			onAuthenticationSuccessForApi(request, response, userExt);
			break;
		default:
			break;
		}
		
		throw new SecurityAuthException("登录成功，但不能确认具体的RequestWay");
	}
	
	protected void onAuthenticationSuccessForForm(
			HttpServletRequest request, HttpServletResponse response,
			String targetUrl) throws IOException {
		
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
	
	protected void onAuthenticationSuccessForAjax(
			HttpServletRequest request, HttpServletResponse response,
			UserExt userExt) throws IOException, ServletException {
		
		Long code = userExt.getCode();
		String nickName = userExt.getNickName();
		String userName = userExt.getUsername();
		boolean accountExpired=!userExt.isAccountNonExpired();
		boolean accountLocked=!userExt.isAccountNonLocked();
		boolean credentialsExpired=!userExt.isCredentialsNonExpired();
		List<String> roles = userExt.getRoles();
		Collection<GrantedAuthority> authorities=userExt.getAuthorities();
		
		LoginSuccessResp loginResp=new LoginSuccessResp();
		
		loginResp.setCode(code==null?null:(code+""));
		loginResp.setNickName(nickName);
		loginResp.setUserName(userName);
		loginResp.setAccountExpired(accountExpired);
		loginResp.setAccountLocked(accountLocked);
		loginResp.setCredentialsExpired(credentialsExpired);
		
		List<RoleResp> roleResps = Lists.newArrayList();
		roles.forEach(role -> {
			RoleResp roleResp=new RoleResp();
			roleResp.setName(role);
			roleResps.add(roleResp);
		});
		loginResp.setRoles(roleResps);
		
		List<UrlAuthorityResp> authorityResps = Lists.newArrayList();
		for (GrantedAuthority grantedAuthority : authorities) {
			UrlAuthority urlAuthority=(UrlAuthority) grantedAuthority;
			UrlAuthorityResp urlAuthorityResp=new UrlAuthorityResp();
			urlAuthorityResp.setCode(urlAuthority.getCode());
			urlAuthorityResp.setType(urlAuthority.getType());
			urlAuthorityResp.setUrl(urlAuthority.getUrl());
			urlAuthorityResp.setData(urlAuthority.getData());
			authorityResps.add(urlAuthorityResp);
		}
		loginResp.setAuthorites(authorityResps);
		
		//返回结果
		springRespGenerator.doResponse(response, HttpStatus.OK, null, loginResp);
		
	}
	
	protected void onAuthenticationSuccessForApi(
			HttpServletRequest request, HttpServletResponse response,
			UserExt userExt) throws IOException, ServletException {
		onAuthenticationSuccessForAjax(request, response, userExt);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年11月25日 上午10:29:41
	 * @version 1.0
	 * @description 根据匹配本次登录的authRequestMatcher中的用户配置信息，判断应该返回的url
	 * @param request
	 * @param authRequestMatcher
	 * @param roles 从数据库获取的用户角色，如果有多个角色，则以第一个匹配role和backUrl的url为结果
	 * @return
	 */
	private String getTargetUrl(HttpServletRequest request, AuthRequestMatcher authRequestMatcher, List<String> roles) {
		String defaultTargetUrl = authRequestMatcher.getSuccess().getDefaultTargetUrl();
		boolean alwaysUseDefaultTargetUrl = authRequestMatcher.getSuccess().isAlwaysUseDefaultTargetUrl();
		String targetUrlParameter = authRequestMatcher.getSuccess().getTargetUrlParameter();
		boolean useReferer = authRequestMatcher.getSuccess().isUseReferer();
		Map<String, String> roleTargetUrlMap = authRequestMatcher.getSuccess().getRoleTargetUrl();
		
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
