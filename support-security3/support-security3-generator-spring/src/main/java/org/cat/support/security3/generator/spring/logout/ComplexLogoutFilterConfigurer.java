package org.cat.support.security3.generator.spring.logout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.support.security3.generator.spring.core.ComplexRequestMatcher;
import org.cat.support.security3.generator.spring.logout.handler.ContextLogoutHandler;
import org.cat.support.security3.generator.spring.logout.success.ComplexLogoutSuccessHandler;
import org.cat.support.security3.generator.spring.logout.success.DefaultLogoutSuccessHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.Assert;

import lombok.Setter;

public class ComplexLogoutFilterConfigurer<H extends HttpSecurityBuilder<H>> 
	extends AbstractHttpConfigurer<LogoutConfigurer<H>, H> {
	
	private ComplexLogoutFilter complexLogoutFilter; //构造方法-初始化
	
	private ComplexRequestMatcher<LogoutRequestMatcher> complexLogoutRequestMatcher; //用户设置
	
	private List<LogoutHandler> logoutHandlers = new ArrayList<>(); //init-初始化
	private List<LogoutHandler> customLogoutHandlers = new ArrayList<>(); 
	
//	private LogoutSuccessEventPublishingLogoutHandler logoutSuccessEventPublishingLogoutHandler = new LogoutSuccessEventPublishingLogoutHandler();
	@Setter private ApplicationEventPublisher logoutSuccessPublisher;
	
	private ComplexLogoutSuccessHandler complexLogoutSuccessHandler;
	private DefaultLogoutSuccessHandler defaultLogoutSuccessHandler;
	private LinkedHashMap<LogoutRequestMatcher, LogoutSuccessHandler> extendMatcherToSuccessHandler;
	private ISpringRespGenerator springRespGenerator;
	
	public ComplexLogoutFilterConfigurer() {
		this.complexLogoutFilter = new ComplexLogoutFilter();
	}
	@Override
	public void init(H builder) throws Exception {
		initLogoutHandlers(builder);
		initLogoutSuccessHandler();
	}
	
	protected void initLogoutSuccessHandler() {
		initDefaultLogoutSuccessHandler();
		initComplexLogoutSuccessHandler();
	}
	
	protected void initDefaultLogoutSuccessHandler() {
		if(this.defaultLogoutSuccessHandler==null) {
			this.defaultLogoutSuccessHandler = new DefaultLogoutSuccessHandler();
			Assert.notNull(this.springRespGenerator, "springRespGenerator不能为null");
			this.defaultLogoutSuccessHandler.setSpringRespGenerator(this.springRespGenerator);
		}
		if(this.defaultLogoutSuccessHandler.getSpringRespGenerator()==null) {
			this.defaultLogoutSuccessHandler.setSpringRespGenerator(this.springRespGenerator);
		}
	}
	
	protected void initComplexLogoutSuccessHandler() {
		if(this.complexLogoutSuccessHandler==null) {
			Assert.notNull(this.complexLogoutRequestMatcher, "complexLogoutRequestMatcher不能为null");
			this.complexLogoutSuccessHandler = new ComplexLogoutSuccessHandler(this.complexLogoutRequestMatcher);
		}
	}
	
	protected void initLogoutHandlers(H builder) {
		this.logoutHandlers.add(postProcess(new ContextLogoutHandler()));
	}
	
	@Override
	public void configure(H builder) throws Exception {
		//successHandler
		this.complexLogoutSuccessHandler.setDefaultLogoutSuccessHandler(this.defaultLogoutSuccessHandler);
		this.complexLogoutSuccessHandler.setExtendMatcherToSuccessHandler(this.extendMatcherToSuccessHandler);
		
		//Filter
		Assert.notNull(this.complexLogoutRequestMatcher, "complexLogoutRequestMatcher不能为null");
		this.complexLogoutFilter.setLogoutRequestMatcher(this.complexLogoutRequestMatcher);
		
		this.logoutHandlers.addAll(this.customLogoutHandlers);
//		this.logoutSuccessEventPublishingLogoutHandler.setApplicationEventPublisher(this.logoutSuccessPublisher);
//		this.logoutHandlers.add(new LogoutSuccessEventPublishingLogoutHandler());//放在logoutHandlers的最后一个
		Assert.notEmpty(this.logoutHandlers, "logoutHandlers不能为空");
		CompositeLogoutHandler compositeLogoutHandler = new CompositeLogoutHandler(this.logoutHandlers);
		//TODO 可以扩展修改为根据不同的RequestMatcher走不同logoutHandlers流程
		this.complexLogoutFilter.setLogoutHandler(compositeLogoutHandler);
		
		this.complexLogoutFilter.setLogoutSuccessHandler(this.complexLogoutSuccessHandler);
		
		builder.addFilterBefore(this.complexLogoutFilter, LogoutFilter.class);
	}
	
	public ComplexLogoutFilterConfigurer<H> setLogoutRequestMatchers(List<LogoutRequestMatcher> requestMatchers) {
		this.complexLogoutRequestMatcher = new ComplexRequestMatcher<>(requestMatchers);
		return this;
	}
	
	public ComplexLogoutFilterConfigurer<H> addCumstomLogoutHandler(LogoutHandler logoutHandler) {
		this.customLogoutHandlers.add(logoutHandler);
		return this;
	}
	
	public ComplexLogoutFilterConfigurer<H> addExtendMatcherToSuccessHandler(
		LogoutRequestMatcher logoutRequestMatcher, LogoutSuccessHandler logoutSuccessHandler) {
		this.extendMatcherToSuccessHandler.put(logoutRequestMatcher, logoutSuccessHandler);
		return this;
	}
	
	public ComplexLogoutFilterConfigurer<H> setSpringGenerator(ISpringRespGenerator springRespGenerator){
		this.springRespGenerator = springRespGenerator;
		return this;
	}
	
	
}
