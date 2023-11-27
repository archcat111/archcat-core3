package org.cat.support.springboot3.starter.web.security;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.support.security3.generator.spring.SupportSecurity3SpringConditionalFlag;
import org.cat.support.security3.generator.spring.adapter.WebSecurityConfigurerAdapterExt;
import org.cat.support.security3.generator.spring.constants.SecurityLoginConstants;
import org.cat.support.security3.generator.spring.constants.SecurityUserStateConstants;
import org.cat.support.security3.generator.spring.context.IJwtSecurityContextRepository;
import org.cat.support.security3.generator.spring.context.JwtLocalSecurityContextRepository;
import org.cat.support.security3.generator.spring.context.JwtRedisSecurityContextRepository;
import org.cat.support.security3.generator.spring.login.AuthRequestMatcher;
import org.cat.support.security3.generator.spring.login.DynamicParameterAuthDetailsSource;
import org.cat.support.security3.generator.spring.login.password.PasswordAuthProvider;
import org.cat.support.security3.generator.spring.login.password.TestUserService;
import org.cat.support.security3.generator.spring.logout.LogoutRequestMatcher;
import org.cat.support.security3.generator.spring.logout.handler.CookieLogoutHandler;
import org.cat.support.security3.generator.spring.logout.handler.JwtLogoutHandler;
import org.cat.support.security3.generator.spring.logout.handler.SessionLogoutHandler;
import org.cat.support.springboot3.starter.web.WebMvcAutoConfiguration;
import org.cat.support.springboot3.starter.web.security.SecurityCorsProperties.Customize;
import org.cat.support.springboot3.starter.web.security.SecurityUserStateRedisProperties.Client;
import org.cat.support.springboot3.starter.web.security.SecurityUserStateRedisProperties.Cluster;
import org.cat.support.springboot3.starter.web.security.SecurityUserStateRedisProperties.Standalone;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.CookieRequestCache;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author 王云龙
 * @date 2021年12月6日 下午4:32:57
 * @version 1.0
 * @description 
 * 		配置条件：Servlet、Spring Security、Support-Security3
 *
 */
@Configuration
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@ConditionalOnClass(SupportSecurity3SpringConditionalFlag.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "cat.support3.web.security", name = "enabled",  havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(value = {SecurityProperties.class})
public class SecurityAutoServletConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	
	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ISpringRespGenerator springRespGenerator;
	
	@Bean
	public WebSecurityConfigurerAdapterExt webSecurityConfigurerAdapterExt() {
		WebSecurityConfigurerAdapterExt webSecurityConfigurerAdapterExt = new WebSecurityConfigurerAdapterExt();
		
		//securitycontext
		webSecurityConfigurerAdapterExt.setSecurityContextRepository(securityContextRepository());
		
		//cors
		if(this.securityProperties.getCors().isEnabled()) {
			webSecurityConfigurerAdapterExt.setOpenCors(true);
			webSecurityConfigurerAdapterExt.setCorsConfigurationSource(corsConfigurationSource());
		}
		
		//csrf
		if(this.securityProperties.getCsrf().isEnabled()) {
			if(!this.securityProperties.getUserState().getFrame().toLowerCase().equals(SecurityUserStateConstants.Frame.JWT)) {
				webSecurityConfigurerAdapterExt.setOpenCsrf(true);
			}else {
				webSecurityConfigurerAdapterExt.setOpenCsrf(false);
			}
		}else {
			webSecurityConfigurerAdapterExt.setOpenCsrf(false);
		}
		
		//logout
		List<LogoutRequestMatcher> logoutRequestMatchers = buildLogoutRequestMatcherList();
		List<LogoutHandler> logoutHandlers = buildLogoutHandlers();
		webSecurityConfigurerAdapterExt.setLogoutRequestMatchers(logoutRequestMatchers);
		webSecurityConfigurerAdapterExt.setLogoutSpringRespGenerator(this.springRespGenerator);
		webSecurityConfigurerAdapterExt.setLogoutHandlers(logoutHandlers);
		
		//login
		List<AuthRequestMatcher> authRequestMatchers = buildLoginRequestMatcherList();
		DynamicParameterAuthDetailsSource dynamicParameterAuthDetailsSource = buildDynamicParameterAuthDetailsSource();
		Set<AuthenticationProvider> authProviders = buildAuthenticationProviders();
		webSecurityConfigurerAdapterExt.setAuthRequestMatchers(authRequestMatchers);
		webSecurityConfigurerAdapterExt.setAuthDynamicParameterAuthDetailsSource(dynamicParameterAuthDetailsSource);
		webSecurityConfigurerAdapterExt.setAuthProviders(authProviders);
		webSecurityConfigurerAdapterExt.setAuthSpringRespGenerator(this.springRespGenerator);
		
		//requestCache
		boolean openRequestCache = false;
		RequestCache requestCache = buildReqeustCache();
		webSecurityConfigurerAdapterExt.setOpenRequestCache(openRequestCache);
		webSecurityConfigurerAdapterExt.setRequestCache(requestCache);
		
		//jwt or session
		initUserStateSessionOrJwt(webSecurityConfigurerAdapterExt);
		
		return webSecurityConfigurerAdapterExt;
	}

	@Bean
	public SecurityContextRepository securityContextRepository() {
		SecurityUserStateProperties securityUserStateProperties = securityProperties.getUserState();
		String frame = securityUserStateProperties.getFrame();
		
		switch (frame.toLowerCase()) {
			case SecurityUserStateConstants.Frame.SESSION:
				return securityContextSessionRepository();
			case SecurityUserStateConstants.Frame.JWT:
				return securityContextJwtRepository();
			case SecurityUserStateConstants.Frame.NULL:
				return securityContextNullRepository();
			default:
				//TODO 抛出异常
				return null;
		}
	}
	
	private SecurityContextRepository securityContextSessionRepository() {
		HttpSessionSecurityContextRepository httpSecurityRepository = new HttpSessionSecurityContextRepository();
		return httpSecurityRepository;
	}
	
	@ConditionalOnBean(HttpSessionSecurityContextRepository.class)
	@ConditionalOnProperty(prefix = "cat.support3.web.security.userState", name = "storage",  havingValue = "redis", matchIfMissing = false)
	@Bean
	ConfigureRedisAction configureRedisAction() {
		switch (securityProperties.getUserState().getSession().getConfigureAction()) {
		case NOTIFY_KEYSPACE_EVENTS:
			return new ConfigureNotifyKeyspaceEventsAction();
		case NONE:
			return ConfigureRedisAction.NO_OP;
		}
		//TODO 抛出异常
		return null;
	}
	
	@ConditionalOnBean(HttpSessionSecurityContextRepository.class)
	@ConditionalOnProperty(prefix = "cat.support3.web.security.userState", name = "storage",  havingValue = "redis", matchIfMissing = false)
	@Configuration(proxyBeanMethods = false)
	public static class SpringBootRedisHttpSessionConfiguration extends RedisHttpSessionConfiguration {

		@Autowired
		public void customize(SecurityProperties securityProperties) {
			SecurityUserStateSessionProperties securityUserStateSessionProperties = securityProperties.getUserState().getSession();
			
			long durationSeconds = securityUserStateSessionProperties.getDurationTimeMillis()/1000/60;
			setMaxInactiveIntervalInSeconds((int)durationSeconds);
			setRedisNamespace(securityUserStateSessionProperties.getNamespace());
			setFlushMode(securityUserStateSessionProperties.getFlushMode());
			setSaveMode(securityUserStateSessionProperties.getSaveMode());
			setCleanupCron(securityUserStateSessionProperties.getCleanupCron());
		}
		
	}
	
	private IJwtSecurityContextRepository securityContextJwtRepository() {
		SecurityUserStateProperties securityUserStateProperties = securityProperties.getUserState();
		String storage = securityUserStateProperties.getStorage();
		SecurityUserStateJwtProperties securityUserStateJwtProperties = securityUserStateProperties.getJwt();
		long durationSeconds = securityUserStateJwtProperties.getDurationTimeSeconds();
		
		IJwtSecurityContextRepository jwtSecurityContextRepository = null;
		if(SecurityUserStateConstants.Storage.LOCAL.toLowerCase().equals(storage.toLowerCase())) {//本地存储
			jwtSecurityContextRepository = new JwtLocalSecurityContextRepository(durationSeconds);
			//TODO 配置参数：headerParamterName、trustResolver
		}else if(SecurityUserStateConstants.Storage.REDIS.toLowerCase().equals(storage.toLowerCase())) {//Redis存储
			RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
			redisTemplate.setConnectionFactory(redisConnectionFactory());
			jwtSecurityContextRepository = new JwtRedisSecurityContextRepository(redisTemplate, durationSeconds);
			//TODO 配置参数：headerParamterName、trustResolver
		}else {
			//TODO 抛出异常
		}
		return jwtSecurityContextRepository;
	}
	
	private SecurityContextRepository securityContextNullRepository() {
		SecurityContextRepository securityContextRepository = new NullSecurityContextRepository();
		return securityContextRepository;
	}
	
	@ConditionalOnProperty(prefix = "cat.support3.web.security.userState", name = "storage",  havingValue = "redis", matchIfMissing = false)
	@Bean
	@SpringSessionRedisConnectionFactory
	public RedisConnectionFactory redisConnectionFactory(){	
		SecurityUserStateRedisProperties storageRedis = this.securityProperties.getUserState().getRedis();
		//如果配置了redisConnectionFactoryName则从已存在的RedisConnectionFactory管理器中获取
		String redisConnectionFactoryName = storageRedis.getRedisConnectionFactoryName();
		if(StringUtils.isNotBlank(redisConnectionFactoryName)) {
			if(this.applicationContext.containsBean(redisConnectionFactoryName)) {
				RedisConnectionFactory sedisConnectionFactory = applicationContext.getBean(redisConnectionFactoryName, RedisConnectionFactory.class);
				return sedisConnectionFactory;
			}
		}
		
		Client client = storageRedis.getClient();
		JedisClientConfiguration jedisClientConfiguration = buildJedisClientConfiguration(client);
		
		String infra = storageRedis.getInfra();
		if(SecurityUserStateConstants.RedisInfra.STANDALONE.toLowerCase().equals(infra.toLowerCase())) {
			Standalone standalone = storageRedis.getStandalone();
			RedisStandaloneConfiguration redisStandaloneConfiguration = buildRedisStandaloneConfiguration(standalone);
			return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
		}else if(SecurityUserStateConstants.RedisInfra.CLUSTER.toLowerCase().equals(infra.toLowerCase())) {
			Cluster cluster = storageRedis.getCluster();
			RedisClusterConfiguration redisClusterConfiguration = buildRedisClusterConfiguration(cluster);
			return new JedisConnectionFactory(redisClusterConfiguration, jedisClientConfiguration);
		}else {
			//TODO 抛出异常
			return null;
		}
	}
	
	private RedisStandaloneConfiguration buildRedisStandaloneConfiguration(Standalone standalone) {
		String hostName = standalone.getHostName();
		Integer port = standalone.getPort();
		Integer dbIndex = standalone.getDbIndex();
		String username = standalone.getUserName();
		String password = standalone.getPassword();
		
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(hostName);
		redisStandaloneConfiguration.setPort(port);
		redisStandaloneConfiguration.setDatabase(dbIndex);
		redisStandaloneConfiguration.setUsername(username);
		redisStandaloneConfiguration.setPassword(password);
		
		return redisStandaloneConfiguration;
	}
	
	private RedisClusterConfiguration buildRedisClusterConfiguration(Cluster cluster) {
		List<String> clusterNodes = cluster.getClusterNodes();
		Integer maxRedirects = cluster.getMaxRedirects();
		String username = cluster.getUserName();
		String password = cluster.getPassword();
		
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
		redisClusterConfiguration.setMaxRedirects(maxRedirects);
		redisClusterConfiguration.setUsername(username);
		redisClusterConfiguration.setPassword(password);
		
		return redisClusterConfiguration;
	}
	
	private JedisClientConfiguration buildJedisClientConfiguration(Client client) {
		JedisPoolConfig jedisPoolConfig = buildJedisPoolConfig(client);
		
		JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
				.clientName(client.getClientName())
				.connectTimeout(Duration.ofSeconds(client.getConnectTimeoutSeconds())) //默认值2000
				.readTimeout(Duration.ofSeconds(client.getReadTimeoutSeconds())) //默认值2000
				.usePooling().poolConfig(jedisPoolConfig)
//				.and()
//				.useSsl().sslSocketFactory(null)
				.build();
		
		return jedisClientConfiguration;
	}
	
	private JedisPoolConfig buildJedisPoolConfig(Client client) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setBlockWhenExhausted(client.isBlockWhenExhausted());
		jedisPoolConfig.setEvictionPolicyClassName(client.getEvictionPolicyClassName());
		jedisPoolConfig.setEvictorShutdownTimeoutMillis(Duration.ofMillis(client.getEvictorShutdownTimeoutMillis()));
		jedisPoolConfig.setFairness(client.isFairness());
		jedisPoolConfig.setJmxEnabled(client.isJmxEnabled());
		jedisPoolConfig.setJmxNameBase(client.getJmxNameBase());
		jedisPoolConfig.setJmxNamePrefix(client.getJmxNamePrefix());
		jedisPoolConfig.setLifo(client.isLifo());
		jedisPoolConfig.setMaxIdle(client.getMaxIdle());
		jedisPoolConfig.setMaxTotal(client.getMaxTotal());
		jedisPoolConfig.setMaxWaitMillis(client.getMaxWaitMillis());
		jedisPoolConfig.setMinEvictableIdleTime(Duration.ofMillis(client.getMinEvictableIdleTimeMillis()));
		jedisPoolConfig.setMinIdle(client.getMinIdle());
		jedisPoolConfig.setNumTestsPerEvictionRun(client.getNumTestsPerEvictionRun());
		jedisPoolConfig.setSoftMinEvictableIdleTime(Duration.ofMillis(client.getSoftMinEvictableIdleTimeMillis()));
		jedisPoolConfig.setTestOnBorrow(client.isTestOnBorrow());
		jedisPoolConfig.setTestOnCreate(client.isTestOnCreate());
		jedisPoolConfig.setTestOnReturn(client.isTestOnReturn());
		jedisPoolConfig.setTestWhileIdle(client.isTestWhileIdle());
		jedisPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(client.getTimeBetweenEvictionRunsMillis()));
		
		return jedisPoolConfig;
	}
	
	@ConditionalOnProperty(prefix = "cat.support3.web.security.cors", name = "enabled",  havingValue = "true", matchIfMissing = false)
	@Bean
	 public CorsConfigurationSource corsConfigurationSource() {
		
		SecurityCorsProperties securityCorsProperties = this.securityProperties.getCors();
		if(securityCorsProperties.isAllowAll()) {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
			configuration.setAllowedMethods(Collections.unmodifiableList(
					Arrays.asList(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name())
				)
			);
			configuration.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
			configuration.setExposedHeaders(null);
			configuration.setMaxAge(1800L);
			configuration.setAllowCredentials(false);
			
			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", configuration);
			
			return source;
		}
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		Map<String, Customize> customizes = securityCorsProperties.getCustomizes();
		customizes.forEach((url, customize) -> {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedOrigins(Arrays.asList(StringUtils.split(customize.getAllowedOrigins(), ",")));
			configuration.setAllowedMethods(Arrays.asList(StringUtils.split(customize.getAllowedMethods(), ",")));
			configuration.setAllowedHeaders(Arrays.asList(StringUtils.split(customize.getAllowedHeaders(), ",")));
			configuration.setExposedHeaders(Arrays.asList(StringUtils.split(customize.getExposedHeaders(), ",")));
			configuration.setMaxAge(customize.getMaxAge());
			configuration.setAllowCredentials(customize.isAllowCredentials());
			
			source.registerCorsConfiguration(url, configuration);
		});
		
		return source;
	}
	
	private List<LogoutRequestMatcher> buildLogoutRequestMatcherList() {
		Map<String, SecurityLogoutProperties> logoutMap = this.securityProperties.getLogout();
		
		List<LogoutRequestMatcher> logoutRequestMatcherList = new ArrayList<>();
		logoutMap.forEach((logoutName, securityLogoutProperties) -> {
			if(!securityLogoutProperties.isEnabled()) {
				return;
			}
			
			String logoutUrl = securityLogoutProperties.getUrl();
			String logoutMethod = securityLogoutProperties.getHttpMethod();
			LogoutRequestMatcher logoutRequestMatcher = new LogoutRequestMatcher(logoutUrl, logoutMethod);
			
			String logoutRequestWay = securityLogoutProperties.getRequestWay();
			String otherParameters = securityLogoutProperties.getOtherParameters();
			logoutRequestMatcher.setRequestWay(logoutRequestWay);
			logoutRequestMatcher.setOtherParameters(Arrays.asList(StringUtils.split(otherParameters, ",")));
			
			String defaultTargetUrl = securityLogoutProperties.getSuccess().getDefaultTargetUrl();
			boolean alwaysUseDefaultTargetUrl = securityLogoutProperties.getSuccess().isAlwaysUseDefaultTargetUrl();
			String targetUrlParameter = securityLogoutProperties.getSuccess().getTargetUrlParameter();
			boolean useReferer = securityLogoutProperties.getSuccess().isUseReferer();
			Map<String, String> roleTargetUrl = securityLogoutProperties.getSuccess().getRoleTargetUrl();
			logoutRequestMatcher.getSuccess().setDefaultTargetUrl(defaultTargetUrl);
			logoutRequestMatcher.getSuccess().setAlwaysUseDefaultTargetUrl(alwaysUseDefaultTargetUrl);
			logoutRequestMatcher.getSuccess().setTargetUrlParameter(targetUrlParameter);
			logoutRequestMatcher.getSuccess().setUseReferer(useReferer);
			logoutRequestMatcher.getSuccess().setRoleTargetUrl(roleTargetUrl);
			
			logoutRequestMatcherList.add(logoutRequestMatcher);
		});
		
		return logoutRequestMatcherList;
	}
		
	private List<LogoutHandler> buildLogoutHandlers() {
		List<LogoutHandler> logoutHandlers = Lists.newArrayList();
		
		SecurityUserStateProperties securityUserStateProperties = this.securityProperties.getUserState();
		String frame = securityUserStateProperties.getFrame();
		
		switch (frame.toLowerCase()) {
			case SecurityUserStateConstants.Frame.SESSION:
				logoutHandlers.add(new SessionLogoutHandler());
			case SecurityUserStateConstants.Frame.JWT:
				logoutHandlers.add(new JwtLogoutHandler());
				//TODO headerParamterName、securityContextRepository
			case SecurityUserStateConstants.Frame.NULL:
				break;
			default:
				break;
		}
		
		SecurityUserStateCookieProperties securityUserStateCookieProperties = securityUserStateProperties.getCookie();
		String cookiesToClear = securityUserStateCookieProperties.getCookiesToClear();
		if(StringUtils.isNotBlank(cookiesToClear)) {
			CookieLogoutHandler cookieLogoutHandler = new CookieLogoutHandler(StringUtils.split(cookiesToClear, ","));
			logoutHandlers.add(cookieLogoutHandler);
		}
		return logoutHandlers;
	}
	
	private RequestCache buildReqeustCache() {
		SecurityUserStateProperties securityUserStateProperties = this.securityProperties.getUserState();
		String frame = securityUserStateProperties.getFrame();
		
		switch (frame.toLowerCase()) {
			case SecurityUserStateConstants.Frame.SESSION:
				new HttpSessionRequestCache();
			case SecurityUserStateConstants.Frame.JWT:
				new CookieRequestCache();
			case SecurityUserStateConstants.Frame.NULL:
				new NullRequestCache();
			default:
				//TODO 抛出异常
				return null;
		}
	}
	
	private List<AuthRequestMatcher> buildLoginRequestMatcherList() {
		Map<String, SecurityLoginProperties> loginMap = this.securityProperties.getLogin();
		
		List<AuthRequestMatcher> authRequestMatcherList = new ArrayList<>();
		loginMap.forEach((loginName, securityLoginProperties) -> {
			if(!securityLoginProperties.isEnabled()) {
				return;
			}
			
			String loginUrl = securityLoginProperties.getUrl();
			String loginMethod = securityLoginProperties.getHttpMethod();
			AuthRequestMatcher authRequestMatcher = new AuthRequestMatcher(loginUrl, loginMethod);
			
			String loginWay = securityLoginProperties.getWay(); //userNameAndPassword、sms、...
			String loginRequestWay = securityLoginProperties.getRequestWay(); //form、ajax、api
			String usernameParameter = securityLoginProperties.getUsernameParameter();
			String passwordParameter = securityLoginProperties.getPasswordParameter();
			String passwordEncryption = securityLoginProperties.getPasswordEncryption();
			String otherParameters = securityLoginProperties.getOtherParameters();
			boolean hideUserNotFoundExceptions = securityLoginProperties.isHideUserNotFoundExceptions();
			authRequestMatcher.setWay(loginWay);
			authRequestMatcher.setRequestWay(loginRequestWay);
			authRequestMatcher.setUsernameParameter(usernameParameter);
			authRequestMatcher.setPasswordParameter(passwordParameter);
			authRequestMatcher.setPasswordEncryption(passwordEncryption);
			authRequestMatcher.setOtherParameters(Arrays.asList(StringUtils.split(otherParameters, ",")));
			authRequestMatcher.setHideUserNotFoundExceptions(hideUserNotFoundExceptions);//TODO 需要扩展passwordProvider
			
			String successDefaultTargetUrl = securityLoginProperties.getSuccess().getDefaultTargetUrl();
			boolean alwaysUseDefaultTargetUrl = securityLoginProperties.getSuccess().isAlwaysUseDefaultTargetUrl();
			String targetUrlParameter = securityLoginProperties.getSuccess().getTargetUrlParameter();
			boolean useReferer = securityLoginProperties.getSuccess().isUseReferer();
			Map<String, String> roleTargetUrl = securityLoginProperties.getSuccess().getRoleTargetUrl();
			authRequestMatcher.getSuccess().setDefaultTargetUrl(successDefaultTargetUrl);
			authRequestMatcher.getSuccess().setAlwaysUseDefaultTargetUrl(alwaysUseDefaultTargetUrl);
			authRequestMatcher.getSuccess().setTargetUrlParameter(targetUrlParameter);
			authRequestMatcher.getSuccess().setUseReferer(useReferer);
			authRequestMatcher.getSuccess().setRoleTargetUrl(roleTargetUrl);
			
			String failDefaultTargetUrl = securityLoginProperties.getFail().getDefaultTargetUrl();
			authRequestMatcher.getFail().setDefaultTargetUrl(failDefaultTargetUrl);
			
			authRequestMatcherList.add(authRequestMatcher);
		});
		
		return authRequestMatcherList;
	}
	
	private DynamicParameterAuthDetailsSource buildDynamicParameterAuthDetailsSource() {
		DynamicParameterAuthDetailsSource dynamicParameterAuthDetailsSource = new DynamicParameterAuthDetailsSource();
		return dynamicParameterAuthDetailsSource;
	}
	
	private Set<AuthenticationProvider> buildAuthenticationProviders(){
		Set<AuthenticationProvider> authenticationProviders = Collections.emptySet();
		
		Set<String> loginWaySet = Collections.emptySet();
		this.securityProperties.getLogin().forEach((loginName, securityLoginProperties) -> {
			String loginWay = securityLoginProperties.getWay();
			loginWaySet.add(loginWay);
		});
		
		for (String loginWay : loginWaySet) {
			switch (loginWay) {
				case SecurityLoginConstants.Way.USERNAME_AND_PASSWORD:
					authenticationProviders.add(passwordAuthProvider());
					break;
				case SecurityLoginConstants.Way.SMS:
					//TODO 当前不支持的异常
					break;
				default:
					//TODO 抛出异常
					break;
			}
		}
		return authenticationProviders;
	}
	
	//ProviderManager中的providers列表中的其中一个
	public PasswordAuthProvider passwordAuthProvider() {
		PasswordAuthProvider passwordAuthProvider = new PasswordAuthProvider();
		passwordAuthProvider.setHideUserNotFoundExceptions(false);
		passwordAuthProvider.setPasswordEncoder(passwordEncoder());
		passwordAuthProvider.setUserDetailsService(userService());
		//userDetailsPasswordService：默认为null，当该参数!=null并且passwordEncoder.upgradeEncoding(user.getPassword())!=null，则会更新密码
		//userCache：默认为NullUserCache，用户缓存
		//forcePrincipalAsString：默认为false，返回的AuthenticationToken中的principal为UserDetails，如果为true，则为user.getUsername()
		//hideUserNotFoundExceptions：默认为true，当发生UsernameNotFoundException异常时抛出BadCredentialsException，为false时返回真实的UsernameNotFoundException
		//preAuthenticationChecks：默认为DefaultPreAuthenticationChecks，检查从DB返回的UserDetails的isAccountNonLocked、isEnabled、isAccountNonExpired
		//postAuthenticationChecks：默认为DefaultPostAuthenticationChecks，检查从DB返回的UserDetails的isCredentialsNonExpired
		//authoritiesMapper：默认为NullAuthoritiesMapper，将this.authoritiesMapper.mapAuthorities(从DB返回的UserDetails的getAuthorities())的结果set到AuthenticationToken中的Collection<GrantedAuthority> authorities
		return passwordAuthProvider;
	}
	
	@Bean
	@ConditionalOnMissingBean(UserDetailsService.class)
	public UserDetailsService userService() {
		return new TestUserService();
	}
	
	private PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	private void initUserStateSessionOrJwt(WebSecurityConfigurerAdapterExt webSecurityConfigurerAdapterExt) {
		SecurityUserStateProperties securityUserStateProperties = securityProperties.getUserState();
		String frame = securityUserStateProperties.getFrame();
		
		switch (frame.toLowerCase()) {
			case SecurityUserStateConstants.Frame.SESSION:
				initUserStateSession(webSecurityConfigurerAdapterExt);
				return;
			case SecurityUserStateConstants.Frame.JWT:
				initUserStateJwt(webSecurityConfigurerAdapterExt);
				return;
			case SecurityUserStateConstants.Frame.NULL:
				initUserStateNull(webSecurityConfigurerAdapterExt);
				return;
			default:
				//TODO 抛出异常
				return;
		}
	}
	
	private void initUserStateSession(WebSecurityConfigurerAdapterExt webSecurityConfigurerAdapterExt) {
		webSecurityConfigurerAdapterExt.setOpenSession(true);
		webSecurityConfigurerAdapterExt.setOpenJwt(false);
	}
	
	private void initUserStateJwt(WebSecurityConfigurerAdapterExt webSecurityConfigurerAdapterExt) {
		SecurityUserStateJwtProperties securityUserStateJwtProperties = this.securityProperties.getUserState().getJwt();
		String jwtVerifyRedirectUrl = securityUserStateJwtProperties.getJwtVerifyRedirectUrl();
		String jwtExpiredRedirectUrl = securityUserStateJwtProperties.getJwtExpiredRedirectUrl();
		String issuer = securityUserStateJwtProperties.getIssuer();
		String subject = securityUserStateJwtProperties.getSubject();
		long durationTimeSeconds = securityUserStateJwtProperties.getDurationTimeSeconds();
		long refreshWindowSeconds = securityUserStateJwtProperties.getRefreshWindowSeconds();
		String headerParamterName = securityUserStateJwtProperties.getHeaderParamterName();
		
		webSecurityConfigurerAdapterExt.setOpenSession(false);
		webSecurityConfigurerAdapterExt.setOpenJwt(true);
		webSecurityConfigurerAdapterExt.setJwtSpringRespGenerator(this.springRespGenerator);
		webSecurityConfigurerAdapterExt.setJwtVerifyRedirectUrl(jwtVerifyRedirectUrl);
		webSecurityConfigurerAdapterExt.setJwtExpiredRedirectUrl(jwtExpiredRedirectUrl);
		webSecurityConfigurerAdapterExt.setIssuer(issuer);
		webSecurityConfigurerAdapterExt.setSubject(subject);
		webSecurityConfigurerAdapterExt.setDurationTimeSeconds(durationTimeSeconds);
		webSecurityConfigurerAdapterExt.setRefreshWindowSeconds(refreshWindowSeconds);
		webSecurityConfigurerAdapterExt.setHeaderParamterName(headerParamterName);
		
	}
	
	private void initUserStateNull(WebSecurityConfigurerAdapterExt webSecurityConfigurerAdapterExt) {
		webSecurityConfigurerAdapterExt.setOpenSession(false);
		webSecurityConfigurerAdapterExt.setOpenJwt(false);
	}
}
