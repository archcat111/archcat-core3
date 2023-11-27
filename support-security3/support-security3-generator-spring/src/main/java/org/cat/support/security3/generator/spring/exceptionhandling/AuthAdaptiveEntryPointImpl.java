package org.cat.support.security3.generator.spring.exceptionhandling;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.core.web3.util.AjaxUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.Getter;
import lombok.Setter;

public class AuthAdaptiveEntryPointImpl implements AuthenticationEntryPoint {

	@Getter @Setter private AuthenticationEntryPoint authResponseBodyEntryPointImpl = new AuthResponseBodyEntryPointImpl();
	@Getter @Setter private AuthenticationEntryPoint authRedirectEntryPointImpl = new AuthRedirectEntryPointImpl();
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		if(AjaxUtil.isAjaxRequest(request)) {
			authResponseBodyEntryPointImpl.commence(request, response, authException);
		}else {
			authRedirectEntryPointImpl.commence(request, response, authException);
		}

	}

}
