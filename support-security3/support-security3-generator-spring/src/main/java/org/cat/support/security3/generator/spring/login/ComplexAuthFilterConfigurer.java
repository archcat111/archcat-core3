package org.cat.support.security3.generator.spring.login;

import java.util.LinkedHashMap;

import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.support.security3.generator.spring.core.ComplexRequestMatcher;
import org.cat.support.security3.generator.spring.login.fail.ComplexAuthFailHandler;
import org.cat.support.security3.generator.spring.login.fail.DefaultAuthFailHandler;
import org.cat.support.security3.generator.spring.login.fail.IAuthMatcherFailHandler;
import org.cat.support.security3.generator.spring.login.success.ComplexAuthSuccessHandler;
import org.cat.support.security3.generator.spring.login.success.DefaultAuthSuccessHandler;
import org.cat.support.security3.generator.spring.login.success.IAuthMatcherSuccessHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.util.Assert;

/**
 * 
 * @author 王云龙
 * @date 2021年11月26日 下午2:17:25
 * @version 1.0
 * @description 
 * 		用于代替DefaultLoginPageConfigurer和FormLoginConfigurer
 * 
 * 		使用前提：
 * 			httpSecurity.removeConfigurer(DefaultLoginPageConfigurer.class); //关闭默认登录
 * 			如果httpSecurity.formLogin()，则必须httpSecurity.formLogin().disable()
 * 		使用：
 * 			httpSecurity.apply(new ComplexAuthFilterConfigurer<>())
 * 				.setComplexAuthRequestMatcher(null) //必须
 * 				.setAuthenticationManager(null)
 * 				.setAuthenticationDetailsSource(null) //必须
 * 				.setSessionStrategy(null)
 * 				.setContinueChainBeforeSuccessfulAuthentication(false)
 * 				.setRememberMeServices(null)
 * 				.setEventPublisher(null)
 * 				.setComplexAuthFailureHandler(null); //必须
 *
 * @param <T>
 * @param <B>
 */
public class ComplexAuthFilterConfigurer<T extends ComplexAuthFilterConfigurer<T, B>, B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<T, B> {
	
	private ComplexAuthFilter complexAuthFilter;
	
	private ComplexRequestMatcher<AuthRequestMatcher> complexAuthRequestMatcher;
	private AuthenticationManager authenticationManager;
	
	private DynamicParameterAuthDetailsSource authenticationDetailsSource;
	private SessionAuthenticationStrategy sessionStrategy;
	private boolean continueChainBeforeSuccessfulAuthentication = false;
	
	private RememberMeServices rememberMeServices;
	private ApplicationEventPublisher eventPublisher;
	
	private ISpringRespGenerator springRespGenerator;
	
	private ComplexAuthSuccessHandler complexAuthSuccessHandler;
	private DefaultAuthSuccessHandler defaultAuthSuccessHandler;
	private LinkedHashMap<AuthRequestMatcher, IAuthMatcherSuccessHandler> extendMatcherToSuccessHandler;
	
	private ComplexAuthFailHandler complexAuthFailHandler;
	private DefaultAuthFailHandler defaultAuthFailHandler;
	private LinkedHashMap<AuthRequestMatcher, IAuthMatcherFailHandler> extendMatcherToFailHandler;
	
	

	public ComplexAuthFilterConfigurer() {
	}
	
	@Override
	public void init(B builder) throws Exception {
		if(this.authenticationManager == null) {
			this.authenticationManager = builder.getSharedObject(AuthenticationManager.class);
		}
		
		Assert.notNull(this.complexAuthRequestMatcher, "complexAuthRequestMatcher不能为null");
		Assert.notNull(this.authenticationManager, "authenticationManager不能为null");
		this.complexAuthFilter = new ComplexAuthFilter(this.complexAuthRequestMatcher, this.authenticationManager);
	
		if(this.sessionStrategy==null) {
			this.sessionStrategy = new NullAuthenticatedSessionStrategy();
		}
		
		if(this.rememberMeServices!=null) {
			this.rememberMeServices = new NullRememberMeServices();
		}
		
		initAuthSuccessHandler();
		initAuthFailHandler();
	}
	
	protected void initAuthSuccessHandler() {
		initDefaultAuthSuccessHandler();
		initComplexAuthSuccessHandler();
	}
	
	protected void initDefaultAuthSuccessHandler() {
		if(this.defaultAuthSuccessHandler==null) {
			this.defaultAuthSuccessHandler = new DefaultAuthSuccessHandler();
			Assert.notNull(this.springRespGenerator, "springRespGenerator不能为null");
		}
		if(this.defaultAuthSuccessHandler.getSpringRespGenerator()==null) {
			this.defaultAuthSuccessHandler.setSpringRespGenerator(this.springRespGenerator);
		}
	}
	
	protected void initComplexAuthSuccessHandler() {
		if(this.complexAuthSuccessHandler==null) {
			Assert.notNull(this.complexAuthRequestMatcher, "complexLogoutRequestMatcher不能为null");
			this.complexAuthSuccessHandler = new ComplexAuthSuccessHandler(this.complexAuthRequestMatcher);
		}
	}
	
	protected void initAuthFailHandler() {
		initDefaultAuthFailHandler();
		initComplexAuthFailHandler();
	}
	
	protected void initDefaultAuthFailHandler() {
		if(this.defaultAuthFailHandler==null) {
			this.defaultAuthFailHandler = new DefaultAuthFailHandler();
			Assert.notNull(this.springRespGenerator, "springRespGenerator不能为null");
			this.defaultAuthFailHandler.setSpringRespGenerator(this.springRespGenerator);
		}
	}
	
	protected void initComplexAuthFailHandler() {
		if(this.complexAuthFailHandler==null) {
			Assert.notNull(this.complexAuthRequestMatcher, "complexLogoutRequestMatcher不能为null");
			this.complexAuthFailHandler = new ComplexAuthFailHandler(this.complexAuthRequestMatcher);
		}
	}
	

	@Override
	public void configure(B builder) throws Exception {
		
		this.complexAuthFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
		
		this.complexAuthFilter.setSessionAuthenticationStrategy(this.sessionStrategy);
		this.complexAuthFilter.setContinueChainBeforeSuccessfulAuthentication(this.continueChainBeforeSuccessfulAuthentication);
		this.complexAuthFilter.setRememberMeServices(this.rememberMeServices);
		this.complexAuthFilter.setApplicationEventPublisher(this.eventPublisher);
		
		Assert.notNull(this.complexAuthSuccessHandler, "complexAuthSuccessHandler不能为null");
		Assert.notNull(this.defaultAuthSuccessHandler, "defaultAuthSuccessHandler不能为null");
		this.complexAuthSuccessHandler.setDefaultAuthSuccessHandler(this.defaultAuthSuccessHandler);
		this.complexAuthSuccessHandler.setExtendMatcherToSuccessHandler(this.extendMatcherToSuccessHandler);
		this.complexAuthFilter.setAuthenticationSuccessHandler(this.complexAuthSuccessHandler);
		
		Assert.notNull(this.complexAuthFailHandler, "complexAuthFailHandler不能为null");
		Assert.notNull(this.defaultAuthFailHandler, "defaultAuthFailHandler不能为null");
		this.complexAuthFailHandler.setDefaultAuthFailHandler(this.defaultAuthFailHandler);
		this.complexAuthFailHandler.setExtendMatcherToFailHandler(this.extendMatcherToFailHandler);
		this.complexAuthFilter.setAuthenticationFailureHandler(this.complexAuthFailHandler);
		
		builder.addFilterBefore(this.complexAuthFilter, UsernamePasswordAuthenticationFilter.class);
	}

	public ComplexAuthFilterConfigurer<T, B> setComplexAuthRequestMatcher(ComplexRequestMatcher<AuthRequestMatcher> complexAuthRequestMatcher) {
		this.complexAuthRequestMatcher = complexAuthRequestMatcher;
		return this;
	}
	
	public ComplexAuthFilterConfigurer<T, B> setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
		return this;
	}

	public ComplexAuthFilterConfigurer<T, B> setAuthenticationDetailsSource(DynamicParameterAuthDetailsSource authenticationDetailsSource) {
		this.authenticationDetailsSource = authenticationDetailsSource;
		return this;
	}

	public ComplexAuthFilterConfigurer<T, B> setSessionStrategy(SessionAuthenticationStrategy sessionStrategy) {
		this.sessionStrategy = sessionStrategy;
		return this;
	}

	public ComplexAuthFilterConfigurer<T, B> setContinueChainBeforeSuccessfulAuthentication(boolean continueChainBeforeSuccessfulAuthentication) {
		this.continueChainBeforeSuccessfulAuthentication = continueChainBeforeSuccessfulAuthentication;
		return this;
	}

	public ComplexAuthFilterConfigurer<T, B> setRememberMeServices(RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
		return this;
	}

	public ComplexAuthFilterConfigurer<T, B> setEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		return this;
	}
	
	public ComplexAuthFilterConfigurer<T, B> setSpringGenerator(ISpringRespGenerator springRespGenerator){
		this.springRespGenerator = springRespGenerator;
		return this;
	}
	
	public ComplexAuthFilterConfigurer<T, B> setDefaultAuthSuccessHandler(DefaultAuthSuccessHandler defaultAuthSuccessHandler) {
		this.defaultAuthSuccessHandler = defaultAuthSuccessHandler;
		return this;
	}
	
	public ComplexAuthFilterConfigurer<T, B> setDefaultAuthFailHandler(DefaultAuthFailHandler defaultAuthFailHandler){
		this.defaultAuthFailHandler = defaultAuthFailHandler;
		return this;
	}
	
	public ComplexAuthFilterConfigurer<T, B> addExtendMatcherToSuccessHandler(
		AuthRequestMatcher authRequestMatcher, IAuthMatcherSuccessHandler authenticationSuccessHandler) {
		this.extendMatcherToSuccessHandler.put(authRequestMatcher, authenticationSuccessHandler);
		return this;
	}

	public ComplexAuthFilterConfigurer<T, B> addExtendMatcherToFailHandler(
		AuthRequestMatcher authRequestMatcher, IAuthMatcherFailHandler authMatcherFailHandler) {
		this.extendMatcherToFailHandler.put(authRequestMatcher, authMatcherFailHandler);
		return this;
	}
	
	
}
