package org.cat.support.security3.generator.spring.exceptionhandling;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.Getter;
import lombok.Setter;

public class AuthResponseBodyEntryPointImpl implements AuthenticationEntryPoint {

	@Getter @Setter private ISpringRespGenerator springRespGenerator;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		String responseResult = "请您先登录";
		
		springRespGenerator.doResponse(response, HttpStatus.UNAUTHORIZED, authException, responseResult);

	}

}
