package org.cat.support.security3.generator.spring.exceptionhandling;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import lombok.Getter;
import lombok.Setter;

public class AccessDeniedResponseBodyHandlerImpl implements AccessDeniedHandler {
	
	@Getter @Setter private ISpringRespGenerator springRespGenerator;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		String responseResult = "您没有权限";
		
		springRespGenerator.doResponse(response, HttpStatus.UNAUTHORIZED, accessDeniedException, responseResult);

	}

}
