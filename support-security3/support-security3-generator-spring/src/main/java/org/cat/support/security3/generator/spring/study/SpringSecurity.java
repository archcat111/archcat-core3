package org.cat.support.security3.generator.spring.study;

/**
 * 
 * @author 王云龙
 * @date 2021年11月5日 上午11:01:16
 * @version 1.0
 * @description 
 * 
 * HttpSecurity
 * WebSecurityConfigurerAdapter
 * ExceptionTranslationFilter
 *
 */
public class SpringSecurity {
	
	protected void SpringSecurityInit() {
		/**
		 * SpringSecurity的初始化
		 * 
		 * SecurityAutoConfiguration：
		 * 		SpringBoot初始化SpringSecurity的起始点，该类import了WebSecurityEnablerConfiguration.class
		 * 
		 * WebSecurityEnablerConfiguration：
		 * 		运行条件：
		 * 			名叫springSecurityFilterChain的Bean在Spring容器中不存在
		 * 			ClassPath中存在类EnableWebSecurity
		 * 			@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
		 * 		该类标有注解@EnableWebSecurity
		 * 
		 * @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)：
		 * 		如果classPath中有org.springframework.web.context.support.GenericWebApplicationContext（spring-web.jar），则为ConditionalOnWebApplication.Type.SERVLET
		 * 		如果classPath中有org.springframework.web.reactive.HandlerResult（spring-webflux.jar），则为ConditionalOnWebApplication.Type.REACTIVE
		 * 		该注解的默认值是ConditionalOnWebApplication.Type.ANY，表示：可以匹配任意
		 * 
		 * @EnableWebSecurity：
		 * 		该注解类通过@Configuration和@Import配合使用引入了一个配置类（重！WebSecurityConfiguration）
		 * 		和两个ImportSelector（SpringWebMvcImportSelector，OAuth2ImportSelector）
		 * 
		 * WebSecurityConfiguration：重！
		 * 		@Bean(name = "springSecurityFilterChain")
		 * 		public Filter springSecurityFilterChain() throws Exception
		 * 
		 * WebSecurity：
		 * 		该类变量WebSecurity是在setFilterChainProxySecurityConfigurer(..., ...)方法中创建
		 * 		setFilterChainProxySecurityConfigurer(..., ...)：
		 * 			参数1：ObjectPostProcessor<Object> objectPostProcessor
		 * 			参数2：@Value("#{@autowiredWebSecurityConfigurersIgnoreParents.getWebSecurityConfigurers()}") List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers
		 * 				通过AutowiredWebSecurityConfigurersIgnoreParents的getWebSecurityConfigurers()从Spring容器中
		 * 					获取所有实现了WebSecurityConfigurer接口的Bean
		 * 			代码片段：
		 * 				this.webSecurity = objectPostProcessor.postProcess(new WebSecurity(objectPostProcessor));
		 * 				从代码中可以看到，它是直接被new出来的
		 * 
		 * 初始化自定义的WebSecurityConfigurerAdapter：
		 * 		如果Spring容器中没有实现了WebSecurityConfigurer接口的Bean && 没有SecurityFilterChain，则直接new一个WebSecurityConfigurerAdapter
		 * 		public Filter springSecurityFilterChain() throws Exception：
		 * 			if (!hasConfigurers && !hasFilterChain) {
		 * 				WebSecurityConfigurerAdapter adapter = this.objectObjectPostProcessor.postProcess(
		 * 					new WebSecurityConfigurerAdapter() {}
		 * 				);
		 * 				this.webSecurity.apply(adapter);
		 * 			}
		 * 			...
		 * 			return this.webSecurity.build();
		 * 			如上知道Filter通过WebSecurityConfiguration的类变量webSecurity来创建
		 * 		该类有一个类参数disableDefaults，默认值是false
		 * 			当该值是false时，会初始化一些默认的XxxConfigurer以及add一些Filter：
		 * 				http.csrf();
		 * 				http.addFilter(new WebAsyncManagerIntegrationFilter());
		 * 				http.exceptionHandling();
		 * 				http.headers();
		 * 				http.sessionManagement();
		 * 				http.securityContext();
		 * 				http.requestCache();
		 * 				http.anonymous();
		 * 				http.servletApi();
		 * 				http.apply(new DefaultLoginPageConfigurer<>());
		 * 				http.logout();
		 * 				还有：
		 * 				List<AbstractHttpConfigurer> defaultHttpConfigurers = SpringFactoriesLoader.loadFactories(AbstractHttpConfigurer.class, classLoader);
		 * 				for (AbstractHttpConfigurer configurer : defaultHttpConfigurers) {
		 * 					this.http.apply(configurer);
		 * 				}
		 * 		因为上面的配置默认filter的代码执行完之后才会执行configure(this.http)
		 * 			这个configure(this.http)是一般情况下用户覆写的方法，而同一个XxxConfigurer会覆盖前一个，所以用户自定义的会生效
		 * 			
		 * 
		 */
	}
	
	protected void HttpSecurity() {
		/**
		 * HttpSecurity 
		 * 		extends AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity>
		 * 			extends AbstractSecurityBuilder
		 * 				implements SecurityBuilder
		 * 		implements SecurityBuilder<DefaultSecurityFilterChain>
		 * 		implements HttpSecurityBuilder<HttpSecurity>
		 * 			extends SecurityBuilder<DefaultSecurityFilterChain>
		 * 
		 * HttpSecurity有一个doBuild方法(在父类AbstractConfiguredSecurityBuilder中)
		 * 		beforeInit()：没有逻辑，可以自定义
		 * 		init()：初始化WebSecurityConfigurerAdapter(SecurityConfigurer的实现之一)中配置的N多XxxConfigurer(调用XxxConfigurer的init())
		 * 		beforeConfigure()：没有逻辑，可以自定义
		 * 		configure()：配置WebSecurityConfigurerAdapter(SecurityConfigurer的实现之一)中配置的N多XxxConfigurer(调用XxxConfigurer的configure())
		 * 		performBuild()：没有逻辑，可以自定义
		 * 
		 * HttpSecurity内维护了一个FilterList
		 * 		当在configure(HttpSecurity http)中配置HttpSecurity时，其实就是在初始化N多的XxxConfigurer
		 * 			例如当调用http.formLogin()：return FormLoginConfigurer<HttpSecurity>
		 * 		在初始化这些XxxConfigurer时，其会创建对应的Filter加入到HttpSecurity内维护的FilterList里
		 * 
		 * WebSecurityConfigurerAdapter：
		 * 		SecurityConfigurer的实现之一
		 * 			是用来初始化和配置HttpSecurity的
		 * 			public interface SecurityConfigurer<O, B extends SecurityBuilder<O>> {
		 * 				void init(B builder) throws Exception; //用于初始化
		 * 				void configure(B builder) throws Exception; //用于配置	
		 * 			}
		 * 			SecurityConfigurer有很多实现，这些实现都是用来配置一些特定的同认证授权相关的功能的
		 * 				例如：OAuth2ClientConfigurer用来配置 OAuth2 客户端的
		 * 
		 * 
		 */
	}
	
	protected void HttpSecurity__XxxConfigurer() {
		/**
		 * HttpSecurity http：
		 * 		and()、disable()：返回HttpSecurity自身
		 * 		formLogin()：return FormLoginConfigurer<HttpSecurity>
		 * 		authorizeRequests()：return ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry
		 * 		anonymous()：return AnonymousConfigurer<HttpSecurity>
		 * 		csrf()：return CsrfConfigurer<HttpSecurity>
		 * 		logout()：return LogoutConfigurer<HttpSecurity>
		 * 		sessionManagement()：return SessionManagementConfigurer<HttpSecurity>
		 * 		cors()：return CorsConfigurer<HttpSecurity>
		 * 		exceptionHandling()：ExceptionHandlingConfigurer
		 * 		......
		 * 
		 * 这里其实就是一个个的去配置这些XxxConfigurer，然后把这些XxxConfigurer保存在其HttpSecurity的父类中的类变量configurers里面
		 * 然后逐个初始化
		 */
	}
	
	protected void GlobalAuthenticationConfigurerAdapter() {
		/**
		 * 类似WebSecurityConfigurerAdapter，不过是以全局为角度，获取Spring容器中的Bean来初始化各种基础组件的
		 * 已有实现类有：
		 * 		EnableGlobalAuthenticationAutowiredConfigurer：
		 * 			获取@EnableGlobalAuthentication相关的信息，打日志，没别的功能
		 * 		InitializeAuthenticationProviderBeanManagerConfigurer：
		 * 			getBeanOrNull(AuthenticationProvider.class)，然后设置给AuthenticationManagerBuilder
		 * 			如果AuthenticationManagerBuilder的authenticationProviders不为空或者parentAuthenticationManager不为null则不设置
		 * 		InitializeUserDetailsBeanManagerConfigurer：
		 * 			getBeanOrNull(PasswordEncoder.class);
		 * 			getBeanOrNull(UserDetailsPasswordService.class);
		 * 			new DaoAuthenticationProvider();
		 * 			然后设置给AuthenticationManagerBuilder
		 * 			如果AuthenticationManagerBuilder的authenticationProviders不为空或者parentAuthenticationManager不为null则不设置
		 */
	}
	
	protected void WebSecurityConfigurerAdapter() {
		/**
		 * <code>WebSecurityConfigurerAdapterDemo</code>
		 */
	}
	
	
	protected void SharedObject() {
		/**
		 * 例如：在WebSecurityConfigurerAdapter中，调用HttpSecurity.sessionManagement()获取的
		 * 		SessionManagementConfigurer<H extends HttpSecurityBuilder<H>>对象的init(H http)方法中有如下：
		 * 		SecurityContextRepository securityContextRepository = http.getSharedObject(SecurityContextRepository.class);
		 * 		从HttpSecurity对象中获取SecurityContextRepository实例
		 * 
		 * 顾名思义，SharedObject的意思是可共享的对象。有点分布式对象的感觉
		 * 它的作用是如果一些对象你希望在不同的作用域配置中共享它们就把这些对象变成SharedObject
		 * 
		 * SharedObject会以其Class类型为Key,实例为Value存储到一个HashMap<Class<?>,Object>中
		 * 具体可看HttpSecurity源码，在HttpSecurity的构造函数中：
		 * 		setSharedObject(AuthenticationManagerBuilder.class, authenticationBuilder);
		 * 即注入AuthenticationManagerBuilder，我们熟知的AuthenticationManagerBuilder在这里被共享
		 * 
		 * WebSecurityConfigurerAdapter（作为SecurityConfigurer，是用来配置和初始化HttpSecurity的）
		 * 		implements WebSecurityConfigurer<WebSecurity>
		 * 			extends SecurityConfigurer<Filter, T extends SecurityBuilder<Filter>>
		 * 		init() ---> HttpSecurity http = getHttp(); --->
		 * 			Map<Class<?>, Object> sharedObjects = createSharedObjects(); --->
		 * 			this.http = new HttpSecurity(this.objectPostProcessor, this.authenticationBuilder, sharedObjects);
		 * 				Map<Class<?>, Object> sharedObjects = new HashMap<>();
		 * 				sharedObjects.putAll(this.localConfigureAuthenticationBldr.getSharedObjects());
		 * 				sharedObjects.put(UserDetailsService.class, userDetailsService());
		 * 				sharedObjects.put(ApplicationContext.class, this.context);
		 * 				sharedObjects.put(ContentNegotiationStrategy.class, this.contentNegotiationStrategy);
		 * 				sharedObjects.put(AuthenticationTrustResolver.class, this.trustResolver);
		 * 				return sharedObjects;
		 * 
		 * 当在HttpSecurity的配置中的其它地方需要用到例如ClientRegistrationRepository时
		 * 		可以直接通过getSharedObject获取
		 */
	}
	
	protected void AuthenticationManager() {
		/**
		 * 用户认证的管理类
		 * 所有的认证请求（比如login）都会通过提交一个token给AuthenticationManager的authenticate()方法来实现
		 * 当然事情肯定不是它来做，具体校验动作会由AuthenticationManager将请求转发给具体的实现类来做
		 * 根据实现反馈的结果再调用具体的Handler来给用户以反馈
		 * 
		 * 默认实现：ProviderManager
		 * 
		 * AuthenticationManager的初始化：
		 * 		WebSecurityConfigurerAdapter：
		 * 			--> authenticationManager() 
		 * 				第一种情况：this.authenticationConfiguration.getAuthenticationManager()
		 * 				第二种情况，覆写了configure(this.localConfigureAuthenticationBldr)：
		 * 					this.authenticationManager = this.localConfigureAuthenticationBldr.build();
		 * 		authenticationConfiguration是怎么来的：
		 * 			这里的authenticationConfiguration是通过Spring的@Autowired注入进来
		 * 			AuthenticationManagerBuilder authBuilder = this.applicationContext.getBean(AuthenticationManagerBuilder.class)
		 * 			这里的AuthenticationManagerBuilder是在Spring容器中获取的
		 * 			如果是第一次通过Builder构建 将全局的认证配置整合到Builder中  那么以后就不用再整合全局的配置了
		 * 			如果this.authenticationManager = authBuilder.build()构建的结果为null，则：
		 * 				再次尝试去Spring IoC 获取懒加载的 AuthenticationManager
		 * 		localConfigureAuthenticationBldr是怎么来的：
		 * 			WebSecurityConfigurerAdapter的setApplicationContext(ApplicationContext context)方法
		 * 			这个方法有@Autowired
		 * 				这个方法会new DefaultPasswordEncoderAuthenticationManagerBuilder(objectPostProcessor, passwordEncoder) {
		 * 
		 * AuthenticationManagerBuilder的初始化：
		 * 		AuthenticationManagerBuilder注入的过程也是在AuthenticationConfiguration中完成的
		 * 		new DefaultPasswordEncoderAuthenticationManagerBuilder(objectPostProcessor, defaultPasswordEncoder);
		 * 
		 * 认证处理器的注入：
		 * 		authenticationConfiguration --> InitializeAuthenticationProviderBeanManagerConfigurer()
		 * 			尝试从Spring IoC获取 AuthenticationProvider
		 * 			AuthenticationProvider authenticationProvider = getBeanOrNull(AuthenticationProvider.class);
		 * 			获取得到就配置到AuthenticationManagerBuilder中，最终会配置到AuthenticationManager中
		 * 
		 * 重！自定义AuthenticationManager：
		 * 		如果不能完全摸透这个AuthenticationManager，那么最好覆写WebSecurityConfigurerAdapter的
		 * 		configure(AuthenticationManagerBuilder authenticationManagerBuilder)方法
		 * 		然后编写例如如下的扩展：
		 * 			authenticationManagerBuilder.authenticationProvider(this.authenticationProvider);
		 * 			authenticationManagerBuilder.setSharedObject(null, null);
		 * 		但是不能加上super.configure(auth);
		 * 			否则就不会用这里自定义的authenticationManagerBuilder来创建AuthenticationManager了
		 */
	}
	
	protected void AuthenticationProvider() {
		/**
		 * 认证的具体实现类
		 * 一个provider是一种认证方式的实现
		 * 		比如提交的用户名密码我是通过和DB中查出的user记录做比对实现的，那就有一个DaoProvider
		 * 		如果我是通过CAS请求单点登录系统实现，那就有一个CASProvider
		 * 按照Spring一贯的作风，主流的认证方式它都已经提供了默认实现，比如DAO、LDAP、CAS、OAuth2等
		 * 
		 * AuthenticationManager只是一个代理接口，默认实现类是ProviderManager
		 * 一个ProviderManager可以包含多个AuthenticationProvider
		 * 每个AuthenticationProvider通过实现一个support方法来表示自己支持那种Token的认证
		 * AuthenticationProvider提供具体的认证
		 * 
		 * 自定义AuthenticationProvider：
		 * 		WebSecurityConfigurerAdapter有一个configure(AuthenticationManagerBuilder authenticationManagerBuilder)方法
		 * 		可以编写多个authenticationManagerBuilder.authenticationProvider(this.authenticationProvider);来设置自己的Provider
		 * 		WebSecurityConfigurerAdapter --> init() --> getHttp() --> authenticationManager() --> configure(this.localConfigureAuthenticationBldr)
		 * 		这里的configure(this.localConfigureAuthenticationBldr)就是上面的configure(AuthenticationManagerBuilder authenticationManagerBuilder)
		 * 		用户自定义这个方法相当于给WebSecurityConfigurerAdapter的localConfigureAuthenticationBldr(这是一个uthenticationManagerBuilder)类变量添加AuthenticationProvider
		 * 		configure(this.localConfigureAuthenticationBldr)方法的默认实现中会将disableLocalConfigureAuthenticationBldr设为true
		 * 		而如果这个参数为true，则会调用this.authenticationConfiguration.getAuthenticationManager()来获取AuthenticationManager
		 * 		所以这里在覆写该方法时不要调用super.configure将其设为true，而且默认为false则不会调用this.authenticationConfiguration.getAuthenticationManager()来获取AuthenticationManager
		 * 		重！这时候使用就会调用this.localConfigureAuthenticationBldr.build()来获取AuthenticationManager了并且应用用户自定义的AuthenticationProvider
		 * 		
		 */
	}
	
	protected void UserDetailService() {
		/**
		 * 用户认证通过Provider来做，所以Provider需要拿到系统已经保存的认证信息
		 * 获取用户信息的接口spring-security抽象成UserDetailService
		 * 虽然叫Service,但是我更愿意把它认为是我们系统里经常有的UserDao
		 * 
		 * 初始化：
		 * 		参考上面AuthenticationManager()中的注释
		 * 		authenticationConfiguration --> InitializeUserDetailsBeanManagerConfigurer()
		 * 			UserDetailsService userDetailsService = getBeanOrNull(UserDetailsService.class);
		 * 			PasswordEncoder passwordEncoder = getBeanOrNull(PasswordEncoder.class);
		 * 			UserDetailsPasswordService passwordManager = getBeanOrNull(UserDetailsPasswordService.class);
		 * 			DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		 * 			...各种set
		 * 			auth.authenticationProvider(provider);
		 */
	}
	
	protected void AuthenticationToken() {
		/**
		 * 所有提交给AuthenticationManager的认证请求都会被封装成一个Token的实现
		 * 		比如最容易理解的UsernamePasswordAuthenticationToken
		 */
	}
	
	protected void SecurityContext() {
		/**
		 * 当用户通过认证之后，就会为这个用户生成一个唯一的SecurityContext
		 * 		里面包含用户的认证信息Authentication
		 * 通过SecurityContext我们可以获取到用户的标识Principle和授权信息GrantedAuthrity
		 * 在系统的任何地方只要通过SecurityHolder.getSecruityContext()就可以获取到SecurityContext
		 */
	}
	
	protected void filterList() {
		/**
		 * 
		 * HttpSecurity 
		 * 		extends AbstractConfiguredSecurityBuilder
		 * 		implements SecurityBuilder, HttpSecurityBuilder
		 * 
		 * 如下列表在HttpSecurityBuilder的H addFilter(Filter filter)方法注释上：
		 * 		ChannelProcessingFilter（order：100）
		 * 		WebAsyncManagerIntegrationFilter extends OncePerRequestFilter（order：300）
		 * 		SecurityContextPersistenceFilter（order：400）
		 * 		HeaderWriterFilter extends OncePerRequestFilter（order：500）
		 * 		CorsFilter extends OncePerRequestFilter（order：600）
		 * 		CsrfFilter extends OncePerRequestFilter（order：700）
		 * 		LogoutFilter（order：800）
		 * 		"OAuth2AuthorizationRequestRedirectFilter"（order：900）
		 * 		"Saml2WebSsoAuthenticationRequestFilter"（order：1000）
		 * 		X509AuthenticationFilter（order：1100）
		 * 		AbstractPreAuthenticatedProcessingFilter（order：1200）
		 * 		"CasAuthenticationFilter"（order：1300）
		 * 		"OAuth2LoginAuthenticationFilter"（order：1400）
		 * 		"Saml2WebSsoAuthenticationFilter"（order：1500）
		 * 		UsernamePasswordAuthenticationFilter（order：1600）
		 * 		OpenIDAuthenticationFilter（order：1800）
		 * 		org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter（order：1900）
		 * 		org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter（order：2000）
		 * 		ConcurrentSessionFilter（order：2100）
		 * 		DigestAuthenticationFilter（order：2200）
		 * 		org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter（order：2300）
		 * 		BasicAuthenticationFilter（order：2400）
		 * 		RequestCacheAwareFilter（order：2500）
		 * 		SecurityContextHolderAwareRequestFilter（order：2600）
		 * 		JaasApiIntegrationFilter（order：2700）
		 * 		RememberMeAuthenticationFilter（order：2800）：通过cookie来实现remember me功能的Filter
		 * 		AnonymousAuthenticationFilter（order：2900）
		 * 		"OAuth2AuthorizationCodeGrantFilter"（order：3000）
		 * 		SessionManagementFilter（order：3100）
		 * 		ExceptionTranslationFilter（order：3200）：捕获所有Spring Security抛出的异常，并决定处理方式
		 * 		FilterSecurityInterceptor（order：3300）：权限校验的拦截器，访问的url权限不足时会抛出异常
		 * 		AuthorizationFilter extends OncePerRequestFilter（order：3400）
		 * 		SwitchUserFilter（order：3500）
		 * 
		 * Filter的顺序：
		 * 		SpringSecurity 5.0.7中，是在FilterComparator中实现的
		 * 		SpringSecurity 5.5.3中，是在FilterOrderRegistration中实现的
		 * 		初始化顺序是100，步长是100
		 */
	}
	
	protected void filterList__ChannelProcessingFilter() {
		/**
		 * 通常是用来过滤哪些请求必须用 https 协议， 哪些请求必须用 http 协议， 哪些请求随便用哪个协议都行
		 * ChannelProcessingFilter 通过 HttpScurity#requiresChannel() 等相关方法引入其配置对象 ChannelSecurityConfigurer 来进行配置
		 * 
		 * 它主要有两个属性：
		 * 		ChannelDecisionManager channelDecisionManager
		 * 		FilterInvocationSecurityMetadataSource securityMetadataSource
		 * 
		 * ChannelDecisionManager：
		 * 		用来判断请求是否符合既定的协议规则。它维护了一个 ChannelProcessor 列表
		 * 		这些ChannelProcessor 是具体用来执行 
		 * 			ANY_CHANNEL 策略（任何通道都可以）
		 * 			REQUIRES_SECURE_CHANNEL 策略 （只能通过https 通道）
		 * 			REQUIRES_INSECURE_CHANNEL 策略 （只能通过 http 通道）
		 * 
		 * FilterInvocationSecurityMetadataSource：
		 * 		用来存储 url 与 对应的ANY_CHANNEL、REQUIRES_SECURE_CHANNEL、REQUIRES_INSECURE_CHANNEL 的映射关系
		 * 		
		 */
	}
	
	protected void filterList__WebAsyncManagerIntegrationFilter__默认开启() {
		/**
		 * 
		 * 用于集成SecurityContext到Spring异步执行机制中的WebAsyncManager
		 * 用来处理异步请求的安全上下文。具体逻辑为：
		 * 		1：从请求属性上获取所绑定的WebAsyncManager，如果尚未绑定，先做绑定
		 * 		2：从asyncManager 中获取 key 为 CALLABLE_INTERCEPTOR_KEY 的安全上下文多线程处理器SecurityContextCallableProcessingInterceptor
		 * 			 如果获取到的为 null，新建一个 SecurityContextCallableProcessingInterceptor 并绑定 CALLABLE_INTERCEPTOR_KEY 注册到 asyncManager 中
		 * 
		 * SecurityContextCallableProcessingInterceptor：
		 * 		它实现了接口 CallableProcessingInterceptor
		 * 		当它被应用于一次异步执行时，beforeConcurrentHandling() 方法会在调用者线程执行
		 * 		该方法会相应地从当前线程获取SecurityContext,然后被调用者线程中执行逻辑时，会使用这个 SecurityContext
		 * 		从而实现安全上下文从调用者线程到被调用者线程的传输
		 * 
		 * WebAsyncManagerIntegrationFilter 通过 WebSecurityConfigurerAdapter#getHttp()方法添加到 HttpSecurity 中
		 * 		成为 DefaultSecurityFilterChain 的一个链节
		 */
	}
	
	protected void filterList__SecurityContextPersistenceFilter__默认开启() {
		/**
		 * 
		 * 主要控制 SecurityContext 的在一次请求中的生命周期
		 * 请求来临时，创建SecurityContext 安全上下文信息，请求结束时清空 SecurityContextHolder
		 * 
		 * SecurityContextPersistenceFilter 通过 HttpScurity#securityContext() 及相关方法引入其配置对象 SecurityContextConfigurer 来进行配置
		 * 
		 * 该Filter中有一个参数forceEagerSessionCreation，默认为false
		 * 当该参数为true时：
		 * 		HttpSession session = request.getSession();
		 * 		if (this.logger.isDebugEnabled() && session.isNew()) {
		 * 			this.logger.debug(LogMessage.format("Created session %s eagerly", session.getId()));
		 * 		}
		 * forceEagerSessionCreation什么时候为true？
		 * 		在使用SecurityContextConfigurer配置SecurityContextPersistenceFilter时
		 * 		会获取已经配置的SessionManagementConfigurer实例，如果该实例中的sessionPolicy!=null并且sessionPolicy==ALWAYS
		 * 		如果sessionPolicy==null，则会getSharedObject(SessionCreationPolicy.class)，如果该值为ALWAYS
		 * 		即只有获取的sessionPolicy==ALWAYS时，即表示永远都会创建Session
		 * 
		 * SecurityContextRepository repo：
		 * 		在这里用来获取和存储SecurityContext
		 * 		这个的实例哪来的？
		 * 			http.getSharedObject(SecurityContextRepository.class)
		 * 			如果上面的结果为null，则new HttpSessionSecurityContextRepository
		 * 
		 * HttpSessionSecurityContextRepository：
		 * 		从session中获取key为"SPRING_SECURITY_CONTEXT"的值，即一个SecurityContext
		 * 
		 */
	}
	
	protected void filterList__HeaderWriterFilter__默认开启() {
		/**
		 * 
		 * 用来给 http 响应添加一些 Header
		 * 比如 X-Frame-Options, X-XSS-Protection，X-Content-Type-Options
		 * 		1：可以在该FIlter中添加一些Header，然后执行后续Filter
		 * 		2：可以在该Filter执行完后续Filter后返回到这里时添加一些Header，然后继续返回之前的Filter
		 * 
		 * 可以通过 HttpScurity#headers() 来定制请求Header
		 */
	}
	
	protected void filterList__CorsFilter() {
		/**
		 * 跨域相关的过滤器
		 * 这是Spring MVC Java配置和XML 命名空间 CORS 配置的替代方法
		 * 仅对依赖于spring-web的应用程序有用，不适用于spring-webmvc，或 要求在javax.servlet.Filter 级别进行CORS检查的安全约束链接
		 * 可以通过 HttpSecurity#cors() 来定制，CorsConfigurer
		 * 
		 * 优先看CorsConfigurer是否配置了configurationSource，如果有则new CorsFilter(this.configurationSource)
		 * 如果没获取到则context.getBean("corsFilter", CorsFilter.class)
		 * 如果没获取到则context.getBean("corsConfigurationSource",CorsConfigurationSource.class); 
		 * 		new CorsFilter(this.configurationSource)
		 * 如果没获取到则：
		 * 		先看org.springframework.web.servlet.handler.HandlerMappingIntrospector的class是否存在
		 * 		如果存在则context.getBean("mvcHandlerMappingIntrospector",HandlerMappingIntrospector.class); 如果获取不到则报错
		 * 		如果存在则new CorsFilter(mappingIntrospector)
		 * 返回null
		 */
	}
	
	protected void filterList__CsrfFilter__默认开启() {
		/**
		 * 
		 * 
		 * 用于防止csrf攻击，前后端使用json交互需要注意的一个问题
		 * 可以通过 HttpSecurity.csrf() 来开启或者关闭它
		 * 在使用 jwt 等 token 技术时，是不需要这个的
		 */
	}
	
	protected void filterList__LogoutFilter__默认开启() {
		/**
		 * 处理注销的过滤器
		 * 可以通过 HttpSecurity.logout() 来定制注销逻辑
		 */
	}
	
	protected void filterList__OAuth2AuthorizationRequestRedirectFilter() {
		/**
		 * 这个需要依赖 spring-scurity-oauth2 相关的模块
		 * 该过滤器是处理 OAuth2 请求首选重定向相关逻辑的
		 */
	}
	
	protected void filterList__Saml2WebSsoAuthenticationRequestFilter() {
		/**
		 * 这个需要用到 Spring Security SAML 模块
		 * 这是一个基于 SMAL 的 SSO 单点登录请求认证过滤器
		 * 
		 * SAML 即安全断言标记语言，英文全称是 Security Assertion Markup Language
		 * 它是一个基于 XML 的标准，用于在不同的安全域（security domain）之间交换认证和授权数据
		 * 在 SAML 标准定义了身份提供者 (identity provider) 和服务提供者 (service provider)，这两者构成了前面所说的不同的安全域
		 * SAML 是 OASIS 组织安全服务技术委员会(Security Services Technical Committee) 的产品
		 * 
		 * SAML（Security Assertion Markup Language）是一个 XML 框架，也就是一组协议，可以用来传输安全声明
		 * 比如，两台远程机器之间要通讯，为了保证安全，我们可以采用加密等措施，也可以采用 SAML 来传输
		 * 传输的数据以 XML 形式，符合 SAML 规范
		 * 这样我们就可以不要求两台机器采用什么样的系统，只要求能理解 SAML 规范即可
		 * 显然比传统的方式更好。SAML 规范是一组 Schema 定义
		 * 
		 * 在Web Service 领域，schema 就是规范，在 Java 领域，API 就是规范
		 */
	}
	
	protected void filterList__X509AuthenticationFilter() {
		/**
		 * X509 认证过滤器
		 * 可以通过 HttpSecurity#X509() 来启用和配置相关功能
		 */
	}
	
	protected void filterList__AbstractPreAuthenticatedProcessingFilter() {
		/**
		 * 处理经过预先认证的身份验证请求的过滤器的基类
		 * 其中认证主体已经由外部系统进行了身份验证
		 * 目的只是从传入请求中提取主体上的必要信息，而不是对它们进行身份验证
		 * 你可以继承该类进行具体实现并通过 HttpSecurity#addFilter 方法来添加个性化的AbstractPreAuthenticatedProcessingFilter
		 */
	}
	
	protected void filterList__CasAuthenticationFilter() {
		/**
		 * CAS 单点登录认证过滤器
		 * 依赖 Spring Security CAS 模块
		 */
	}
	
	protected void filterList__OAuth2LoginAuthenticationFilter() {
		/**
		 * 依赖 spring-scurity-oauth2 相关的模块
		 * OAuth2 登录认证过滤器
		 * 处理通过 OAuth2 进行认证登录的逻辑
		 */
	}
	
	protected void filterList__Saml2WebSsoAuthenticationFilter() {
		/**
		 * 依赖 Spring Security SAML 模块
		 * 这是一个基于 SMAL 的 SSO 单点登录认证过滤器
		 */
	}
	
	protected void filterList__UsernamePasswordAuthenticationFilter() {
		/**
		 * 处理用户以及密码认证的核心过滤器
		 * 认证请求提交的username和 password，被封装成token进行一系列的认证，便是主要通过这个过滤器完成的
		 * 在表单认证的方法中，这是最最关键的过滤器
		 * 
		 * 登录之后如何创建Session：
		 * 		AbstractAuthenticationProcessingFilter的父类AbstractAuthenticationProcessingFilter中
		 * 			认证完成后调用this.sessionStrategy.onAuthentication(authenticationResult, request, response);
		 * 		this.sessionStrategy：
		 * 			FormLoginConfigurer的父类AbstractAuthenticationFilterConfigurer中的configure(B http)方法中
		 * 				sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
		 * 				获取到sessionAuthenticationStrategy后set到UsernamePasswordAuthenticationFilter
		 * 			而这个sessionAuthenticationStrategy则是在SessionManagementConfigurer中创建的
		 * 				默认情况http.sessionManagement()是开启时，不做其他配置时：
		 * 					sessionAuthenticationStrategy为CompositeSessionAuthenticationStrategy(包含多个SessionAuthenticationStrategy)
		 * 					这时CompositeSessionAuthenticationStrategy里面只有ChangeSessionIdAuthenticationStrategy
		 * 			ChangeSessionIdAuthenticationStrategy默认情况下类变量alwaysCreateSession=false，也不会创建Session
		 * 		UsernamePasswordAuthenticationFilter的流程走完后会一步一步返回到SecurityContextPersistenceFilter
		 * 		SecurityContextPersistenceFilter：
		 * 			有一句this.repo.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
		 * 			这里的repo默认情况下为HttpSessionSecurityContextRepository
		 * 			这句会调用SaveToSessionResponseWrapper的createNewSessionIfAllowed(...)方法
		 * 				如果该request在最早进入这里时就已经有Session则不创建
		 * 				如果HttpSessionSecurityContextRepository.this.allowSessionCreation==false则不创建（这里的默认值为true）
		 * 				如果HttpSessionSecurityContextRepository.this.contextObject.equals(context)则不创建
		 * 				然后就会HttpSession session = this.request.getSession(true);
		 */
	}
	
	protected void filterList__OpenIDAuthenticationFilter() {
		/**
		 * 基于OpenID 认证协议的认证过滤器
		 * 你需要在依赖中依赖额外的相关模块才能启用它
		 */
	}
	
	protected void filterList__DefaultLoginPageGeneratingFilter__默认开启() {
		/**
		 * 生成默认的登录页。默认 /login
		 */
	}
	
	protected void filterList__DefaultLogoutPageGeneratingFilter__默认开启() {
		/**
		 * 生成默认的退出页。 默认 /logout
		 */
	}
	
	protected void filterList__ConcurrentSessionFilter() {
		/**
		 * 
		 * maximumSessions!=null的时候SessionManagementConfigurer启用该ConcurrentSessionFilter
		 * 该过滤器可能会被多次执行
		 * 用来判断session是否过期以及更新最新的访问时间
		 * 
		 * 其流程为：
		 * 		1：session 检测，如果不存在直接放行去执行下一个过滤器。存在则进行下一步
		 * 		2：执行session存在
		 * 			根据sessionid从SessionRegistry中获取SessionInformation
		 * 			从SessionInformation中获取session是否过期
		 * 				没有过期：更新SessionInformation中的访问日期
		 * 				如果过期：
		 * 					则执行doLogout()方法，这个方法会将session无效
		 * 					并将 SecurityContext 中的Authentication中的权限置空
		 * 					同时在SecurityContenxtHoloder中清除SecurityContext
		 * 					然后查看是否有跳转的 expiredUrl，如果有就跳转，没有就输出提示信息
		 */
	}
	
	protected void filterList__DigestAuthenticationFilter() {
		/**
		 * Digest身份验证是 Web 应用程序中流行的可选的身份验证机制
		 * DigestAuthenticationFilter 能够处理 HTTP 头中显示的摘要式身份验证凭据
		 * 可以通过 HttpSecurity#addFilter() 来启用和配置相关功能
		 * 
		 */
	}
	
	protected void filterList__BearerTokenAuthenticationFilter() {
		/**
		 * oauth2相关
		 */
	}
	
	protected void filterList__BasicAuthenticationFilter() {
		/**
		 * 如果不自定义WebSecurityConfigurerAdapter，则会默认启用
		 * 
		 * 和Digest身份验证一样都是Web 应用程序中流行的可选的身份验证机制
		 * BasicAuthenticationFilter 负责处理 HTTP 头中显示的基本身份验证凭据
		 * 这个 Spring Security 的 Spring Boot 自动配置默认是启用的
		 * 
		 * BasicAuthenticationFilter 通过 HttpSecurity#httpBasic() 及相关方法引入其配置对象 HttpBasicConfigurer 来进行配置
		 */
	}
	
	protected void filterList__RequestCacheAwareFilter__默认开启() {
		/**
		 * 用于用户认证成功后，重新恢复因为登录被打断的请求
		 * 当匿名访问一个需要授权的资源时。会跳转到认证处理逻辑，此时请求被缓存
		 * 在认证逻辑处理完毕后，从缓存中获取最开始的资源请求进行再次请求
		 */
	}
	
	protected void filterList__SecurityContextHolderAwareRequestFilter__默认开启() {
		/**
		 * 用来 实现j2ee中 Servlet Api 一些接口方法
		 * 		比如 getRemoteUser 方法、isUserInRole 方法
		 * 在使用 Spring Security 时其实就是通过这个过滤器来实现的
		 * 
		 * SecurityContextHolderAwareRequestFilter 通过 HttpSecurity.servletApi() 及相关方法引入其配置对象 ServletApiConfigurer 来进行配置
		 */
	}
	
	protected void filterList__JaasApiIntegrationFilter() {
		/**
		 * 适用于JAAS （Java 认证授权服务）
		 * 如果 SecurityContextHolder 中拥有的 Authentication 是一个 JaasAuthenticationToken
		 * 那么该 JaasApiIntegrationFilter 将使用包含在 JaasAuthenticationToken 中的 Subject 继续执行 FilterChain
		 */
	}
	
	protected void filterList__RememberMeAuthenticationFilter() {
		/**
		 * 处理 记住我 功能的过滤器
		 * 
		 * RememberMeAuthenticationFilter 通过 HttpSecurity.rememberMe() 及相关方法引入其配置对象 RememberMeConfigurer 来进行配置
		 * 通过cookie来实现remember me功能的Filter
		 */
	}
	
	protected void filterList__AnonymousAuthenticationFilter__默认开启() {
		/**
		 * 匿名认证过滤器
		 * 
		 * 对于 Spring Security 来说，所有对资源的访问都是有 Authentication 的
		 * 对于无需登录（UsernamePasswordAuthenticationFilter ）直接可以访问的资源，会授予其匿名用户身份
		 * AnonymousAuthenticationFilter 通过 HttpSecurity.anonymous() 及相关方法引入其配置对象 AnonymousConfigurer 来进行配置
		 * 
		 * 如何判断是否是匿名用户？
		 * 		如果SecurityContextHolder.getContext().getAuthentication() == null
		 * 			SecurityContextHolder.getContext().setAuthentication(createAuthentication((HttpServletRequest) req));
		 * 			这里的createAuthentication((HttpServletRequest)创建一个AnonymousAuthenticationToken
		 * 			后面根据是否是AnonymousAuthenticationToken来判断是否是匿名用户
		 * 			创建时：new AnonymousAuthenticationToken(this.key, this.principal,this.authorities);
		 * 			可以在这个filter中配置匿名用户的key，principal，authorities
		 * 				UUID：默认在AnonymousConfigurer中生成一个UUID传入进来
		 * 				principal：默认为："anonymousUser"
		 * 				authorities：默认为：SimpleGrantedAuthority，其中的role为"ROLE_ANONYMOUS"
		 */
	}
	
	protected void filterList__OAuth2AuthorizationCodeGrantFilter() {
		/**
		 * oauth2相关
		 */
	}
	
	protected void filterList__SessionManagementFilter__默认开启() {
		/**
		 * Session 管理器过滤器
		 * 内部维护了一个 SessionAuthenticationStrategy 用于管理 Session
		 * SessionManagementFilter 通过 HttpScurity#sessionManagement() 及相关方法引入其配置对象 SessionManagementConfigurer 来进行配置
		 */
	}
	
	protected void filterList__ExceptionTranslationFilter__默认开启() {
		/**
		 * 主要来传输异常事件，还记得之前我们见过的 DefaultAuthenticationEventPublisher 吗？
		 */
	}
	
	protected void filterList__FilterSecurityInterceptor__默认开启() {
		/**
		 * 这个过滤器决定了访问特定路径应该具备的权限
		 * 访问的用户的角色，权限是什么？访问的路径需要什么样的角色和权限？
		 * 这些判断和处理都是由该类进行的
		 * 如果你要实现动态权限控制就必须研究该类
		 */
	}
	
	protected void filterList__AuthorizationFilter() {
		/**
		 * 使用限制访问 URL 的授权过滤器
		 */
	}
	
	protected void filterList__SwitchUserFilter() {
		/**
		 * 用来做账户切换的
		 * 默认的切换账号的url为/login/impersonate
		 * 默认注销切换账号的url为/logout/impersonate
		 * 默认的账号参数为username
		 * 可以通过此类实现自定义的账户切换
		 */
	}
	
}
