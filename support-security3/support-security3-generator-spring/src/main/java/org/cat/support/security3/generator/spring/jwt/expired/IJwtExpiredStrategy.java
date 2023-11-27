package org.cat.support.security3.generator.spring.jwt.expired;

import java.io.IOException;

import javax.servlet.ServletException;

public interface IJwtExpiredStrategy {
	void onExpiredSessionDetected(JwtExpiredEvent event) throws IOException, ServletException;
}
