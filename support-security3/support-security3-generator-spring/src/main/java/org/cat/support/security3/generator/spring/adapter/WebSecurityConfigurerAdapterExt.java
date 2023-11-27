package org.cat.support.security3.generator.spring.adapter;

import java.util.List;
import java.util.Set;

import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.support.security3.generator.spring.context.IJwtSecurityContextRepository;
import org.cat.support.security3.generator.spring.core.ComplexRequestMatcher;
import org.cat.support.security3.generator.spring.jwt.JwtFilterConfigurer;
import org.cat.support.security3.generator.spring.login.AuthRequestMatcher;
import org.cat.support.security3.generator.spring.login.ComplexAuthFilterConfigurer;
import org.cat.support.security3.generator.spring.login.DynamicParameterAuthDetailsSource;
import org.cat.support.security3.generator.spring.logout.ComplexLogoutFilterConfigurer;
import org.cat.support.security3.generator.spring.logout.LogoutRequestMatcher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.annotation.web.configurers.SecurityContextConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年11月30日 上午10:58:53
 * @version 1.0
 * @description 
 * 		自定义登录：
 * 		JWT：
 * 			httpSecurity.sessionManagement().disable();
 * 			
 * 		
 *
 */
public class WebSecurityConfigurerAdapterExt extends WebSecurityConfigurerAdapter {
	
	//SecurityContextPersistenceFilter
	@Setter private SecurityContextRepository securityContextRepository;
	
	//HeaderWriterFilter
	
	//CorsFilter
	@Setter private boolean openCors = false;
	@Setter private CorsConfigurationSource corsConfigurationSource;
	
	//CsrfFilter
	@Setter private boolean openCsrf = true;
	
	//LogoutFilter
	@Setter private List<LogoutRequestMatcher> logoutRequestMatchers;
	@Setter private ISpringRespGenerator logoutSpringRespGenerator;
	@Setter private List<LogoutHandler> logoutHandlers;
	
	//UsernamePasswordAuthenticationFilter
	@Setter private List<AuthRequestMatcher> authRequestMatchers;
	@Setter private DynamicParameterAuthDetailsSource authDynamicParameterAuthDetailsSource;
	@Setter private Set<AuthenticationProvider> authProviders;
	@Setter private ISpringRespGenerator authSpringRespGenerator;
	
	//RequestCacheAwareFilter
	@Setter private boolean openRequestCache;
	@Setter private RequestCache requestCache;
	
	//AnonymousAuthenticationFilter
	
	//SessionManagementFilter
	@Setter boolean openSession = false;

	//ConcurrentJwtFilter
	@Setter boolean openJwt = false;
	@Setter private ISpringRespGenerator jwtSpringRespGenerator;
	@Setter private String jwtVerifyRedirectUrl;
	@Setter private String jwtExpiredRedirectUrl;
	@Setter private String issuer = "default"; //jwt签发者
	@Setter private String subject = "default-platform"; //jwt所面向的用户，可以标注是面向什么平台
//	@Setter private String audience = "default"; //接收jwt的一方
	@Setter private long durationTimeSeconds = 60;
	@Setter private long refreshWindowSeconds = 5;
	@Setter private String headerParamterName = "Authorization";
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		/**
		 * 覆盖此方法以配置 {@link WebSecurity}。 例如，如果您希望忽略某些请求
		 * Spring Security 将忽略此方法中指定的端点，这意味着它不会保护它们免受 CSRF、XSS、Clickjacking 等的影响。
		 * 
		 * 相反，如果您想保护端点免受常见漏洞的影响，
		 * 请参阅 {@link #configure(HttpSecurity)} 和 {@link HttpSecurity#authorizeRequests} 配置方法
		 */
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		this.authProviders.forEach(authProvider -> {
			authenticationManagerBuilder.authenticationProvider(authProvider);
		});
//		authenticationManagerBuilder.setSharedObject(null, null);
//		//不能调用，否则就不会用这里自定义的authenticationManagerBuilder来创建AuthenticationManager了
		//super.configure(auth); 
	}



	@SuppressWarnings("unchecked")
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		///////////////////////////////SecurityContextPersistenceFilter（默认开启）
		SecurityContextConfigurer<HttpSecurity> securityContextConfigurer = httpSecurity.securityContext();
		securityContextConfigurer.securityContextRepository(this.securityContextRepository);
//		securityContextConfigurer.withObjectPostProcessor(new ObjectPostProcessor<SecurityContextPersistenceFilter>() {
//			@Override
//			public <O extends SecurityContextPersistenceFilter> O postProcess(O securityContextPersistenceFilter) {
//				securityContextPersistenceFilter.setForceEagerSessionCreation(false);//默认值为false
//				return null;
//			}
//		});
		
		///////////////////////////////HeaderWriterFilter（默认开启）
//		HeadersConfigurer<HttpSecurity> headersConfigurer = httpSecurity.headers();
		
		///////////////////////////////CorsFilter（默认关闭）
		if(openCors) {
			CorsConfigurer<HttpSecurity> corsConfigurer = httpSecurity.cors();
			corsConfigurer.configurationSource(this.corsConfigurationSource);
		}
		
		///////////////////////////////CsrfFilter（默认开启）
		if(openCsrf) {
			//默认是开启的
//			CsrfConfigurer<HttpSecurity> csrfConfigurer = httpSecurity.csrf();
//			csrfConfigurer.csrfTokenRepository(null);
//			csrfConfigurer.ignoringAntMatchers(null);
//			csrfConfigurer.ignoringRequestMatchers(null);
//			csrfConfigurer.sessionAuthenticationStrategy(null);
		}else {
			CsrfConfigurer<HttpSecurity> csrfConfigurer = httpSecurity.csrf();
			csrfConfigurer.disable();
		}
		
		///////////////////////////////LogoutFilter（默认开启）
		httpSecurity.removeConfigurer(LogoutConfigurer.class);
		ComplexLogoutFilterConfigurer<HttpSecurity> complexLogoutFilterConfigurer = httpSecurity.apply(new ComplexLogoutFilterConfigurer<>());
		complexLogoutFilterConfigurer.setLogoutRequestMatchers(logoutRequestMatchers);
		complexLogoutFilterConfigurer.setSpringGenerator(this.logoutSpringRespGenerator);
		this.logoutHandlers.forEach(logoutHandler -> {
			complexLogoutFilterConfigurer.addCumstomLogoutHandler(logoutHandler);
		});
//			.addExtendMatcherToSuccessHandler(null, null);
		
		///////////////////////////////UsernamePasswordAuthenticationFilter
		ComplexRequestMatcher<AuthRequestMatcher> complexAuthRequestMatcher = new ComplexRequestMatcher<>(this.authRequestMatchers);
		httpSecurity.apply(new ComplexAuthFilterConfigurer<>())
			.setComplexAuthRequestMatcher(complexAuthRequestMatcher) //必须
//			.setAuthenticationManager(null) //需要完全自定义的时候传入
			.setAuthenticationDetailsSource(this.authDynamicParameterAuthDetailsSource) //必须
//			.setSessionStrategy(null) //登录校验成功在调用successfulAuthentication之前如果需要处理Session相关则传入
//			.setContinueChainBeforeSuccessfulAuthentication(false)
//			.setRememberMeServices(null)
//			.setEventPublisher(authSuccessEventPublisher)
			.setSpringGenerator(this.authSpringRespGenerator); //defaultAuthSuccessHandler和defaultAuthFailHandler使用
//			.setDefaultAuthSuccessHandler(null);
//			.addExtendMatcherToSuccessHandler(null, null);
//			.setDefaultAuthFailHandler(null);
//			.addExtendMatcherToFailHandler(null, null);
		
		
		///////////////////////////////RequestCacheAwareFilter（默认开启）
		if(openRequestCache) {
			RequestCacheConfigurer<HttpSecurity> requestCacheConfigurer = httpSecurity.requestCache();
			requestCacheConfigurer.requestCache(this.requestCache);
		}else {
			httpSecurity.removeConfigurer(RequestCacheConfigurer.class);
		}
		
		///////////////////////////////LoginFilter（默认开启）
		//关闭DefaultLoginPageGeneratingFilter、DefaultLogoutPageGeneratingFilter
		httpSecurity.removeConfigurer(DefaultLoginPageConfigurer.class);
		
		///////////////////////////////AnonymousAuthenticationFilter（默认开启）
		
		
		
		if(openSession) {///////////////////////////////SessionManagementFilter
			httpSecurity.sessionManagement();
//				.invalidSessionStrategy(null) //TODO 无效会话ID
//				.sessionAuthenticationFailureHandler(null) //TODO Session验证失败
//				.maximumSessions(3)
			
		}else if(openJwt) {///////////////////////////////ConcurrentJwtFilter
			httpSecurity.removeConfigurer(SessionManagementConfigurer.class);
			httpSecurity.apply(new JwtFilterConfigurer<>())
				.setJwtSecurityContextRepository((IJwtSecurityContextRepository)this.securityContextRepository)
				.setJwtSpringRespGenerator(this.jwtSpringRespGenerator)
				.setJwtVerifyRedirectUrl(this.jwtVerifyRedirectUrl)
//				.addCumstomExpiredLogoutHandler(null)
				.setJwtExpiredRedirectUrl(this.jwtExpiredRedirectUrl)
				.setIssuer(this.issuer)
				.setDurationTimeSeconds(this.durationTimeSeconds)
				.setRefreshWindowSeconds(this.refreshWindowSeconds)
				.setHeaderParamterName(this.headerParamterName);
		}else {//null
			httpSecurity.removeConfigurer(SessionManagementConfigurer.class);
		}
		
	}


}
