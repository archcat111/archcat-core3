package org.cat.support.security3.generator.spring.jwt.fail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.core.web3.util.AjaxUtil;
import org.cat.support.security3.generator.spring.exception.SecurityJwtVerifyException;

import lombok.Getter;
import lombok.Setter;

public class JwtAdaptiveVerifyFailStrategyImpl implements IJwtVerifyFailStrategy {
	
	@Getter @Setter private IJwtVerifyFailStrategy jwtResponseBodyVerifyFailStrategy = new JwtResponseBodyVerifyFailStrategyImpl();
	@Getter @Setter private IJwtVerifyFailStrategy jwtRedirectVerifyFailStrategy = new JwtRedirectVerifyFailStrategyImpl();
	
	@Override
	public void onVerifyFailure(HttpServletRequest request, HttpServletResponse response,
			SecurityJwtVerifyException exception) throws IOException, ServletException {
		if(AjaxUtil.isAjaxRequest(request)) {
			jwtResponseBodyVerifyFailStrategy.onVerifyFailure(request, response, exception);
		}else {
			jwtRedirectVerifyFailStrategy.onVerifyFailure(request, response, exception);
		}
		
	}

}
