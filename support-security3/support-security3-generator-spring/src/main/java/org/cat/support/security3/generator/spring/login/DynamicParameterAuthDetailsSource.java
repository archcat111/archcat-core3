package org.cat.support.security3.generator.spring.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import lombok.Setter;

public class DynamicParameterAuthDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, DynamicParameterAuthDetails> {

	@Setter private AuthRequestMatcher authRequestMatcher;
	
	@Override
	public DynamicParameterAuthDetails buildDetails(HttpServletRequest httpServletRequest) {
		DynamicParameterAuthDetails DynamicParameterAuthDetails = new DynamicParameterAuthDetails(httpServletRequest, authRequestMatcher);
		return DynamicParameterAuthDetails;
	}

}
