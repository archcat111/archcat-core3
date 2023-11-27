package org.cat.support.security3.generator.spring.study;

import javax.servlet.DispatcherType;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@Configuration
class WebSecurityConfigurerAdapterDemo extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.formLogin()
			.loginPage(null) //指定登录页url。会根据传入的值调用：强制转换Https、登录验证url、登出成功url、登录验证失败url
			.usernameParameter(null)	//默认获取用户名的参数名，默认值为"username"
			.passwordParameter(null)	//默认获取密码的参数名，默认值为"password"
			.loginProcessingUrl(null) //指定登录验证的url，该请求只能是post请求
			.defaultSuccessUrl("/") //认证成功后的默认重定向页面
			.successForwardUrl(null)
			.successHandler(null)
			.failureUrl(null)
			.failureForwardUrl(null)
			.failureHandler(null)
			.authenticationDetailsSource(null)
			.withObjectPostProcessor(null);
		
		http.authorizeRequests()
			.antMatchers("url匹配路径").permitAll()
			.antMatchers("/admin/**").hasRole("ADMIN1")
			.antMatchers("/admin/**").hasAnyRole("ADMIN1","ADMIN2")
			.antMatchers("/admin/**").hasAuthority("ADMIN")
			.antMatchers("/admin/**").hasAnyAuthority("ADMIN1","ADMIN2")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.antMatchers("/admin/**").hasIpAddress(null)
			.antMatchers("/admin/**").anonymous()
			.antMatchers("/admin/**").denyAll()
			.antMatchers("/admin/**").rememberMe()
			.antMatchers("/admin/**").authenticated()
			.antMatchers("/admin/**").fullyAuthenticated()
			.antMatchers(HttpMethod.GET).access(null)
			.antMatchers(HttpMethod.DELETE, "/admin/**").access(null)
			.anyRequest().authenticated()
			.mvcMatchers("/admin/**").access(null)
			.mvcMatchers(HttpMethod.DELETE, "/admin/**").servletPath("/v1").access(null)
			.regexMatchers("/admin/**").access(null)
			.regexMatchers(HttpMethod.DELETE, "/admin/**").access(null)
			.dispatcherTypeMatchers(DispatcherType.FORWARD).access(null)
			.dispatcherTypeMatchers(HttpMethod.GET, DispatcherType.FORWARD).access(null)
			.requestMatchers(new AntPathRequestMatcher("/admin/**")).access(null)
			.expressionHandler(null)
			.accessDecisionManager(null)
			.filterSecurityInterceptorOncePerRequest(true)
			.withObjectPostProcessor(null);
		
		//异常处理
		http.exceptionHandling()
		.accessDeniedPage(null)
		.accessDeniedHandler(null)
		.defaultAccessDeniedHandlerFor(null, null)
		.authenticationEntryPoint(null)
		.defaultAuthenticationEntryPointFor(null, null);
			
		//匿名访问时，存在默认用户名annonymousUser
		http.anonymous()
			.authorities("aaa","bbb")
			.principal(null)
			.authenticationFilter(null)
			.authenticationProvider(null)
			.withObjectPostProcessor(null);
		
		//登出操作管理
		http.logout()
			.logoutUrl("/my/logout")
			.logoutSuccessUrl("/my/index")
			.logoutSuccessHandler(null)
			.defaultLogoutSuccessHandlerFor(null, null)
			.logoutRequestMatcher(null)
			.addLogoutHandler(null)
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.deleteCookies("cookieName1", "cookieName2");
			
		//session管理
		http.sessionManagement()
			.enableSessionUrlRewriting(false)
			.invalidSessionUrl(null)
			.invalidSessionStrategy(null)
			.sessionAuthenticationErrorUrl(null)
			.sessionAuthenticationFailureHandler(null)
			.sessionAuthenticationStrategy(null)
			.sessionFixation(sessionFixationConfigurer -> {})
			.sessionCreationPolicy(null)
			.sessionConcurrency(concurrencyControlConfigurer -> {
				concurrencyControlConfigurer
					.maximumSessions(1)
					.maxSessionsPreventsLogin(false)
					.expiredUrl("/")
					.expiredSessionStrategy(null) 
					.sessionRegistry(null);
			})
			.withObjectPostProcessor(null);
		
		http.httpBasic()
			.realmName(null)
			.authenticationDetailsSource(null)
			.authenticationEntryPoint(null);
		
		http.cors()
			.configurationSource(null)
			.disable();
		
		http.csrf()
			.csrfTokenRepository(null)
			.ignoringAntMatchers("/aaa")
			.ignoringRequestMatchers(new AntPathRequestMatcher("/aaa"))
			.requireCsrfProtectionMatcher(new AntPathRequestMatcher("/aaa"))
			.sessionAuthenticationStrategy(null)
			.disable();
		
	}
	
	protected void formLogin() {
		/**
		 * 
		 * FormLoginConfigurer：
		 * 创建FormLoginConfigurer对象赋值给类变量configurersAddedInInitializing
		 * FormLoginConfigurer<H extends HttpSecurityBuilder<H>>
		 * 		extends AbstractAuthenticationFilterConfigurer<H, FormLoginConfigurer<H>, UsernamePasswordAuthenticationFilter>
		 * 
		 * 该FormLoginConfigurer会初始化一个UsernamePasswordAuthenticationFilter
		 * 		如果没有初始化则默认使用DefaultLoginPageGeneratingFilter
		 * 
		 * 创建1个UsernamePasswordAuthenticationFilter对象赋值给类变量authFilter
		 * 初始化UsernamePasswordAuthenticationFilter的时候会默认初始化一个AntPathRequestMatcher用于匹配
		 * 		AntPathRequestMatcher("/login","POST");
		 * 给UsernamePasswordAuthenticationFilter设置usernameParameter为"username"
		 * 给UsernamePasswordAuthenticationFilter设置passwordParameter为"password"
		 * 其中的AuthenticationManager是在SharedObject中获取的
		 * 		this.authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		 * 
		 * 涉及到：
		 * 		LoginUrlAuthenticationEntryPoint：用于处理用户在未登录的状态下访问需要权限的资源
		 * 			portResolver
		 * 				portMapper
		 * 		UsernamePasswordAuthenticationFilter：用户进行基于用户名和密码进行登录认证的Filter
		 * 			requiresAuthenticationRequestMatcher
		 * 			authenticationDetailsSource
		 * 			AuthenticationManager
		 * 			AuthenticationSuccessHandler
		 * 			AuthenticationFailureHandler
		 * 			SessionAuthenticationStrategy
		 * 			RememberMeServices
		 * 		AuthenticationSuccessHandler：认证成功后的处理器，如跳转到登录成功页面
		 * 			实现1：SavedRequestAwareAuthenticationSuccessHandler
		 * 				SavedRequest
		 * 			实现2：ForwardAuthenticationSuccessHandler
		 * 		AuthenticationFailureHandler：认证失败后的处理器，如跳转到登录页面
		 * 			实现1：SimpleUrlAuthenticationFailureHandler
		 * 			实现2：ForwardAuthenticationFailureHandler
		 * 		DefaultLoginPageGeneratingFilter
		 * 
		 */
	}
	
	protected void fromLogin__loginPage(){
		/**
		 * 登录页面，默认值为"/login"(在UsernamePasswordAuthenticationFilter)
		 * 
		 * 当设置该方法时，其实就是在配置formLogin()方法创建的FormLoginConfigurer
		 * 该方法会直接调用：super.loginPage(loginPage);
		 * 		在AbstractAuthenticationFilterConfigurer中，会new LoginUrlAuthenticationEntryPoint(loginPage)
		 * 		重！如果loginProcessingUrl(String loginProcessingUrl)没有被自定义，则会loginProcessingUrl(this.loginPage)
		 * 		重！如果failureUrl(String authenticationFailureUrl)没有被自定义，则会failureUrl(this.loginPage + "?error")
		 * 		重！如果logoutSuccessUrl或者logoutSuccessHandler没有被自定义，则会logoutSuccessUrl(this.loginPage + "?logout")
		 * 		设置FormLoginConfigurer.customLoginPage = true
		 * 
		 * LoginUrlAuthenticationEntryPoint：
		 * 		重！会设置给ExceptionHandlingConfigurer用于处理用户在未登录的状态下访问需要权限的接口
		 * 		这里的loginPage就是我们调用该方法传入的参数
		 * 		类参数loginFormUrl，表示登录页url。通过构造方法传入
		 * 		类参数forceHttps = false(默认)，表示是否强制跳转到Https。可通过set方法修改
		 * 		类参数useForward = false(默认)，表示是否使用请求转发。可通过set方法修改
		 * 		类参数redirectStrategy = new DefaultRedirectStrategy()(默认)，表示使用的请求转发器
		 * 		类参数portMapper = new PortMapperImpl()(默认)，表示端口映射器。可通过set方法修改
		 * 		类参数portResolver = new PortResolverImpl()(默认)，表示端口解析器。可通过set方法修改
		 * 			重！portResolver调用portMapper，PortMapperImpl默认只支持80到443、8080到8443，如果需要其他需要自定义
		 * 		执行步骤：
		 * 			如果useForward == false
		 * 				如果loginFormUrl是绝对路径地址（这里的绝对地址判断正则为[a-z0-9.+-]+://.*）
		 * 					this.redirectStrategy.sendRedirect(request, response, redirectUrl);
		 * 				如果loginFromUrl是相对路径地址
		 * 					先使用portMapper、portResolver、forceHttps、loginFormUrl各种转换和组装出来一个完整的url
		 * 					然后this.redirectStrategy.sendRedirect(request, response, redirectUrl);
		 * 			如果forceHttps==true && 当前Schema为http
		 * 				替换将当前登录请求中的shema替换为https，对应的端口根据PortMapperImpl替换为443or8443，其他不变
		 * 				重定向
		 * 			请求转发当前配置的loginFromUrl
		 * 
		 */
	}
	
	protected void fromLogin__usernameParameter(){
		/**
		 * 修改UsernamePasswordAuthenticationFilter中的usernameParameter，其默认值为"username"
		 */
	}
	
	protected void fromLogin__passwordParameter(){
		/**
		 * 修改UsernamePasswordAuthenticationFilter中的passwordParameter，其默认值为"password"
		 */
	}
	
	protected void fromLogin__loginProcessingUrl(){
		/**
		 * 修改认证请求的url
		 * 修改UsernamePasswordAuthenticationFilter中的requiresAuthenticationRequestMatcher
		 * 		默认值：AntPathRequestMatcher("/login", "POST");
		 * 修改该值会创建AntPathRequestMatcher(loginProcessingUrl, "POST")进行覆盖
		 */
	}
	
	protected void fromLogin__defaultSuccessUrl(){
		/**
		 * 认证成功后的重定向页面
		 * 默认值：SavedRequestAwareAuthenticationSuccessHandler
		 * 		extends SimpleUrlAuthenticationSuccessHandler
		 * 			extends AbstractAuthenticationTargetUrlRequestHandler
		 * 			implements AuthenticationSuccessHandler
		 * 		targetUrlParameter = null (默认值)
		 * 		defaultTargetUrl = "/" (默认值)
		 * 		alwaysUseDefaultTargetUrl = false (默认值)
		 * 		useReferer = false (默认值)
		 * 		redirectStrategy = new DefaultRedirectStrategy() (默认值)
		 * 		在FormLoginConfigurer.configure()中，set到UsernamePasswordAuthenticationFilter的AuthenticationSuccessHandler类参数
		 * 
		 * 如果SavedRequest==null：第一次登录
		 * 		alwaysUse==false：
		 * 			targetUrlParameter==null或者targetUrlParameter有值但是其value没值：
		 * 				header中的Referer也没值，则重定向到defaultSuccessUrl，即："/"
		 * 如果SavedRequest!=null：发生权限异常后再登录
		 * 		alwaysUse==false && targetUrlParameter对应的value==null：
		 * 			重定向到SavedRequest中的url
		 * 
		 * 这里涉及到：
		 * 		1：该Handler中的一个参数targetUrlParameter(默认值为null)，该值可以自定义
		 * 		2：SavedRequest，只有当用户登录失败 || 用户不管是否登录但是访问无权限的资源时会将用户访问的地址存储在其中，处理类是ExceptionTranslationFilter
		 * 
		 * 该方法会初始化SavedRequestAwareAuthenticationSuccessHandler
		 * 		SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
		 * 		handler.setDefaultTargetUrl(defaultSuccessUrl);
		 * 		handler.setAlwaysUseDefaultTargetUrl(alwaysUse);
		 * 		重！return successHandler(handler);
		 * 还有另一个方法defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse)，alwaysUse默认值为false
		 * 最后：configure(B http) ---> this.authFilter.setAuthenticationSuccessHandler(this.successHandler);
		 * 
		 * 如果SavedRequest==null：第一次登录
		 *		如果alwaysUse==true；重定向到defaultSuccessUrl，结束
		 *		如果alwaysUse==false：
		 *			尝试获取<targetUrlParameter>的值，如果获取成功并且有值，则重定向到该值，结束
		 * 				如果targetUrlParameter==null或者targetUrlParameter有值但是其value没值：
		 * 					尝试targetUrl = request.getHeader("Referer")
		 * 						如果header中的Referer也没值，则重定向到defaultSuccessUrl，结束
		 * 如果SavedRequest!=null：发生权限异常后再登录
		 * 		如果alwaysUse==true && 不管targetUrlParameter是否等于null，或者其值是否等于null：
		 * 			重定向到defaultSuccessUrl，结束
		 * 		如果alwaysUse==false && targetUrlParameter对应的value有值：
		 * 			重定向到targetUrlParameter对应的value，结束
		 * 		如果alwaysUse==false && targetUrlParameter对应的value==null：
		 * 			重定向到SavedRequest中的url，结束
		 */
	}
	
	protected void fromLogin__successForwardUrl(){
		/**
		 * 认证成功后的默认请求转发页面
		 * 该方法会初始化ForwardAuthenticationSuccessHandler
		 * 重！successHandler(new ForwardAuthenticationSuccessHandler(forwardUrl));
		 * 		该方法和defaultSuccessUrl()都是创建一个SuccessHandler设置到FormLoginConfigurer的类变量successHandler
		 * 		所以是冲突的
		 * 实现：request.getRequestDispatcher(this.forwardUrl).forward(request, response);
		 */
	}
	
	protected void fromLogin__successHandler(){
		/**
		 * 
		 * 参考：fromLogin__defaultSuccessUrl()、fromLogin__successForwardUrl()
		 */
	}
	
	protected void fromLogin__failureUrl(){
		/**
		 * 认证失败后的默认跳转页面
		 * 该方法会初始化SimpleUrlAuthenticationFailureHandler
		 * 		failureHandler(new SimpleUrlAuthenticationFailureHandler(authenticationFailureUrl));
		 * 			类变量String failureUrl = authenticationFailureUrl;
		 * 			类变量failureHandler = authenticationFailureHandler;
		 * 
		 * 重！该方法和failureForwardUrl()、failureHandler()只会有其一起作用
		 * 
		 * 如果defaultFailureUrl==null：
		 * 		response.sendError(401, "Unauthorized");，即重定向到error-page指定的页面
		 * 如果defaultFailureUrl!=null && forwardToDestination==true：（forwardToDestination默认为false）
		 * 		request.getRequestDispatcher(this.defaultFailureUrl).forward(request, response)
		 * 如果defaultFailureUrl!=null && forwardToDestination==false：
		 * 		this.redirectStrategy.sendRedirect(request, response, this.defaultFailureUrl);
		 */
	}
	
	protected void fromLogin__failureForwardUrl(){
		/**
		 * 认证失败后的默认请求转发页面
		 * 该方法会初始化ForwardAuthenticationFailureHandler
		 * 		failureHandler(new ForwardAuthenticationFailureHandler(forwardUrl));
		 * 			类变量String failureUrl = null;
		 * 			类变量failureHandler = authenticationFailureHandler;
		 * 
		 * 重！该方法和failureUrl()、failureHandler()只会有其一起作用
		 * 
		 * request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
		 * request.getRequestDispatcher(this.forwardUrl).forward(request, response);
		 */
	}
	
	protected void fromLogin__failureHandler(){
		/**
		 * 认证失败后的处理器
		 * 		类变量String failureUrl = null;
		 * 		类变量failureHandler = authenticationFailureHandler;
		 * 
		 * 重！该方法和failureUrl()、failureForwardUrl()只会有其一起作用
		 * 		
		 */
	}
	
	
	protected void fromLogin__authenticationDetailsSource(){
		/**
		 * 设置一个AuthenticationDetailsSource
		 * 当AuthenticationDetailsSource不为null的时候，将该实例设置到UsernamePasswordAuthenticationFilter
		 * UsernamePasswordAuthenticationFilter是专门用来调用验证器进行用户认证的
		 * 在认证之前，调用AuthenticationDetailsSource的buildDetails(request)方法创建一个对象填充到UsernamePasswordAuthenticationToken的details类变量
		 * 一般用于传输form表单提交的除了用户名和密码以外的其他request的属性，用于之后的认证
		 * 
		 * 如果不设置，UsernamePasswordAuthenticationFilter则会使用默认实现WebAuthenticationDetailsSource
		 * WebAuthenticationDetailsSource会创建一个WebAuthenticationDetails对象填充到UsernamePasswordAuthenticationToken的details类变量
		 * WebAuthenticationDetails从request中获取了remoteAddress和sessionId
		 * 
		 * 也可以自定义传输自己想要的更多属性用于认证以及后期使用
		 */
	}
	
	protected void fromLogin__withObjectPostProcessor(){
		/**
		 * 
		 * 因为SpringSecurity的很多组件都有可以配置的属性，但是公开出来让用户可以直接配置的属性只有一部分
		 * 该方法则可以对这些组件的没有公开的可配置的属性就行增强设置
		 * 这里是把ObjectPostProcessor添加到一个List<ObjectPostProcessor<?>>里
		 * 当其他地方调用Object postProcess(Object object)方法即可对传入的object进行增强，增强逻辑如下：
		 * 		循环遍历List<ObjectPostProcessor<?>>中的所有ObjectPostProcessor<?>
		 * 		如果这里某个ObjectPostProcessor没有设置泛型，则对调用该ObjectPostProcessor的postProcess方法对该传入的组件进行增强
		 * 		如果这里某个ObjectPostProcessor的泛型是调用该ObjectPostProcessor的postProcess方法时传入组件的父类则对该传入的组件进行增强
		 * 系统中调用Object postProcess(Object object)的地方的举例：
		 * 		WebSecurityConfiguration ---> springSecurityFilterChain()
		 * 			@Autowired(required = false) private ObjectPostProcessor<Object> objectObjectPostProcessor;
		 * 			WebSecurityConfigurerAdapter adapter = this.objectObjectPostProcessor.postProcess(new WebSecurityConfigurerAdapter() {});
		 * 		GlobalMethodSecurityConfiguration ---> setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor)
		 * 			private DefaultMethodSecurityExpressionHandler defaultMethodExpressionHandler = new DefaultMethodSecurityExpressionHandler();
		 * 			this.defaultMethodExpressionHandler = objectPostProcessor.postProcess(this.defaultMethodExpressionHandler);
		 * 		WebSecurityConfigurerAdapter ---> getAuthenticationEventPublisher()
		 * 			return this.objectPostProcessor.postProcess(new DefaultAuthenticationEventPublisher());
		 * 		......
		 * 		
		 * 举例：
		 * http.formLogin()
		 *		.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
		 *			@Override
		 *			public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
		 *				fsi.setPublishAuthorizationSuccess(true);
		 *				//修改成自定义的	安全元数据源		权限的源
		 *				fsi.setSecurityMetadataSource(myInvocationSecurityMetadataSourceService);
		 *				//修改成自定义的     访问决策器  自定义的
		 *				fsi.setAccessDecisionManager(myAccessDecisionManager);
		 *				//使用系统的
		 *				fsi.setAuthenticationManager(authenticationManager);
		 *				return fsi;
		 *			}
		 *		});
		 * 
		 * 可加强的对象：
		 * AuthenticationEntryPoint：
		 * 		FormLoginConfigurer中给ExceptionHandlingConfigurer设置AuthenticationEntryPoint
		 * 	
		 * AbstractAuthenticationProcessingFilter：（类变量叫AbstractAuthenticationProcessingFilter）
		 * 		如：UsernamePasswordAuthenticationFilter
		 */
	}
	
	protected void fromLogin__问题(){
		/**
		 * SavedRequestAwareAuthenticationSuccessHandler：
		 * 		RequestCache requestCache如何修改
		 * 		String targetUrlParameter如何修改
		 * 		boolean useReferer如何修改
		 * 		RedirectStrategy redirectStrategy如何修改
		 * 		如何重写这个Handler
		 * 		
		 */
	}
	
	protected void authorizeRequests() {
		/**
		 * 
		 * ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry：
		 * 
		 * ExpressionUrlAuthorizationConfigurer：
		 * 		ExpressionUrlAuthorizationConfigurer<H extends HttpSecurityBuilder<H>>：
		 * 		extends AbstractInterceptUrlConfigurer<ExpressionUrlAuthorizationConfigurer<H>, H>
		 * 				extends AbstractHttpConfigurer<C, H>
		 * 					extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B>
		 * 						implements SecurityConfigurer<O, B>
		 * 
		 * ExpressionInterceptUrlRegistry：ExpressionUrlAuthorizationConfigurer的内部类
		 * 		extends ExpressionUrlAuthorizationConfigurer<H>.AbstractInterceptUrlRegistry<ExpressionInterceptUrlRegistry, AuthorizedUrl>
		 * 			extends AbstractConfigAttributeRequestMatcherRegistry<T>
		 * 				extends AbstractRequestMatcherRegistry<C>
		 */
	}
	
	protected void authorizeRequests__antMatchers() {
		/**
		 * 该方法在AbstractRequestMatcherRegistry中
		 * 
		 * 该方法传入了一个url的String数据，方法内部定义了一个每个url对应生成一个RequestMatcher
		 * 重！该RequestMatcher的实现类为AntPathRequestMatcher
		 * 		将会匹配对应的url，而HttpMethod为null表示不对HttpMethod进行限制
		 * 最终赋值给类变量List<RequestMatcher> unmappedMatchers
		 * 该方法返回AuthorizedUrl对象
		 * 
		 * AntPathRequestMatcher：
		 * 		将预先定义的ant风格与URL进行比较(一个HttpServletRequest的servletPath + pathInfo)
		 * 		查询字符串网址被忽略
		 * 		匹配是否区分大小写具体取决于传递给构造函数的参数(默认区分)
		 * 		使用/** 或 ** 的模式值被视为通用匹配，这将匹配任何请求
		 * 		以/** 结尾的模式（并且没有其他通配符）比如 /aaa/** 的模式将匹配/aaa，/aaa/和任何子目录
		 * 
		 * AuthorizedUrl：ExpressionUrlAuthorizationConfigurer的内部类
		 * 		该类有类变量List<? extends RequestMatcher> requestMatchers，即上面定义的那些RequestMatcher
		 * 		重！RequestMatcher支持URL、HttpMethod以及URL和HttpMethod的组合
		 * 
		 * 当调用permitAll()、hasRole(...)等方法时：
		 * 		最终都会调用到AuthorizedUrl的access(String attribute)方法
		 * 		该方法会将每一个RequestMatcher以及对应的权限表达式set到AbstractRequestMatcherRegistry的类变量中
		 * 			即：ExpressionInterceptUrlRegistry REGISTRY
		 */
		
		/**
		 * ExpressionInterceptUrlRegistry.antMatchers("url匹配路径").permitAll()：
		 * 		所在类AuthorizedUrl，首先该类已经持有这里定义的url字符串数组对应数量的RequestMatcher
		 * 		当调用permitAll()方法时：
		 * 			创建一个List<ConfigAttribute> attributes，这个attributes只有一个SecurityConfig
		 * 				SecurityConfig：implements ConfigAttribute，并且这里的SecurityConfig中的类变量attrib = "permitAll"
		 * 		ExpressionUrlAuthorizationConfigurer.interceptUrl(this.requestMatchers, SecurityConfig.createList(attribute))：
		 * 			for (RequestMatcher requestMatcher : requestMatchers) {
		 * 				重！this.REGISTRY.addMapping(new AbstractConfigAttributeRequestMatcherRegistry.UrlMapping(requestMatcher, configAttributes));
		 * 			}
		 * 			这里的this.REGISTRY为类变量ExpressionInterceptUrlRegistry REGISTRY
		 * 			最后返回ExpressionInterceptUrlRegistry REGISTRY
		 * ExpressionInterceptUrlRegistry.antMatchers("url匹配路径").denyAll()：
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "denyAll"
		 * ExpressionInterceptUrlRegistry.antMatchers("url匹配路径").anonymous()：
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "anonymous"
		 * ExpressionInterceptUrlRegistry.antMatchers("url匹配路径").rememberMe()：
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "rememberMe"
		 * ExpressionInterceptUrlRegistry.antMatchers("url匹配路径").authenticated()：
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "authenticated"
		 * ExpressionInterceptUrlRegistry.antMatchers("url匹配路径").fullyAuthenticated()：
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "fullyAuthenticated"
		 * 
		 * ExpressionInterceptUrlRegistry.antMatchers("/admin/**").hasRole("ADMIN")：
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "hasRole('ROLE_ADMIN')"
		 * ExpressionInterceptUrlRegistry.antMatchers("/admin/**").hasAnyRole("ADMIN1","ADMIN2")：
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "hasAnyRole('ROLE_ADMIN1','ROLE_ADMIN2')"
		 * ExpressionInterceptUrlRegistry.antMatchers("/admin/**").hasAuthority("ADMIN")：
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "hasAuthority('ADMIN')"
		 * ExpressionInterceptUrlRegistry.antMatchers("/admin/**").hasAnyAuthority("ADMIN1","ADMIN2")：
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "hasAnyAuthority('ADMIN1','ADMIN2')"
		 * ExpressionInterceptUrlRegistry.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "hasRole('ROLE_ADMIN')"
		 * ExpressionInterceptUrlRegistry.antMatchers("/admin/**").hasIpAddress("192.168.0.1")
		 * 		这里的实现中，会将SecurityConfig中的类变量attrib = "hasIpAddress('192.168.0.1')"
		 */
	}
	
	protected void authorizeRequests__anyRequest() {
		/**
		 * 创建一个AnyRequestMatcher（RequestMatcher的实现类之一）
		 * 之后的流程和上述authorizeRequests__antMatchers()中讲的是一致的
		 * 
		 * AnyRequestMatcher：
		 * 		@Override
		 * 		public boolean matches(HttpServletRequest request) {
		 * 			return true;
		 * 		}
		 */
	}
	
	protected void authorizeRequests__mvcMatchers() {
		/**
		 * 返回一个MvcMatchersAuthorizedUrl对象
		 * 
		 * MvcMatchersAuthorizedUrl：
		 * 		extends AuthorizedUrl
		 * 	多了一个public AuthorizedUrl servletPath(String servletPath)方法
		 * 重！这里的RequestMatcher的实现类为MvcRequestMatcher
		 * 
		 * MvcRequestMatcher：
		 * 		使用Spring MVC的HandlerMappingIntrospector来匹配路径并提取变量
		 * 		如果您已经将任何servlet映射到以“/String”开头，则还应该指定#setServletPath（String）属性以区分相关映射匹配
		 * 
		 * 其他实现流程和antMatchers()没有区别
		 */
	}
	
	protected void authorizeRequests__regexMatchers() {
		/**
		 * 返回一个AuthorizedUrl对象
		 * 
		 * 重！这里的RequestMatcher的实现类为RegexRequestMatcher
		 * 
		 * 其他实现流程和antMatchers()没有区别
		 */
	}
	
	protected void authorizeRequests__dispatcherTypeMatchers() {
		/**
		 * 返回一个AuthorizedUrl对象
		 * 
		 * 重！这里的RequestMatcher的实现类为DispatcherTypeRequestMatcher
		 * 
		 * 可以设置：HttpMethod 和 DispatcherType
		 * 
		 * DispatcherType：
		 * 		REQUEST：默认值。浏览器直接请求资源
		 * 		FORWARD：指通过RequestDispatcher的forward()而来的请求
		 * 		INCLUDE：指通过RequestDispatcher的include()而来的请求，如Iframe
		 * 		ERROR：指由容器处理错误而转发过来的请求
		 * 		ASYNC：指异步处理的请求
		 * 
		 * 其他实现流程和antMatchers()没有区别
		 */
	}
	
	protected void authorizeRequests__requestMatchers() {
		/**
		 * 可以直接自定义的RequestMatcher
		 * 具体原理可以参考antMatchers、anyRequest等
		 */
	}
	
	protected void authorizeRequests__expressionHandler() {
		/**
		 * 默认值：DefaultWebSecurityExpressionHandler
		 * 还看到一个实现DefaultMethodSecurityExpressionHandler
		 * 
		 * DefaultWebSecurityExpressionHandler：
		 * 		DefaultWebSecurityExpressionHandler是Spring Security Web用于Web安全表达式处理器(handler)
		 * 		它会基于一组缺省配置，和当时的环境，对指定的Web安全表达式求值
		 * 		DefaultWebSecurityExpressionHandler对给定的认证token和请求上下文FilterInvocation创建一个评估上下文EvaluationContext
		 * 		然后供SPEL求值使用。比如在WebExpressionVoter中被应用
		 */
	}
	
	protected void authorizeRequests__accessDecisionManager(){
		/**
		 * 用于判断用户是否有权限访问某个资源的处理器，可以自定义
		 * 默认使用AffirmativeBased
		 * 		AbstractInterceptUrlConfigurer的getAccessDecisionManager(H http)中，如果accessDecisionManager==null则创建一个AffirmativeBased
		 * AffirmativeBased通过AccessDecisionVoter判断权限
		 * AccessDecisionVoter通过Authentication中的GrantedAuthority和传入的Collection<ConfigAttribute>来判断用户是否有权限
		 * 		Authentication：当前登录用户的信息
		 * 		Object：是一个Filterlnvocation对象，可以获取当前请求对象等
		 * 		Collection<ConfigAttribute>：当前请求URL所需要的角色
		 * 			FilterlnvocationSecurityMetadataSource 中的 getAttributes 方法的返回值
		 * 
		 * AccessDecisionManager --->
		 * 		AffirmativeBased(默认实现)
		 * 			持有List<AccessDecisionVoter<?>>，构造函数传入的
		 * 				WebExpressionVoter(AccessDecisionVoter的一种实现)
		 * 					SecurityExpressionHandler<FilterInvocation> expressionHandler（就是上面的expressionHandler()方法set进来的）
		 */
	}
	
	protected void authorizeRequests__filterSecurityInterceptorOncePerRequest(){
		/**
		 * 默认值为true(在FilterSecurityInterceptor中的observeOncePerRequest=true)
		 * 当该值为true时，过滤器FilterSecurityInterceptor第一次对请求进行处理后会在request里set一个属性表示该请求已经被处理过
		 * 		当包含该属性的请求再次进行该过滤器时就不会被处理而是直接传给下一个过滤器
		 * 			private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied";
		 * 			filterInvocation.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
		 * 当该值为false时，则请求每次都会被处理
		 */
	}
	
	protected void authorizeRequests__withObjectPostProcessor(){
		/**
		 * SecurityExpressionHandler<FilterInvocation>
		 */
	}
	
	protected void authorizeRequests__问题() {
		/**
		 * 通过Spring的@Bean初始化的UserDetailsService是怎么注入进去的
		 */
	}
	
	protected void anonymous() {
		/**
		 * 会new一个AnonymousConfigurer，并返回AnonymousConfigurer<HttpSecurity>
		 */
	}
	
	protected void anonymous__authorities() {
		/**
		 * 代表用户的权限
		 * 默认值：
		 * 		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
		 * 		然后传递给AnonymousAuthenticationFilter
		 * 		最终生成一个AnonymousAuthenticationToken，传递给该AuthenticationToken，表示该匿名用户的角色权限
		 */
	}
	
	protected void anonymous__principal() {
		/**
		 * 代表用户自身
		 * 默认值：
		 * 		Object principal = "anonymousUser";
		 * 		然后传递给AnonymousAuthenticationFilter
		 * 		最终生成一个AnonymousAuthenticationToken，传递给该AuthenticationToken，表示该匿名用户自身
		 */
	}
	
	
	
	protected void anonymous__authenticationFilter() {
		/**
		 * 自定义一个AnonymousAuthenticationFilter，Set到FilterList
		 * 如果自定义了该AnonymousAuthenticationFilter，则系统不会再自行设置AnonymousAuthenticationFilter，将完全使用自己的
		 * 
		 * 默认值：
		 * this.authenticationFilter = new AnonymousAuthenticationFilter(getKey(), this.principal, this.authorities);
		 * 		authenticationDetailsSource：默认的自己new的WebAuthenticationDetailsSource，重！如果这个参数需要自定义，那就需要自定义AnonymousAuthenticationFilter
		 * 		key：为UUID.randomUUID().toString();
		 * 		principal：字符串"anonymousUser"
		 * 		authorities：一个长度为1的List<GrantedAuthority>，其中的这一个对象为SimpleGrantedAuthority
		 * 			该SimpleGrantedAuthority对象只有一个类参数String role，值为"ROLE_ANONYMOUS"
		 * 
		 * SimpleGrantedAuthority：
		 * 		SimpleGrantedAuthority implements GrantedAuthority
		 * 		只有一个类参数String role
		 * 		唯一@override的getAuthority()方法即返回该role字符串
		 * 
		 * AnonymousAuthenticationFilter：
		 * 		如果SecurityContextHolder.getContext().getAuthentication() == null，则会自行创建一个AnonymousAuthenticationToken
		 * 			AnonymousAuthenticationToken对象的参数值如下：
		 * 				key：上面AnonymousAuthenticationFilter中的key执行key.hashCode()获取的值
		 * 				principal：上面AnonymousAuthenticationFilter中的principal，即：字符串"anonymousUser"
		 * 				authorities：上面AnonymousAuthenticationFilter中的List<GrantedAuthority>，其中role为"ROLE_ANONYMOUS"
		 * 		然后：SecurityContextHolder.getContext().setAuthentication(上面创建的AnonymousAuthenticationToken)
		 */
	}
	
	protected void anonymous__authenticationProvider() {
		/**
		 * 自定义一个AuthenticationProvider，add到AuthenticationManagerBuilder的类变量List<AuthenticationProvider> authenticationProviders里
		 * 如果自定义了该AuthenticationProvider，则系统不会再自行设置AuthenticationProvider，将完全使用自己的
		 * 
		 * 默认值：
		 * 		AnonymousAuthenticationProvider(getKey())
		 * 			key：为UUID.randomUUID().toString();
		 * 
		 * AnonymousAuthenticationProvider：
		 * 		首先看传入的Authentication是否是AnonymousAuthenticationToken或者其子类，否则return null
		 * 		然后判断key和AnonymousAuthenticationToken中的key的hashCode是否相同
		 * 			相同：return 该authentication
		 * 			不相同：throw new BadCredentialsException
		 */
	}
	
	protected void anonymous__withObjectPostProcessor(){
		/**
		 * AuthenticationProvider
		 */
	}
	
	protected void exceptionHandling() {
		/**
		 * 创建一个ExceptionHandlingConfigurer<HttpSecurity>
		 * 最终创建一个ExceptionTranslationFilter
		 * 
		 * ExceptionTranslationFilter：
		 * 		捕获所有Spring Security抛出的异常，并决定处理方式
		 * 		order：3200，在FilterSecurityInterceptor之前执行
		 * 
		 * 抛出AuthenticationException：
		 * 		清除Authentication：SecurityContextHolder.getContext().setAuthentication(null);
		 * 		记录用户要访问的url：this.requestCache.saveRequest(request, response);
		 * 		this.authenticationEntryPoint.commence(request, response, reason);
		 * 抛出AccessDeniedException && (AnonymousAuthenticationToken || RememberMeAuthenticationToken)：
		 * 		清除Authentication：SecurityContextHolder.getContext().setAuthentication(null);
		 * 		记录用户要访问的url：this.requestCache.saveRequest(request, response);
		 * 		this.authenticationEntryPoint.commence(request, response, reason);
		 * 抛出AccessDeniedException && !(AnonymousAuthenticationToken || RememberMeAuthenticationToken)：
		 * 		this.accessDeniedHandler.handle(request, response, exception);
		 */
		
	}
	
	protected void exceptionHandling__accessDeniedPage() {
		/**
		 * 非匿名用户(已登录用户)访问资源出现权限问题时触发
		 * 
		 * 抛出AuthenticationException：
		 * 		清除Authentication：SecurityContextHolder.getContext().setAuthentication(null);
		 * 		记录用户要访问的url：this.requestCache.saveRequest(request, response);
		 * 		this.authenticationEntryPoint.commence(request, response, reason);
		 * 抛出AccessDeniedException && (AnonymousAuthenticationToken || RememberMeAuthenticationToken)：
		 * 		清除Authentication：SecurityContextHolder.getContext().setAuthentication(null);
		 * 		记录用户要访问的url：this.requestCache.saveRequest(request, response);
		 * 		this.authenticationEntryPoint.commence(request, response, reason);
		 * 抛出AccessDeniedException && !(AnonymousAuthenticationToken || RememberMeAuthenticationToken)：
		 * 		this.accessDeniedHandler.handle(request, response, exception);
		 * 
		 * 三者之间的关系：accessDeniedPage("/errorPage")、accessDeniedHandler(xxx)、defaultAccessDeniedHandlerFor(xxx)
		 * 不配置accessDeniedPage("/errorPage") && 不配置accessDeniedHandler(xxx) && 不配置defaultAccessDeniedHandlerFor(xxx)：
		 * 		相当于：使用errorPage为null的AccessDeniedHandlerImpl(默认)
		 * 			AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
		 * 			exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandler);
		 * 配置accessDeniedPage("/errorPage") && 不配置accessDeniedHandler(xxx) && 不配置defaultAccessDeniedHandlerFor(xxx)：
		 * 		相当于：使用设置了errorPage的AccessDeniedHandlerImpl
		 * 			AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
		 * 			accessDeniedHandler.setErrorPage("/errorPage");
		 * 			exceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandler);
		 * 不配置accessDeniedPage("/errorPage") && 配置accessDeniedHandler(xxx) && 无论是否配置defaultAccessDeniedHandlerFor(xxx)：
		 * 		相当于：使用用户自定义的AccessDeniedHandler
		 * 			exceptionTranslationFilter.setAccessDeniedHandler(用户自定义的accessDeniedHandler);
		 * 配置accessDeniedPage("/errorPage") && 配置accessDeniedHandler(xxx) && 无论是否配置defaultAccessDeniedHandlerFor(xxx)：
		 * 		相当于：accessDeniedPage("/errorPage")和accessDeniedHandler(xxx)，谁在后面谁生效
		 * 			exceptionTranslationFilter.setAccessDeniedHandler(用户自定义的accessDeniedHandler或者设置了errorPage的AccessDeniedHandlerImpl);
		 * 不配置accessDeniedPage("/errorPage") && 不配置accessDeniedHandler(xxx) && 配置了1个defaultAccessDeniedHandlerFor(xxx)：
		 * 		相当于：使用用户在这里自定义的这个AccessDeniedHandler
		 * 			exceptionTranslationFilter.setAccessDeniedHandler(用户在这里自定义的这个AccessDeniedHandler);
		 * 不配置accessDeniedPage("/errorPage") && 不配置accessDeniedHandler(xxx) && 配置了多个defaultAccessDeniedHandlerFor(xxx)：
		 * 		相当于：首先会根据RequestMatcher匹配选择不同AccessDeniedHandler，如果都不匹配则使用errorPage为null的AccessDeniedHandlerImpl
		 * 			new RequestMatcherDelegatingAccessDeniedHandler(this.defaultDeniedHandlerMappings,new AccessDeniedHandlerImpl());
		 * 			exceptionTranslationFilter.setAccessDeniedHandler(RequestMatcherDelegatingAccessDeniedHandler);
		 * 
		 * AccessDeniedHandlerImpl：
		 * 		默认errorPage==null
		 * 		如果不配置errorPage：
		 * 			response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
		 * 		否则：
		 * 			request.getRequestDispatcher(this.errorPage).forward(request, response);
		 * 
		 */
	}
	
	protected void exceptionHandling__accessDeniedHandler() {
		/**
		 * 非匿名用户(已登录用户)访问资源出现权限问题时触发
		 * 参考：exceptionHandling__accessDeniedPage()
		 */
	}
	
	protected void exceptionHandling__defaultAccessDeniedHandlerFor() {
		/**
		 * 非匿名用户(已登录用户)访问资源出现权限问题时触发，可根据不同的RequestMatcher使用不同的AccessDeniedHandler
		 * 参考：exceptionHandling__accessDeniedPage()
		 */
	}
	
	protected void exceptionHandling__authenticationEntryPoint() {
		/**
		 * 匿名用户(非登录用户)访问资源出现权限问题时触发
		 * 
		 * 抛出AuthenticationException：
		 * 		清除Authentication：SecurityContextHolder.getContext().setAuthentication(null);
		 * 		记录用户要访问的url：this.requestCache.saveRequest(request, response);
		 * 		this.authenticationEntryPoint.commence(request, response, reason);
		 * 抛出AccessDeniedException && (AnonymousAuthenticationToken || RememberMeAuthenticationToken)：
		 * 		清除Authentication：SecurityContextHolder.getContext().setAuthentication(null);
		 * 		记录用户要访问的url：this.requestCache.saveRequest(request, response);
		 * 		this.authenticationEntryPoint.commence(request, response, reason);
		 * 抛出AccessDeniedException && !(AnonymousAuthenticationToken || RememberMeAuthenticationToken)：
		 * 		this.accessDeniedHandler.handle(request, response, exception);
		 * 
		 * 两者之间的关系：authenticationEntryPoint、defaultAuthenticationEntryPointFor：
		 * 不配置authenticationEntryPoint && 不配置defaultAuthenticationEntryPointFor：
		 * 		使用Http403ForbiddenEntryPoint 
		 * 配置authenticationEntryPoint && 无论是否配置defaultAuthenticationEntryPointFor：
		 * 		使用用户自定义的authenticationEntryPoint
		 * 不配置authenticationEntryPoint && 配置了1个defaultAuthenticationEntryPointFor：
		 * 		使用这里用户唯一自定义的authenticationEntryPoint
		 * 不配置authenticationEntryPoint && 配置了多个defaultAuthenticationEntryPointFor：
		 * 		new DelegatingAuthenticationEntryPoint(this.defaultEntryPointMappings);
		 * 		首先会根据RequestMatcher匹配选择不同authenticationEntryPoint
		 * 		如果都不匹配则使用defaultEntryPointMappings中的第一个authenticationEntryPoint
		 * 
		 * Http403ForbiddenEntryPoint：
		 * 		response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
		 */
	}
	
	protected void exceptionHandling__defaultAuthenticationEntryPointFor() {
		/**
		 * 不配置
		 */
	}
	
	protected void logout(){
		/**
		 * 创建一个LogoutConfigurer
		 */
	}
	
	protected void logout__logoutUrl(){
		/**
		 * 退出登录的请求链接
		 * 
		 * 默认值：String logoutUrl = "/logout";
		 * 默认情况下：
		 * 		会生成4个AntPathRequestMatcher，并组装成一个OrRequestMatcher(get, post, put, delete)
		 * 			AntPathRequestMatcher("/logout", "POST")
		 * 			AntPathRequestMatcher("/logout", "GET")
		 * 			AntPathRequestMatcher("/logout", "PUT")
		 * 			AntPathRequestMatcher("/logout", "DELETE")
		 * 		然后new一个LogoutFilter，并把这个OrRequestMatcher传递给这个LogoutFilter用于匹配是否是登出请求
		 * 			如果设置了Csrf，则只会生成AntPathRequestMatcher("/logout", "POST")传递给这个LogoutFilter
		 * 
		 * 该方法：
		 * 		将logoutRequestMatcher设置为null，即会在configure(H http)中使用自定义的logoutUrl重新生成RequestMatcher
		 * 		将this.logoutUrl设置为自定义值
		 */
	}
	
	protected void logout__logoutSuccessUrl(){
		/**
		 * 默认值：
		 * 		LogoutConfigurer中的logoutSuccessUrl的默认值为"/login?logout"（this.loginPage + "?logout"）
		 * 		创建LogoutFilter的时候需要一个LogoutSuccessHandler
		 * 		如果LogoutSuccessHandler没有自定义，也就是这个类参数为null，则创建一个SimpleUrlLogoutSuccessHandler
		 * 		然后把这个logoutSuccessUrl设置到SimpleUrlLogoutSuccessHandler的defaultTargetUrl属性里(这个属性默认值为"\")
		 * 		登出成功之后，就会返回到该地址
		 */
	}
	
	protected void logout__logoutSuccessHandler(){
		/**
		 * 退出成功后的处理器
		 * 默认值为SimpleUrlLogoutSuccessHandler 或者 DelegatingLogoutSuccessHandler 或者 自定义的LogoutSuccessHandler
		 * 重！如果自定义了LogoutSuccessHandler，那么logoutSuccessUrl参数也就没有用了
		 * 
		 * LogoutConfigurer ---> configure(H http) ---> createLogoutFilter(H http) ---> getLogoutSuccessHandler()
		 * 		if (handler == null)(这里的handler是自行通过logoutSuccessHandler(LogoutSuccessHandler)设置的) ---> createDefaultSuccessHandler() --->
		 * 			重！SimpleUrlLogoutSuccessHandler urlLogoutHandler = new SimpleUrlLogoutSuccessHandler();
		 * 			urlLogoutHandler.setDefaultTargetUrl(this.logoutSuccessUrl);
		 * 			if (this.defaultLogoutSuccessHandlerMappings.isEmpty()) ---> 
		 * 				重！return urlLogoutHandler;	
		 * 			else
		 * 				重！DelegatingLogoutSuccessHandler successHandler = new DelegatingLogoutSuccessHandler(this.defaultLogoutSuccessHandlerMappings);
		 * 				successHandler.setDefaultLogoutSuccessHandler(urlLogoutHandler);
		 * 				return successHandler;
		 * 		else
		 * 			重！return 自行通过logoutSuccessHandler(LogoutSuccessHandler logoutSuccessHandler)设置的
		 * 上面的defaultLogoutSuccessHandlerMappings通过
		 * 		defaultLogoutSuccessHandlerFor(LogoutSuccessHandler handler,RequestMatcher preferredMatcher)
		 * 设置
		 * 
		 * SimpleUrlLogoutSuccessHandler：
		 * 		alwaysUseDefaultTargetUrl == true(默认值为false) ---> return defaultTargetUrl(即：logoutSuccessUrl)
		 * 		targetUrlParameter!=null ---> targetUrl = request.getParameter(this.targetUrlParameter) ---> targetUrl != null ---> targetUrl
		 * 		useReferer == ture(默认值为false) ---> targetUrl = request.getHeader("Referer") ---> targetUrl != null ---> targetUrl
		 * 		return this.defaultTargetUrl(即：logoutSuccessUrl，默认值：/login?logout或者通过.logoutSuccessUrl(...)设置)
		 * 
		 * DelegatingLogoutSuccessHandler：
		 * 		遍历matcherToHandler(即：defaultLogoutSuccessHandlerMappings，通过.defaultLogoutSuccessHandlerFor(null, null)方法设置)
		 * 			用RequestMatcher matcher对比logoutUrl，匹配的执行对应的LogoutSuccessHandler
		 * 		如果都matcherToHandler中没有匹配的，并且defaultLogoutSuccessHandler !=null ---> 执行defaultLogoutSuccessHandler
		 * 			如果自行通过logoutSuccessHandler(LogoutSuccessHandler logoutSuccessHandler)设置了LogoutSuccessHandler
		 */
	}
	
	protected void logout__defaultLogoutSuccessHandlerFor(){
		/**
		 * 填充defaultLogoutSuccessHandlerMappings，key为RequestMatcher，value为LogoutSuccessHandler
		 * 即：url匹配不同的LogoutSuccessHandler
		 * 如果使用logoutSuccessHandler自定义了LogoutSuccessHandler，那么这个不会生效
		 * 如果没有使用logoutSuccessHandler自定义LogoutSuccessHandler，并且设置了该方法：
		 * 		会初始化一个DelegatingLogoutSuccessHandler，参考上一个方法的注释
		 * 根据设置进来的RequestMatcher执行对应的LogoutSuccesshandler
		 * 如果都没有匹配的，则执行默认的SimpleUrlLogoutSuccessHandler：
		 * 		SimpleUrlLogoutSuccessHandler urlLogoutHandler = new SimpleUrlLogoutSuccessHandler();
		 * 		urlLogoutHandler.setDefaultTargetUrl(this.logoutSuccessUrl);
		 */
	}
	
	protected void logout__logoutRequestMatcher(){
		/**
		 * 用于匹配用户发起的logout请求，只有请求匹配了才会执行登出操作，否则会直接流向下一个Filter
		 * 如果这里设置了自定义的RequestMatcher，LogoutFilter会使用自定义的RequestMatcher，否则会如下的逻辑自行创建RequestMatcher
		 * 默认情况下：
		 * 		如果设置了CSRF：return new AntPathRequestMatcher(this.logoutUrl, "POST");，即只允许默认url(/logout)或者自定义url的post请求
		 * 		如果没有设置SCRF：new OrRequestMatcher(get, post, put, delete);，即允许默认url(/logout)或者自定义url的post、get、put、delete请求
		 * 
		 * logoutUrl(String logoutUrl)会将LogoutConfigurer的类变量RequestMatcher设置为null
		 */
	}
	
	protected void logout__addLogoutHandler(){
		/**
		 * 处理登出逻辑：
		 * 		向类变量List<LogoutHandler> logoutHandlers增加新的LogoutHandler
		 * 		logoutHandlers默认有：
		 * 			SecurityContextLogoutHandler
		 * 			LogoutSuccessEventPublishingLogoutHandler
		 * 		在创建LogoutFilter的时候会将logoutHandlers传入，LogoutFilter会创建CompositeLogoutHandler包含所有logoutHandlers
		 * 			this.handler = new CompositeLogoutHandler(handlers);
		 * 		当LogoutFilter匹配到登出请求时会调用CompositeLogoutHandler的logout(request, response, auth)方法
		 * 		CompositeLogoutHandler会循环调用logoutHandlers中所有LogoutHandler的logout(request, response, auth)方法
		 * 
		 * SecurityContextLogoutHandler：
		 * 		如果invalidateHttpSession==true(默认为true)，可以通过.invalidateHttpSession(true)设置session.invalidate()
		 * 		如果clearAuthentication==true(默认为true)，可以通过.clearAuthentication(true)设置
		 * 			SecurityContext context = SecurityContextHolder.getContext();
		 * 			context.setAuthentication(null);
		 * 		SecurityContextHolder.clearContext()
		 * 			SecurityContextHolderStrategy strategy;
		 * 			strategy.clearContext();
		 * 				strategy：
		 * 					默认情况下是ThreadLocalSecurityContextHolderStrategy
		 * 				可以通过SecurityContextHolder.setStrategyName(String strategyName)来修改为其他的Strategy或者自定义的Strategy
		 * 
		 * LogoutSuccessEventPublishingLogoutHandler：
		 * 		发送一个LogoutSuccessEvent(authentication)的SpringEvent
		 */
	}
	
	protected void logout__invalidateHttpSession(){
		/**
		 * 设置SecurityContextLogoutHandler中的invalidateHttpSession，默认为true
		 * 当该值为false的时候，登出不会将SecurityContext中的Authentication置为null
		 */
	}
	
	protected void logout__clearAuthentication(){
		/**
		 * 设置SecurityContextLogoutHandler中的clearAuthentication，默认为true
		 * 当该值为false的时候，登出不会让session过期
		 */
	}
	
	protected void logout__deleteCookies(){
		/**
		 * 实现：
		 * 		return addLogoutHandler(new CookieClearingLogoutHandler(cookieNamesToClear))
		 * 用于在登出时清除指定的cookie
		 */
	}
	
	
	protected void logout__withObjectPostProcessor(){
		/**
		 * LogoutSuccessEventPublishingLogoutHandler
		 */
	}
	
	protected void sessionManagement() {
		/**
		 * 创建一个SessionManagementConfigurer
		 */
	}
	
	protected void sessionManagement__enableSessionUrlRewriting() {
		/**
		 * 允许在URL中使用会话标识符
		 * 默认不允许，一般不需要更改
		 */
	}
	
	protected void sessionManagement__invalidSessionUrl() {
		/**
		 * 当提交无效的会话 ID 时，将调用该策略，重定向到配置的 URL
		 * 
		 * 默认为null，将会：
		 * 		http.setSharedObject(InvalidSessionStrategy.class, null)
		 * 		SessionManagementFilter的类变量InvalidSessionStrategy invalidSessionStrategy = null;
		 * 			如果到无效的会话ID，SessionManagementFilter则直接return
		 * 
		 * 如果设置该url，将会：
		 * 		如果自定义了InvalidSessionStrategy，则该属性无用
		 * 		否则会初始化一个SimpleRedirectInvalidSessionStrategy(this.invalidSessionUrl)
		 * 			然后将该SimpleRedirectInvalidSessionStrategy设置到SessionManagementFilter
		 * 			当遇到无效的会话ID时，会调用SimpleRedirectInvalidSessionStrategy的onInvalidSessionDetected()方法
		 * 				如果createNewSession==true(默认为true)，可以通过withObjectPostProcessor()来修改
		 * 					创建session
		 * 				his.redirectStrategy.sendRedirect(request, response, "这里配置的invalidSessionUrl")
		 * 	
		 */
	}
	
	protected void sessionManagement__invalidSessionStrategy() {
		/**
		 * 当提交无效的会话 ID 时，将调用该策略
		 * 
		 * 如果自定义了InvalidSessionStrategy，SessionManagementFilter则会直接使用该InvalidSessionStrategy
		 * 因此，invalidSessionUrl()方法设置的属性将无效
		 */
	}
	
	protected void sessionManagement__sessionAuthenticationErrorUrl() {
		/**
		 * SessionManagementFilter调用this.failureHandler.onAuthenticationFailure(request, response, ex)发生异常：
		 * 		会在catch中进行跳转页面等操作
		 * 
		 * 默认为null，将会：
		 * 		SessionManagementFilter会自己创建failureHandler = new SimpleUrlAuthenticationFailureHandler()
		 * 		当this.sessionAuthenticationStrategy.onAuthentication(authentication, request, response);发生异常时
		 * 			重！调用this.failureHandler.onAuthenticationFailure(request, response, ex);
		 * 				response.sendError(401, "msg");，即重定向到error-page指定的页面
		 * 
		 * 如果设置该url，将会：
		 * 		new SimpleUrlAuthenticationFailureHandler(this.sessionAuthenticationErrorUrl);
		 * 		sessionManagementFilter.setAuthenticationFailureHandler(failureHandler)
		 * 		当this.sessionAuthenticationStrategy.onAuthentication(authentication, request, response);发生异常时
		 * 			重！调用this.failureHandler.onAuthenticationFailure(request, response, ex);
		 * 				this.redirectStrategy.sendRedirect(request, response, this.defaultFailureUrl);
		 * 	
		 */
	}
	
	protected void sessionManagement__sessionAuthenticationFailureHandler() {
		/**
		 * SessionManagementFilter调用this.failureHandler.onAuthenticationFailure(request, response, ex)发生异常：
		 * 		会在catch中进行跳转页面等操作
		 * 
		 * 如果自定义了sessionAuthenticationFailureHandler，SessionManagementFilter则会直接使用该sessionAuthenticationFailureHandler
		 * 因此，sessionAuthenticationErrorUrl()方法设置的属性将无效
		 */
	}
	
	protected void sessionManagement__sessionAuthenticationStrategy() {
		/**
		 * SecurityContextRepository
		 * 
		 * 传递给SessionManagementFilter的类参数sessionAuthenticationStrategy
		 * 主要负责处理认证成功的时候session管理需要执行的逻辑
		 * 
		 * 包含如下子类：
		 * 		SessionFixationProtectionStrategy：创建一个新的session，将老session的属性迁移到新的session中
		 * 		ChangeSessionIdAuthenticationStrategy：仍使用原来的session，仅修改下session id
		 * 		ConcurrentSessionControlAuthenticationStrategy：限制用户同时登陆的次数
		 * 		CompositeSessionAuthenticationStrategy：组合多个SessionAuthenticationStrategy
		 * 		CsrfAuthenticationStrategy：登陆成功之后，更换原来的csrf token
		 * 		NullAuthenticatedSessionStrategy：空实现，什么都不操作
		 * 		RegisterSessionAuthenticationStrategy：注册新session信息到SessionRegistry
		 * 
		 * SessionManagementConfigurer有三个类变量：
		 * 		sessionFixationAuthenticationStrategy：默认的ChangeSessionIdAuthenticationStrategy，可以通过sessionFixation()修改
		 * 		sessionAuthenticationStrategy：最终赋好值后传递给SessionManagementFilter，是一个CompositeSessionAuthenticationStrategy
		 * 		providedSessionAuthenticationStrategy：用户自定义的sessionAuthenticationStrategy
		 * 
		 * SessionManagementConfigurer生成sessionAuthenticationStrategy的流程：
		 * 		defaultSessionAuthenticationStrategy：
		 * 			如果用户自定义了就使用用户自定义的sessionAuthenticationStrategy
		 * 			如果用户没有自定义就使用postProcess(new ChangeSessionIdAuthenticationStrategy())
		 * 		如果maximumSessions ==null(默认值)
		 * 			sessionAuthenticationStrategy = postProcess(new CompositeSessionAuthenticationStrategy(defaultSessionAuthenticationStrategy));
		 * 			重！即：只有一个ChangeSessionIdAuthenticationStrategy
		 * 		如果maximumSessions != null(用户自己配置了该值，如果配置-1也表示不限制)
		 * 			重！获取sessionRegistry：
		 * 				先尝试用SpringContext中获取
		 * 				如果没有，则自己new SessionRegistryImpl()(在内存中管理Session)
		 * 			new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
		 * 				.setMaximumSessions(this.maximumSessions);
		 * 				.setExceptionIfMaximumExceeded(this.maxSessionsPreventsLogin);
		 * 					默认值为false，即当同一个用户的登录数量>maximumSessions，则将最久没用使用的SessionInformation的expired属性设为true
		 * 					如果设为true，即当同一个用户的登录数量>maximumSessions，则抛出SessionAuthenticationException异常
		 * 			new RegisterSessionAuthenticationStrategy(sessionRegistry);
		 * 			List<SessionAuthenticationStrategy> delegateStrategies为：
		 * 				ConcurrentSessionControlAuthenticationStrategy
		 * 				defaultSessionAuthenticationStrategy(自定义的sessionAuthenticationStrategy 或者 ChangeSessionIdAuthenticationStrategy)
		 * 				RegisterSessionAuthenticationStrategy
		 *			重！sessionAuthenticationStrategy = postProcess(new CompositeSessionAuthenticationStrategy(delegateStrategies));
		 * 				
		 * 			
		 */
	}
	
	protected void sessionManagement__sessionFixation() {
		/**
		 * Session的处理策略
		 * 
		 * sessionManagement__sessionAuthenticationStrategy()中得知：
		 * 		SessionManagementConfigurer中的sessionFixationAuthenticationStrategy默认为ChangeSessionIdAuthenticationStrategy
		 * 		如果想要更换这个实现，则可以通过sessionFixation()
		 * 
		 * newSession()：
		 * 		SessionFixationProtectionStrategy sessionFixationProtectionStrategy = new SessionFixationProtectionStrategy();
		 * 		sessionFixationProtectionStrategy.setMigrateSessionAttributes(false);
		 * 		创建一个新的session，但是不会将老session的属性迁移到新的session中，并且不会迁移Session的最大存活时间
		 * 
		 * migrateSession()：
		 * 		new SessionFixationProtectionStrategy()
		 * 		创建一个新的session，将老session的属性迁移到新的session中
		 * 
		 * changeSessionId()：
		 * 		new ChangeSessionIdAuthenticationStrategy()
		 * 		仍使用原来的session，仅修改下session id
		 * 
		 * none()：
		 * 		new NullAuthenticatedSessionStrategy()
		 * 		空实现，什么都不操作
		 */
	}
	
	protected void sessionManagement__sessionCreationPolicy() {
		/**
		 * Spring Security关于Session的各类创建策略 {@link HttpSession}
		 * 		ALWAYS：保存session状态，每次会话都保存，可能会导致内存溢出
		 * 		IF_REQUIRED：Spring Security只会在需要时创建一个HttpSession
		 * 		NEVER：不会创建HttpSession，但是会使用已经存在的HttpSession
		 * 		STATELESS：Spring Security永远不会创建HttpSession，它不会使用HttpSession来获取SecurityContext
		 * 这个策略在很多地方都有使用
		 * 
		 * 默认值：IF_REQUIRED
		 */
	}
	
	
	protected void sessionManagement__sessionConcurrency() {
		/**
		 * .sessionConcurrency(concurrencyControlConfigurer -> {
		 * 		concurrencyControlConfigurer
		 * 			.maximumSessions(1)
		 * 			.maxSessionsPreventsLogin(false)
		 * 			.expiredUrl("/")
		 * 			.expiredSessionStrategy(null) 
		 * 			.sessionRegistry(null);
		 * })
		 * 
		 * maximumSessions：默认值为null
		 * 		设置同一个用户的会话数量
		 * 
		 * maxSessionsPreventsLogin：默认值为false
		 * 		如果设置了maximumSessions，则会初始化ConcurrentSessionControlAuthenticationStrategy
		 * 			并且设置ConcurrentSessionControlAuthenticationStrategy的exceptionIfMaximumExceeded设为自定义值
		 * 			默认值为false，即当同一个用户的登录数量>maximumSessions，则将最久没用使用的SessionInformation的expired属性设为true
		 * 			如果设为true，即当同一个用户的登录数量>maximumSessions，则抛出SessionAuthenticationException异常
		 * 
		 * expiredUrl：
		 * 		如果expiredUrl==null(默认)：
		 * 		则ConcurrentSessionFilter会自己new ResponseBodySessionInformationExpiredStrategy()
		 * 		ResponseBodySessionInformationExpiredStrategy：
		 * 			HttpServletResponse response = event.getResponse();
		 * 			response.getWriter().print("This session has been expired (possibly due to multiple concurrent... ");
		 * 			response.flushBuffer();
		 * 		如果expiredUrl!=null：
		 * 			用于new SimpleRedirectSessionInformationExpiredStrategy(this.expiredUrl)
		 * 			然后会将SessionInformationExpiredStrategy交给ConcurrentSessionFilter
		 * 			ConcurrentSessionFilter在处理Session的时长问题时，如果发现Session已经过期(通过自己管理的SessionInformation)：
		 * 				先调用SecurityContextLogoutHandler:
		 * 					1：执行HttpSession.invalidate()
		 * 					2：SecurityContextHolder.getContext().setAuthentication(null);
		 * 					3：SecurityContextHolder.clearContext();
		 * 				然后调用SessionInformationExpiredStrategy
		 * 					this.redirectStrategy.sendRedirect(event.getRequest(), event.getResponse(), this.expiredUrl);
		 * 
		 * expiredSessionStrategy：
		 * 		参考上面的expiredUrl，如果自定义了SessionInformationExpiredStrategy：
		 * 			则ConcurrentSessionFilter会使用自定义的SessionInformationExpiredStrategy
		 * 		并且，expiredUrl参数将无效
		 * 
		 * sessionRegistry：用于进行Session存储的实现
		 * 		ConcurrentSessionFilter会使用sessionRegistry
		 * 		ConcurrentSessionControlAuthenticationStrategy会使用sessionRegistry
		 * 			SessionManagementFilter会使用ConcurrentSessionControlAuthenticationStrategy
		 * 		默认实现：SessionRegistryImpl
		 * 			获取SessionRegistry的顺序优先级如下：
		 * 				如果用户自定义了优先使用用户自定义的
		 * 				如果用户没有自定义，则尝试用从Spring容器中获取，如果有则使用Spring容器中的
		 * 				如果都没有，则自己new SessionRegistryImpl()
		 * 		
		 */
	}
	
	protected void sessionManagement__withObjectPostProcessor(){
		/**
		 * RegisterSessionAuthenticationStrategy
		 * SessionManagementFilter
		 * 
		 * ChangeSessionIdAuthenticationStrategy
		 * SessionFixationProtectionStrategy
		 * NullAuthenticatedSessionStrategy
		 * ConcurrentSessionControlAuthenticationStrategy
		 * RegisterSessionAuthenticationStrategy
		 * CompositeSessionAuthenticationStrategy
		 * 
		 * ConcurrentSessionFilter
		 */
	}
	
	protected void cors(){
		/**
		 * new CorsConfigurer<>()
		 * 默认值：
		 * 		CorsConfigurationSource configurationSource = null
		 * 
		 * 主要是创建一个org.springframework.web.filter.CorsFilter
		 * 
		 * CorsFilter：
		 * 		有一个默认的CorsProcessor processor = new DefaultCorsProcessor();
		 * 		然后调用该processor根据CorsConfigurationSource中的CorsConfiguration处理是否允许以及相关headers
		 * 
		 */
	}
	
	protected void cors__configurationSource(){
		/**
		 * 如果自定义了CorsConfigurationSource：return new CorsFilter(this.configurationSource)
		 * 如果Spring容器中有名叫"corsFilter"的CorsFilter：return 这个corsFilter
		 * 如果Spring容器中有名叫"corsConfigurationSource"的CorsConfigurationSource：return new CorsFilter(this.configurationSource)
		 * 如果org.springframework.web.servlet.handler.HandlerMappingIntrospector存在：
		 * 		从Spring容器中获取名叫"mvcHandlerMappingIntrospector"的HandlerMappingIntrospector
		 * 		return new CorsFilter(mappingIntrospector)
		 * return null;(会报错)
		 * 		
		 * 使用Demo：
		 * @Bean
		 * CorsConfigurationSource corsConfigurationSource() {
		 * 		CorsConfiguration corsConfiguration = new CorsConfiguration();
		 * 		corsConfiguration.setAllowedOrigins(Arrays.asList("https://www.baidu.com")); // 允许从百度站点跨域
		 * 		corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST")); // 允许GET和POST方法
		 * 		corsConfiguration.setAllowCredentials(true); // 允许携带凭证
		 * 
		 * 		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		 * 		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration); // 对所有URL生效
		 * 		//urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration); //可以添加多个corsConfiguration
		 * 		
		 * 		return urlBasedCorsConfigurationSource;
		 * }
		 */
	}
	
	protected void cors__disable(){
		/**
		 * 从SecurityBuilder中删除CorsConfigurer
		 */
	}
	
	protected void csrf(){
		/**
		 * 创建一个CsrfConfigurer(ApplicationContext)
		 * 
		 * 创建一个CsrfFilter，该filter需要：
		 * 		CsrfTokenRepository tokenRepository：
		 * 			默认值：new LazyCsrfTokenRepository(new HttpSessionCsrfTokenRepository())
		 * 		RequestMatcher requireCsrfProtectionMatcher：
		 * 			默认值：DefaultRequiresCsrfMatcher
		 * 		AccessDeniedHandler accessDeniedHandler：
		 * 			默认值：AccessDeniedHandlerImpl
		 * logoutConfigurer.addLogoutHandler(new CsrfLogoutHandler(this.csrfTokenRepository));
		 * sessionConfigurer.addSessionAuthenticationStrategy(getSessionAuthenticationStrategy());
		 * 
		 * CsrfTokenRepository：
		 * 		LazyCsrfTokenRepository：可以支持CsrfToken的懒生成，即每次生成一个
		 * 
		 * CsrfFilter的流程：
		 * 		获取CsrfToken：通过LazyCsrfTokenRepository获取之前生成并保存的CsrfToken
		 * 			HttpSessionCsrfTokenRepository：
		 * 				session.getAttribute("HttpSessionCsrfTokenRepository")
		 * 			CookieCsrfTokenRepository：
		 * 				Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
		 * 				String token = cookie.getValue();
		 * 				new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", token)
		 * 		如果CsrfToken == null：
		 * 			生成CsrfToken：
		 * 				this.tokenRepository.generateToken(request)：new SaveOnAccessCsrfToken(this.delegate, request, response, token)
		 * 					HttpSessionCsrfTokenRepository：
		 * 						new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", createNewToken())（是一个UUID）
		 * 					CookieCsrfTokenRepository：
		 * 						new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", createNewToken())（是一个UUID）
		 * 			将这个CsrfToken保存起来
		 * 				this.tokenRepository.saveToken(csrfToken, request, response);
		 * 					HttpSessionCsrfTokenRepository：
		 * 						session.setAttribute("HttpSessionCsrfTokenRepository", token);
		 * 					CookieCsrfTokenRepository：
		 * 						Cookie cookie = new Cookie("XSRF-TOKEN", tokenValue);
		 * 						cookie.setSecure((this.secure != null) ? this.secure : request.isSecure());
		 * 						cookie.setPath(StringUtils.hasLength(this.cookiePath) ? this.cookiePath : this.getRequestContext(request));
		 * 						cookie.setMaxAge((token != null) ? this.cookieMaxAge : 0);
		 * 						cookie.setHttpOnly(this.cookieHttpOnly);
		 * 						cookie.setDomain(this.cookieDomain);
		 * 			request.setAttribute("CsrfToken", csrfToken);
		 * 			request.setAttribute("_csrf", csrfToken);
		 * 			判断请求是否需要被放行：
		 * 				重！默认实现为DefaultRequiresCsrfMatcher
		 * 					"GET", "HEAD", "TRACE", "OPTIONS"请求会被放行，而POST会被要求进行CSRF校验
		 * 			获取请求传递过来的Token：
		 * 				String actualToken = request.getHeader("X-XSRF-TOKEN")
		 * 				如果为null，则actualToken = request.getParameter(""_csrf"")
		 * 			对比两个Token是否一致：
		 * 				如果不一致，调用this.accessDeniedHandler.handle(request, response, exception)
		 * 					默认实现为：AccessDeniedHandlerImpl
		 * 						response.sendError(403, HttpStatus.FORBIDDEN.getReasonPhrase());
		 */
	}
	
	protected void csrf__csrfTokenRepository(){
		/**
		 * 自定义一个Token的生成、获取、保存的Repository
		 * 
		 * 默认实现：
		 * 		LazyCsrfTokenRepository
		 * 			HttpSessionCsrfTokenRepository：一般用于前后端不分离的情况
		 * 
		 * 可以修改为：
		 * 		LazyCsrfTokenRepository
		 * 			CookieCsrfTokenRepository：一般用于前后端分离的情况
		 * 或者完全自定义
		 */
	}
	
	protected void csrf__ignoringAntMatchers(String...antPatterns){
		/**
		 * 默认情况下："GET", "HEAD", "TRACE", "OPTIONS"请求会被放行，而POST会被要求进行CSRF校验
		 * 		如果还需要其他需要放行的url，则可以在这里指定
		 * 
		 * RequestMatchers.antMatchers(antPatterns)，生成一个List<RequestMatcher> 
		 * 然后添加到CsrfConfigurer.this.ignoredCsrfProtectionMatchers
		 * 创建一个RequestMatcher requireCsrfProtectionMatcher对象set到CsrfFilter，当CsrfFilter匹配到符合要求的请求，则不进行CSRF验证
		 * 		如果不调用ignoringAntMatchers(String...antPatterns)添加自定义：
		 * 			默认实现为DefaultRequiresCsrfMatcher
		 * 				"GET", "HEAD", "TRACE", "OPTIONS"请求会被放行，而POST会被要求进行CSRF校验
		 * 		如果调用ignoringAntMatchers(String...antPatterns)添加自定义：
		 * 			new AndRequestMatcher(
		 * 				this.requireCsrfProtectionMatcher,
		 * 				new NegatedRequestMatcher(new OrRequestMatcher(this.ignoredCsrfProtectionMatchers)));
		 * 		
		 */
	}
	
	protected void csrf__ignoringRequestMatchers(){
		/**
		 * 默认情况下："GET", "HEAD", "TRACE", "OPTIONS"请求会被放行，而POST会被要求进行CSRF校验
		 * 		如果还需要其他需要放行的url，则可以在这里指定
		 * 
		 *  具体细节参考：csrf__ignoringAntMatchers(String...antPatterns)
		 */
	}
	
	protected void csrf__requireCsrfProtectionMatcher(){
		/**
		 * 默认情况下："GET", "HEAD", "TRACE", "OPTIONS"请求会被放行，而POST会被要求进行CSRF校验
		 * 		如果还需要其他需要放行的url，则可以通过ignoringAntMatchers()或者csrf__ignoringRequestMatchers()来指定
		 * 如果需要修改上面的默认情况，可以使用requireCsrfProtectionMatcher()来指定一个RequestMatcher
		 */
	}
	
	protected void csrf__sessionAuthenticationStrategy(){
		/**
		 * 指定在SessionManagementConfigurer中处理Session中的CSRFToken
		 * 默认情况下，会创建一个CsrfAuthenticationStrategy(this.csrfTokenRepository)
		 * 		add到SessionManagementConfigurer
		 * 
		 * CsrfAuthenticationStrategy：
		 * 		如果csrfTokenRepository中有token：
		 * 			这先将csrfTokenRepository中的token置为null
		 * 			然后使用csrfTokenRepository生成一个新token，并保存到csrfTokenRepository
		 * 			request.setAttribute("CsrfToken", newToken);
		 * 			request.setAttribute("_csrf", newToken);
		 */
	}
	
	protected void csrf__disable(){
		/**
		 * 从SecurityBuilder中删除CsrfConfigurer
		 */
	}
	
	protected void csrf__withObjectPostProcessor(){
		/**
		 * CsrfFilter
		 */
	}
	
	protected void sharedObject() {
		/**
		 * ContentNegotiationStrategy：
		 * 		作用对象：ExceptionHandlingConfigurer
		 * 		FormLoginConfigurer中给ExceptionHandlingConfigurer设置AuthenticationEntryPoint时需要一个RequestMatcher
		 * 		getAuthenticationEntryPointMatcher(B http)方法的实现中：
		 * 			ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
		 * 			默认值：HeaderContentNegotiationStrategy
		 * 
		 * AuthenticationManager：
		 * 		作用对象：UsernamePasswordAuthenticationFilter
		 * 
		 * DefaultLoginPageGeneratingFilter：
		 * 		FormLoginConfigurer ---> init(H http) ---> initDefaultLoginFilter(H http)
		 * 		如果该DefaultLoginPageGeneratingFilter存在 && 没有自定义过loginPage()会生效
		 * 			loginPageGeneratingFilter.setFormLoginEnabled(true);
		 * 			loginPageGeneratingFilter.setUsernameParameter(getUsernameParameter());
		 * 			loginPageGeneratingFilter.setPasswordParameter(getPasswordParameter());
		 * 			loginPageGeneratingFilter.setLoginPageUrl(getLoginPage());
		 * 			loginPageGeneratingFilter.setFailureUrl(getFailureUrl());
		 * 			loginPageGeneratingFilter.setAuthenticationUrl(getLoginProcessingUrl());
		 * 
		 * UserDetailsService：
		 * 		WebSecurityConfigurerAdapter ---> init() ---> createSharedObjects() ---> userDetailsService()
		 * 		AuthenticationManagerBuilder globalAuthBuilder = this.context.getBean(AuthenticationManagerBuilder.class);
		 * 		return new UserDetailsServiceDelegator(Arrays.asList(this.localConfigureAuthenticationBldr, globalAuthBuilder));
		 * 		delegateBuilder.getDefaultUserDetailsService();
		 * 		即：
		 * 			先调用WebSecurityConfigurerAdapter的类变量localConfigureAuthenticationBldr.getDefaultUserDetailsService()获取UserDetailsService
		 * 			如果其UserDetailsService为null，则从ApplicationContext中获取AuthenticationManagerBuilder，调用getDefaultUserDetailsService()获取UserDetailsService
		 * 			然后调用该UserDetailsService的loadUserByUsername()方法获取用户信息进行验证
		 * 
		 * InvalidSessionStrategy：
		 * 		如：SimpleRedirectInvalidSessionStrategy
		 * 
		 * SessionAuthenticationStrategy：
		 * 		当提交无效的会话 ID 时，将调用该策略，重定向到配置的 URL
		 * 		如：CompositeSessionAuthenticationStrategy
		 * 
		 * SessionCreationPolicy
		 * 
		 * SecurityContextRepository：
		 * 		用于保存SecurityContext，默认实现是一个SecurityContextImpl
		 * 		SessionManagementConfigurer.init：
		 * 			尝试获取http.getSharedObject(SecurityContextRepository.class)
		 * 			如果没有获取到，并且SessionCreationPolicy.STATELESS：
		 * 				http.setSharedObject(SecurityContextRepository.class, new NullSecurityContextRepository());
		 * 			如果没有获取到，并且SessionCreationPolicy不是STATELESS：
		 * 				HttpSessionSecurityContextRepository httpSecurityRepository = new HttpSessionSecurityContextRepository();
		 * 				http.setSharedObject(SecurityContextRepository.class, httpSecurityRepository);
		 * 		SecurityContextConfigurer：
		 * 			这个XxxConfigurer的securityContextRepository(...)方法其实就是将自定义的SecurityContextRepository执行
		 * 				getBuilder().setSharedObject(SecurityContextRepository.class, securityContextRepository);
		 * 		SecurityContextConfigurer.configure：
		 * 			http.getSharedObject(SecurityContextRepository.class);
		 * 			如果为null，则：new HttpSessionSecurityContextRepository()
		 * 
		 * AuthenticationTrustResolver：
		 * 		默认情况的实现一般为AuthenticationTrustResolverImpl，用于判断Authentication是否是一定的Authentication
		 * 		例如：isAnonymous(Authentication authentication)、isRememberMe(Authentication authentication)
		 * 		其实就是根据Authentication的Class的父子关系来判断的
		 * 		SessionManagementFilter会使用
		 * 
		 */
	}
	
	
	
}
