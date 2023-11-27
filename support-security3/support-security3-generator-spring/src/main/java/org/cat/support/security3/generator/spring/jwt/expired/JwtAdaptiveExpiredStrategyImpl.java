package org.cat.support.security3.generator.spring.jwt.expired;

import java.io.IOException;

import javax.servlet.ServletException;

import org.cat.core.web3.util.AjaxUtil;

import lombok.Getter;
import lombok.Setter;

public class JwtAdaptiveExpiredStrategyImpl implements IJwtExpiredStrategy {
	
	@Getter @Setter private IJwtExpiredStrategy jwtResponseBodyExpiredStrategy = new JwtResponseBodyExpiredStrategyImpl();
	@Getter @Setter private IJwtExpiredStrategy jwtRedirectExpiredStrategy = new JwtRedirectExpiredStrategyImpl();
	
	@Override
	public void onExpiredSessionDetected(JwtExpiredEvent event) throws IOException, ServletException {
		if(AjaxUtil.isAjaxRequest(event.getRequest())) {
			jwtResponseBodyExpiredStrategy.onExpiredSessionDetected(event);
		}else {
			jwtRedirectExpiredStrategy.onExpiredSessionDetected(event);
		}
	}

}
