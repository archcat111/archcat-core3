package org.cat.support.security3.generator.spring.jwt;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.support.security3.generator.spring.context.IJwtSecurityContextRepository;
import org.cat.support.security3.generator.spring.jwt.expired.IJwtExpiredStrategy;
import org.cat.support.security3.generator.spring.jwt.expired.JwtAdaptiveExpiredStrategyImpl;
import org.cat.support.security3.generator.spring.jwt.expired.JwtExpiredEventPublishingLogoutHandler;
import org.cat.support.security3.generator.spring.jwt.expired.JwtRedirectExpiredStrategyImpl;
import org.cat.support.security3.generator.spring.jwt.expired.JwtResponseBodyExpiredStrategyImpl;
import org.cat.support.security3.generator.spring.jwt.fail.IJwtVerifyFailStrategy;
import org.cat.support.security3.generator.spring.jwt.fail.JwtAdaptiveVerifyFailStrategyImpl;
import org.cat.support.security3.generator.spring.jwt.fail.JwtRedirectVerifyFailStrategyImpl;
import org.cat.support.security3.generator.spring.jwt.fail.JwtResponseBodyVerifyFailStrategyImpl;
import org.cat.support.security3.generator.spring.logout.handler.ContextLogoutHandler;
import org.cat.support.security3.generator.spring.logout.handler.JwtLogoutHandler;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.Setter;

public class JwtFilterConfigurer<H extends HttpSecurityBuilder<H>>
	extends AbstractHttpConfigurer<SessionManagementConfigurer<H>, H>{
	
	@Getter @Setter private ConcurrentJwtFilter concurrentJwtFilter = new ConcurrentJwtFilter();
	
	private IJwtSecurityContextRepository jwtSecurityContextRepository;
	@Getter @Setter private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	
	private ISpringRespGenerator springRespGenerator;
	//验证
	@Getter @Setter private IJwtVerifyFailStrategy jwtVerifyFailStrategy = new JwtAdaptiveVerifyFailStrategyImpl();
	private String jwtVerifyRedirectUrl = "/";
	//超时
	private List<LogoutHandler> expiredLogoutHandlers = new ArrayList<>(); //init-初始化
	private List<LogoutHandler> customExpiredLogoutHandlers = new ArrayList<>(); 
	@Getter @Setter private IJwtExpiredStrategy jwtExpiredStrategy = new JwtAdaptiveExpiredStrategyImpl();
	private String jwtExpiredRedirectUrl = "/";
	
	private String issuer = "default"; //jwt签发者
	private String subject = "default-platform"; //jwt所面向的用户，可以标注是面向什么平台
//	private String audience = "default"; //接收jwt的一方
	private long durationTimeSeconds = 60;
	private long refreshWindowSeconds = 5;
	private String headerParamterName = "Authorization";

	@Override
	public void init(H builder) throws Exception {
		initJwtVerifyFailStrategy();
		initExpiredLogoutHandlers();
		initJwtExpiredStrategy();
	}
	
	protected void initJwtVerifyFailStrategy() {
		if(this.jwtVerifyFailStrategy!=null && (this.jwtVerifyFailStrategy instanceof JwtAdaptiveVerifyFailStrategyImpl)) {
			if(StringUtils.isNotBlank(this.jwtVerifyRedirectUrl)) {
				Assert.hasText(this.jwtVerifyRedirectUrl, "jwtVerifyRedirectUrl不能为空");
				JwtAdaptiveVerifyFailStrategyImpl jwtAdaptiveVerifyFailStrategyImpl = (JwtAdaptiveVerifyFailStrategyImpl) this.jwtVerifyFailStrategy;
				JwtRedirectVerifyFailStrategyImpl jwtRedirectVerifyFailStrategyImpl = (JwtRedirectVerifyFailStrategyImpl) jwtAdaptiveVerifyFailStrategyImpl.getJwtRedirectVerifyFailStrategy();
				jwtRedirectVerifyFailStrategyImpl.setRedirectUrl(this.jwtVerifyRedirectUrl);
			}
			if(this.springRespGenerator!=null) {
				Assert.notNull(this.springRespGenerator, "springRespGenerator不能为null");
				JwtAdaptiveVerifyFailStrategyImpl jwtAdaptiveVerifyFailStrategyImpl = (JwtAdaptiveVerifyFailStrategyImpl) this.jwtVerifyFailStrategy;
				JwtResponseBodyVerifyFailStrategyImpl jwtResponseBodyVerifyFailStrategyImpl = (JwtResponseBodyVerifyFailStrategyImpl) jwtAdaptiveVerifyFailStrategyImpl.getJwtResponseBodyVerifyFailStrategy();
				jwtResponseBodyVerifyFailStrategyImpl.setSpringRespGenerator(this.springRespGenerator);
			}
		}
	}
	
	protected void initExpiredLogoutHandlers() {
		this.expiredLogoutHandlers.add(postProcess(new JwtLogoutHandler()));
		this.expiredLogoutHandlers.add(postProcess(new ContextLogoutHandler()));
		this.expiredLogoutHandlers.add(postProcess(new JwtExpiredEventPublishingLogoutHandler()));
		this.expiredLogoutHandlers.addAll(this.customExpiredLogoutHandlers);
	}
	
	protected void initJwtExpiredStrategy() {
		if(this.jwtExpiredStrategy!=null && (this.jwtExpiredStrategy instanceof JwtAdaptiveExpiredStrategyImpl)) {
			if(StringUtils.isNotBlank(this.jwtExpiredRedirectUrl)) {
				Assert.hasText(this.jwtExpiredRedirectUrl, "jwtExpiredRedirectUrl不能为空");
				JwtAdaptiveExpiredStrategyImpl jwtAdaptiveExpiredStrategyImpl = (JwtAdaptiveExpiredStrategyImpl) this.jwtExpiredStrategy;
				JwtRedirectExpiredStrategyImpl jwtRedirectExpiredStrategyImpl = (JwtRedirectExpiredStrategyImpl) jwtAdaptiveExpiredStrategyImpl.getJwtRedirectExpiredStrategy();
				jwtRedirectExpiredStrategyImpl.setRedirectUrl(this.jwtExpiredRedirectUrl);
			}
			if(this.springRespGenerator!=null) {
				Assert.notNull(this.springRespGenerator, "springRespGenerator不能为null");
				JwtAdaptiveExpiredStrategyImpl jwtAdaptiveExpiredStrategyImpl = (JwtAdaptiveExpiredStrategyImpl) this.jwtExpiredStrategy;
				JwtResponseBodyExpiredStrategyImpl jwtResponseBodyExpiredStrategyImpl = (JwtResponseBodyExpiredStrategyImpl) jwtAdaptiveExpiredStrategyImpl.getJwtResponseBodyExpiredStrategy();
				jwtResponseBodyExpiredStrategyImpl.setSpringRespGenerator(this.springRespGenerator);
			}
		}
	}

	@Override
	public void configure(H builder) throws Exception {
		Assert.notNull(this.jwtSecurityContextRepository, "jwtSecurityContextRepository不能为null");
		this.concurrentJwtFilter.setJwtSecurityContextRepository(this.jwtSecurityContextRepository);
		
		if(this.trustResolver!=null) {
			this.concurrentJwtFilter.setTrustResolver(this.trustResolver);
		}
		
		if(this.jwtVerifyFailStrategy!=null) {
			this.concurrentJwtFilter.setJwtVerifyFailStrategy(this.jwtVerifyFailStrategy);
		}
		
		this.concurrentJwtFilter.setExpiredLogoutHandlers(this.expiredLogoutHandlers);
		if(this.jwtExpiredStrategy!=null) {
			this.concurrentJwtFilter.setJwtExpiredStrategy(this.jwtExpiredStrategy);
		}
		
		if(StringUtils.isNotBlank(this.issuer)) {
			this.concurrentJwtFilter.setIssuer(this.issuer);
		}
		if(StringUtils.isNotBlank(this.subject)) {
			this.concurrentJwtFilter.setSubject(this.subject);
		}
		if(this.durationTimeSeconds>0) {
			this.concurrentJwtFilter.setDurationTimeSeconds(this.durationTimeSeconds);
		}
		if(this.refreshWindowSeconds>0) {
			this.concurrentJwtFilter.setRefreshWindowSeconds(this.refreshWindowSeconds);
		}
		
		Assert.hasText(this.headerParamterName, "headerParamterName不能为空");
		this.concurrentJwtFilter.setHeaderParamterName(this.headerParamterName);
		
		builder.addFilterBefore(this.concurrentJwtFilter, SessionManagementFilter.class);
	}
	
	public JwtFilterConfigurer<H> setJwtSecurityContextRepository(IJwtSecurityContextRepository jwtSecurityContextRepository) {
		this.jwtSecurityContextRepository = jwtSecurityContextRepository;
		return this;
	}
	
	public JwtFilterConfigurer<H> setJwtSpringRespGenerator(ISpringRespGenerator springRespGenerator) {
		this.springRespGenerator = springRespGenerator;
		return this;
	}
	
	public JwtFilterConfigurer<H> setJwtVerifyRedirectUrl(String jwtVerifyRedirectUrl) {
		this.jwtVerifyRedirectUrl = jwtVerifyRedirectUrl;
		return this;
	}
	
	public JwtFilterConfigurer<H> addCumstomExpiredLogoutHandler(LogoutHandler logoutHandler) {
		this.customExpiredLogoutHandlers.add(logoutHandler);
		return this;
	}

	public JwtFilterConfigurer<H> setJwtExpiredRedirectUrl(String jwtExpiredRedirectUrl) {
		this.jwtExpiredRedirectUrl = jwtExpiredRedirectUrl;
		return this;
	}
	
	public JwtFilterConfigurer<H> setIssuer(String issuer) {
		this.issuer = issuer;
		return this;
	}
	
	public JwtFilterConfigurer<H> setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	
	public JwtFilterConfigurer<H> setDurationTimeSeconds(long durationTimeSeconds) {
		this.durationTimeSeconds = durationTimeSeconds;
		return this;
	}
	
	public JwtFilterConfigurer<H> setRefreshWindowSeconds(long refreshWindowSeconds) {
		this.refreshWindowSeconds = refreshWindowSeconds;
		return this;
	}
	
	public JwtFilterConfigurer<H> setHeaderParamterName(String headerParamterName) {
		this.headerParamterName = headerParamterName;
		return this;
	}
	
}
