package org.cat.support.security3.generator.spring.login.success;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.login.AuthRequestMatcher;
import org.cat.support.security3.generator.spring.user.UserExt;
import org.springframework.security.core.Authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.Getter;
import lombok.Setter;

public class JwtDefaultAuthSuccessHandler extends DefaultAuthSuccessHandler {
	
	@Getter @Setter private String issuer = "default"; //jwt签发者
	@Getter @Setter private String subject = "default-platform"; //jwt所面向的用户，可以标注是面向什么平台
//	private String audience = "default"; //接收jwt的一方
	@Getter @Setter private long durationTimeSeconds = 60;
	@Getter @Setter private String headerParamterName = "Authorization";
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication, AuthRequestMatcher authRequestMatcher) throws IOException, ServletException {
		
		UserExt userExt = (UserExt) authentication.getPrincipal();
		String dbPassword = userExt.getPassword();
		
		long currentTimeMillis = System.currentTimeMillis();
		long durationTimeMillis = currentTimeMillis + this.durationTimeSeconds*60*1000;
		
		Algorithm algorithm = Algorithm.HMAC256(dbPassword);
		JWTCreator.Builder builder = JWT.create()
                .withIssuer(this.issuer)
                .withSubject(this.subject)
                .withAudience(userExt.getCode()+"")
                .withIssuedAt(new Date(currentTimeMillis))
                .withExpiresAt(new Date(durationTimeMillis));
        String jwtToken = builder.sign(algorithm);
        
		response.setHeader(this.headerParamterName, jwtToken);
		
		super.onAuthenticationSuccess(request, response, authentication, authRequestMatcher);
	}
	

}
