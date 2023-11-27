package org.cat.support.security3.generator.spring.jwt.expired;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

public class JwtResponseBodyExpiredStrategyImpl implements IJwtExpiredStrategy {
	
	@Getter @Setter private ISpringRespGenerator springRespGenerator;

	@Override
	public void onExpiredSessionDetected(JwtExpiredEvent event) throws IOException, ServletException {
		HttpServletResponse response = event.getResponse();
		String responseResult = "his jwt has been expired";
		
		springRespGenerator.doResponse(response, HttpStatus.UNAUTHORIZED, null, responseResult);

	}

}
