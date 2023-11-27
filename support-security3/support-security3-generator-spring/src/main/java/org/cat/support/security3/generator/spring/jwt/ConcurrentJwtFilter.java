package org.cat.support.security3.generator.spring.jwt;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.context.IJwtSecurityContextRepository;
import org.cat.support.security3.generator.spring.exception.SecurityJwtVerifyException;
import org.cat.support.security3.generator.spring.jwt.expired.IJwtExpiredStrategy;
import org.cat.support.security3.generator.spring.jwt.expired.JwtAdaptiveExpiredStrategyImpl;
import org.cat.support.security3.generator.spring.jwt.expired.JwtExpiredEvent;
import org.cat.support.security3.generator.spring.jwt.fail.IJwtVerifyFailStrategy;
import org.cat.support.security3.generator.spring.jwt.fail.JwtAdaptiveVerifyFailStrategyImpl;
import org.cat.support.security3.generator.spring.logout.handler.ContextLogoutHandler;
import org.cat.support.security3.generator.spring.user.UserExt;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.filter.GenericFilterBean;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import lombok.Getter;
import lombok.Setter;

public class ConcurrentJwtFilter extends GenericFilterBean {
	
	private static final String FILTER_APPLIED = "__spring_security_session_mgmt_filter_applied";
	@Getter @Setter private IJwtSecurityContextRepository jwtSecurityContextRepository;
	@Getter @Setter private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	
	private IJwtVerifyFailStrategy jwtVerifyFailStrategy = new JwtAdaptiveVerifyFailStrategyImpl();
	private LogoutHandler expiredLogoutHandler = new CompositeLogoutHandler(new ContextLogoutHandler());
	private IJwtExpiredStrategy jwtExpiredStrategy = new JwtAdaptiveExpiredStrategyImpl();
	
	@Getter @Setter private String issuer = "default"; //jwt签发者
	@Getter @Setter private String subject = "default-platform"; //jwt所面向的用户，可以标注是面向什么平台
//	private String audience = "default"; //接收jwt的一方
	@Getter @Setter private long durationTimeSeconds = 60;
	@Getter @Setter private long refreshWindowSeconds = 5;
	@Getter @Setter private String headerParamterName = "Authorization";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request.getAttribute(FILTER_APPLIED) != null) {
			chain.doFilter(request, response);
			return;
		}
		request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
		
//		SecurityContext securityContext = SecurityContextHolder.getContext();
//		Authentication authentication = securityContext.getAuthentication();
//		UserExt userExt = (UserExt) authentication.getPrincipal();
//		String dbPassword = userExt.getPassword();
		
		String jwtToken = request.getHeader(this.headerParamterName);
		if(jwtToken!=null) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && !this.trustResolver.isAnonymous(authentication)) {
				//验证合法性
				UserExt userExt = (UserExt) authentication.getPrincipal();
				String userCode = userExt.getCode()+"";
				String dbPassword = userExt.getPassword();
				
				DecodedJWT jwt = JWT.decode(jwtToken);
				try {
					Algorithm algorithm = Algorithm.HMAC256(dbPassword);
					JWTVerifier verifier = JWT.require(algorithm)
					        .withIssuer(this.issuer)
					        .withSubject(this.subject)
					        .withAudience(userCode)
					        .ignoreIssuedAt()
					        .build();
					verifier.verify(jwtToken);
				} catch (IllegalArgumentException | JWTVerificationException e) {
					//验证失败
					SecurityJwtVerifyException securityJwtVerifyException = new SecurityJwtVerifyException("jwt验证失败", e);
					jwtVerifyFailStrategy.onVerifyFailure(request, response, securityJwtVerifyException);
					return;
				}
				
				//验证是否过期
				Date expiresAtDate = jwt.getExpiresAt();
				Date nowDate = new Date();
				if(expiresAtDate.before(nowDate)) {//过期
					doExpiredLogoutHandler(request, response);
					this.jwtExpiredStrategy.onExpiredSessionDetected(new JwtExpiredEvent(jwtToken, request, response, jwtToken));
					return;
				}
				//刷新过期时间
				if(expiresAtDate.getTime() - nowDate.getTime()>this.refreshWindowSeconds*60*1000) {
					this.jwtSecurityContextRepository.refreshSecurityContext(jwtToken);
				}
			}
			
		}
		chain.doFilter(request, response);
	}
	
	private void doExpiredLogoutHandler(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		this.expiredLogoutHandler.logout(request, response, auth);
	}
	
	public void setJwtVerifyFailStrategy(IJwtVerifyFailStrategy jwtVerifyFailStrategy) {
		this.jwtVerifyFailStrategy = jwtVerifyFailStrategy;
	}
	
	public void setExpiredLogoutHandlers(LogoutHandler[] handlers) {
		this.expiredLogoutHandler = new CompositeLogoutHandler(handlers);
	}
	
	public void setExpiredLogoutHandlers(List<LogoutHandler> handlers) {
		this.expiredLogoutHandler = new CompositeLogoutHandler(handlers);
	}
	
	public void setJwtExpiredStrategy(IJwtExpiredStrategy jwtExpiredStrategy) {
		this.jwtExpiredStrategy = jwtExpiredStrategy;
	}
}
