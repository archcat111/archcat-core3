package org.cat.support.security3.generator.spring.exceptionhandling;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.core.web3.util.AjaxUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import lombok.Getter;
import lombok.Setter;

public class AccessDeniedAdaptiveHandler implements AccessDeniedHandler {

	@Getter @Setter private AccessDeniedHandler accessDeniedResponseBodyHandlerImpl = new AccessDeniedResponseBodyHandlerImpl();
	@Getter @Setter private AccessDeniedHandler accessDeniedRedirectHandlerImpl = new AccessDeniedRedirectHandlerImpl();
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		if(AjaxUtil.isAjaxRequest(request)) {
			accessDeniedResponseBodyHandlerImpl.handle(request, response, accessDeniedException);
		}else {
			accessDeniedRedirectHandlerImpl.handle(request, response, accessDeniedException);
		}
	}

}
