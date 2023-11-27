package org.cat.support.security3.generator.spring.jwt.fail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.support.security3.generator.spring.exception.SecurityJwtVerifyException;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

public class JwtResponseBodyVerifyFailStrategyImpl implements IJwtVerifyFailStrategy {
	
	@Getter @Setter private ISpringRespGenerator springRespGenerator;

	@Override
	public void onVerifyFailure(HttpServletRequest request, HttpServletResponse response,
			SecurityJwtVerifyException exception) throws IOException, ServletException {
		String responseResult = "his jwt has been verify failure";
		
		springRespGenerator.doResponse(response, HttpStatus.UNAUTHORIZED, exception, responseResult);
		
	}

}
