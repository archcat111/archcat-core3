package org.cat.support.springboot3.actuator.study;

public class AutoConfig {
	
	public void Properties() {
		//复制下面XxxProperties的名称，在本Class中搜索即可找到使用说明
		/**
		 * ManagementServerProperties
		 * WebEndpointProperties
		 * CorsEndpointProperties.class
		 * HealthEndpointProperties extends HealthProperties
		 * 
		 * @ConditionalOnAvailableEndpoint(endpoint = XxxEndpoint.class)
		 * 		management.endpoint.<endpoint_id>.enabled
		 * 		management.endpoints.enabled-by-default
		 * 
		 * CachingOperationInvokerAdvisor：
		 * 		management.endpoint.<endpoint_id>.cache.time-to-live
		 * 
		 * PathMapper：
		 * 		management.endpoints.path-mapping.<endpoint_id>=<path>
		 * 
		 * @ConditionalOnEnabledHealthIndicator("ping2")：
		 * 		management.health.defaults.enabled
		 * 		默认为true
		 */
	}
	
	public void ManagementContextAutoConfiguration() {
		/**
		 * 在spring-boot-actuator-autoconfigure-2.5.6.jar
		 * actuator自动化配置的核心启动类，用于配置actuator的地址、port等
		 * 如果management.server.port和server.port相同初始化：SameManagementContextConfiguration
		 * 否则初始化：DifferentManagementContextConfiguration
		 * 
		 * 配置文件：《《ManagementServerProperties》》
		 * 
		 * 可以参考该jar中的spring.factories
		 * management.server:
		 * 		address: #如果端口相同，则不能设置该参数
		 * 		port:
		 * 		basePath:
		 * 		servlet:
		 * 			contextPath:
		 * 		ssl:
		 * 			keyStore:
		 * 			...:
		 */
	}
	
	public void 注解OnAvailableEndpointCondition(){
		/**
		 * 例如：
		 * @Configuration(proxyBeanMethods = false)
		 * @ConditionalOnAvailableEndpoint(endpoint = ShutdownEndpoint.class)
		 * public class ShutdownEndpointAutoConfiguration {
		 * 		@Bean(destroyMethod = "")
		 * 		@ConditionalOnMissingBean
		 * 		public ShutdownEndpoint shutdownEndpoint() {
		 * 			return new ShutdownEndpoint();
		 * 		}
		 * }
		 * 该注解表示只有该参数中的XxxEndpoint是可以用状态才会判定条件满足
		 * 1：首先会判断management.endpoint.<endpoint_id>.enabled是否等于true，默认值为null
		 * 2：然后会判断management.endpoints.enabled-by-default是否为true，默认值为null
		 * 		该参数会在判断第一个XxxEndpoint的时候加载到缓存
		 * 3：接着获取对应的这个XxxEndpoint的注解例如@Endpoint(id = "auditevents")标注的enableByDefault的值
		 * 		enableByDefault默认为true
		 * 4：如果上面1到3通过了，最后通过ExposureFilter中的WebEndpointProperties相关配置判断是否可以通过
		 * 		先判断EndpointExposure是否在JMX、WEB、CLOUD_FOUNDRY(初始化ExposureFilter的时候指定，一般没问题，除非自定义)
		 * 		再判断management.endpoints.<EndpointExposure>.exposure下的include和exclude配置
		 * 因此：一个定义个类似@Endpoint(id = "auditevents")这种注解的Endpoint都会加载
		 * 
		 */
	}
	
	public void 注解EndpointExtension() {
		/**
		 * @EndpointExtension是参数中标注的Endpoint的增强
		 * 举例：
		 * @Endpoint(id = "health")
		 * public class HealthEndpoint extends HealthEndpointSupport<HealthContributor, HealthComponent> {
		 * 		...
		 * }
		 * 
		 * @EndpointWebExtension(endpoint = HealthEndpoint.class)
		 * public class HealthEndpointWebExtension extends HealthEndpointSupport<HealthContributor, HealthComponent> {
		 * 		...
		 * }
		 * 这里的HealthEndpointWebExtension就是对HealthEndpoint的扩展增强
		 */
	}
	
	public void WebMvcEndpointManagementContextConfiguration() {
		//向容器注入自定义的 HandlerMapping，供 DispatcherServlet 调用
		//DispatcherServlet 根据各个 HandlerMapping 做实际的请求分发
		/**
		 * CorsEndpointProperties：
		 * 		management.endpoints.web.cors...
		 * 		用于解决跨域请求相关的配置
		 */
		/**
		 * WebMvcEndpointHandlerMapping：
		 * 		向容器注入 WebMvcEndpointHandlerMapping，由 DispatcherServlet 调用，
		 * 			转发请求到真实的 endpoint 中的 特定 operation 中
		 * 		1：获取所有的 web 类型 endpoints（@Endpoint、@WebEndpoint 注解）
		 * 			这里可能会触发 endpoints 的初始化，但是应该是被 5 给抢先了
		 * 		2：获取所有 servlet 类型 endpoints（@ServletEndpoint 注解）
		 * 		3：获取所有 controller 类型 endpoints（@ControllerEndpoint 注解）
		 * 		allEndpoints：
		 * 			archaius、nacosconfig、nacosdiscovery、ping2、beans、caches、health、info、
		 * 			conditions、configprops、env、loggers、heapdump、threaddump、metrics、
		 * 			quartz、scheduledtasks、mappings、refresh、features、serviceregistry
		 * 		4：web endpoint 的 base path，webEndpointProperties.getBasePath();
		 * 		5：当 bathPath 为空，且 endpoint 的端口和server 端口一样，才不暴露。是否暴露 /actuator 发现页面
		 * 
		 * ControllerEndpointHandlerMapping：
		 * 		向容器注入 ControllerEndpointHandlerMapping，由 DispatcherServlet 调用，转发请求到真实的 endpoint
		 */
	}
	
	public void WebEndpointAutoConfiguration() {
		//开始这个之前，会先开始EndpointAutoConfiguration，EndpointAutoConfiguration不知道是干嘛的
		
		//《《WebEndpointProperties》》
		/**
		 * management.endpoints.web
		 * 		basePath: /actuator	#默认值：/actuator
		 * 		discovery:
		 * 			enabled: true		#默认值：true		#是否启用发现页面
		 * 		pathMapping:
		 * 			- <endpintId>: <应该公开它们的路径>
		 * 		exposure:
		 * 			include:  	#应包含的端点 ID 或全部为“*”，或者：类似：mapping,metric
		 * 			exclude:		#应排除的端点 ID 或全部为“*”
		 */		
		//定义Endpoint需要被暴露的Endpoint的Id
		//如果设置了enabled-by-default为false，并且设置了exposure，取设置了<endpoint>.enabled=true的Endpoint和这里的Endpoint的子集
		//如果设置了enabled-by-default为false，并且设置了<endpoint>.enabled=true，并且没有设置exposure，则根据<endpoint>.enabled=true的Endpoint加载
		//当设置“*”的时候会暴露所有Endpoint
		//有些Endpoint默认不能使用Web访问，如果将该Endpoint设置到这里，则其也可使用Web访问了，例如httptrace
		/**
		 * IncludeExcludeEndpointFilter：
		 * 		ExposureFilter extends IncludeExcludeEndpointFilter<ExposableEndpoint<?>>
		 * 		该过滤器会加载上述配置中的exposure
		 * 		注解OnAvailableEndpointCondition会使用该过滤器来判断该Endpoint能否加载
		 * 		有：
		 * 			IncludeExcludeEndpointFilter<ExposableWebEndpoint>
		 * 			IncludeExcludeEndpointFilter<ExposableControllerEndpoint>
		 */
		
		//PathMapper
		/**
		 * health,info 等路径的重定义，如：
		 * management.endpoints.path-mapping.health=healthcheck
		 * management.endpoints.path-mapping.info=myInfo
		 */
		
		//WebEndpointDiscoverer、ControllerEndpointDiscoverer、ServletEndpointDiscoverer
		/**
		 * 创建 discoverer 类
		 */
		
		//PathMappedEndpoints
		/**
		 * 通过 basepath 和 所有的  endpointSuppliers，得到 所有 endpoint 对象 和其对应的 operation
		 */
	}
	
	public void EndpointAutoConfiguration() {
		//ParameterValueMapper
		/**
		 * 获取容器中的 @EndpointConverter(Converter，GenericConverter)，用于 @Endpoint 输入参数的类型转换
		 * 如果没有，则使用默认的 ApplicationConversionService
		 * 如果有，则使用 它们，来设置 ApplicationConversionService
		 */
		//CachingOperationInvokerAdvisor
		/**
		 * 返回可缓存的 endpoint 的缓存时间
		 * management.endpoint.<endpoint_id>.cache.time-to-live=xx 来配置 endpointName 的缓存时间
		 */
	}
	
	public void 请求生效流程doDispatch() {
		//DispatcherServlet
		/**
		 * protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		 * 		HttpServletRequest processedRequest = request;
		 * 		HandlerExecutionChain mappedHandler = null;
		 * 		boolean multipartRequestParsed = false;
		 * 		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
		 * 		try {
		 * 			...
		 * 			// 为当前请求确定处理程序
		 * 			mappedHandler = getHandler(processedRequest);
		 * 			...
		 * 			// 为当前请求确定处理程序适配器
		 * 			HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
		 * 			String method = request.getMethod();
		 * 			...
		 * 			//可以自定义配置预处理方法，如鉴权等
		 * 			if (!mappedHandler.applyPreHandle(processedRequest, response)) {
		 * 				return;
		 * 			}
		 * 			// Actually invoke the handler.
		 * 			mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
		 * 			...
		 * 			applyDefaultViewName(processedRequest, mv);
		 * 			//调用后置处理器，如加密等
		 * 			mappedHandler.applyPostHandle(processedRequest, response, mv);
		 * 			...
		 * 		}
		 * }
		 */
	}
	public void 请求生效流程getHandler() {
		/**
		 * //为当前请求确定处理程序
		 * protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		 * 		//其中关键的mapping： WebMvcEndpointHandlerMapping、ControllerEndpointHandlerMapping、RequestMappingHandlerMapping
		 * 		if (this.handlerMappings != null) {
		 * 			for (HandlerMapping mapping : this.handlerMappings) {
		 * 				//获取request对应的handler，并处理为 chain
		 * 				HandlerExecutionChain handler = mapping.getHandler(request);
		 * 				if (handler != null) {
		 * 					return handler;
		 * 				}
		 * 			}
		 * 		}
		 * }
		 */
	}
	public void 请求生效流程getHandlerAdapter() {
		/**
		 * // 为当前请求确定处理程序适配器
		 * protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		 * 		//默认提供多个 handlerAdapter
		 * 		if (this.handlerAdapters != null) {
		 * 			for (HandlerAdapter adapter : this.handlerAdapters) {
		 * 				if (adapter.supports(handler)) {
		 * 					//返回第一个可以匹配的 adapter 
		 * 					//这里返回的是：RequestMappingHandlerAdapter
		 * 					return adapter;
		 * 				}
		 * 			}
		 * 		}
		 * 		throw new ServletException("No adapter for handler [" + handler +
		 * 			"]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
		 * }
		 */
	}
	public void 请求生效流程getHandlerAdapter的handle() {
		/**
		 * ha.handle(processedRequest, response, mappedHandler.getHandler());
		 * 这里的实现为：AbstractWebMvcEndpointHandlerMapping.OperationHandler（这是一个内部类）
		 * private static final class OperationHandler {
		 * 		...
		 * 		@ResponseBody
		 * 		Object handle(HttpServletRequest request, @RequestBody(required = false) Map<String, String> body) {
		 * 			//最终走到这
		 * 			return this.operation.handle(request, body);
		 * 		}
		 * }
		 * 
		 * 然后调用到：AbstractWebMvcEndpointHandlerMapping.ServletWebOperationAdapter（这是另一个内部类）
		 * public Object handle(HttpServletRequest request, @RequestBody(required = false) Map<String, String> body) {
		 * 		...
		 * 		//通过 this.operation.invoke 最终调用实际方法
		 * 		//如果容器中注入了 CachingOperationInvokerAdvisor，则 invoke 方法会被 CachingOperationInvoker 拦截，做 cache 方面的操作
		 * 		return handleResult(this.operation.invoke(invocationContext), HttpMethod.resolve(request.getMethod()));
		 * 		...
		 * }
		 */
	}
	public void 请求生效流程CachingOperationInvoker() {
		/**
		 * //每一个 operation 会对应创建一个 CachingOperationInvoker，具体操作在 DiscoveredOperationsFactory 中
		 * //通过 CachingOperationInvokerAdvisor 对 operation 做的静态代理
		 * public class CachingOperationInvoker implements OperationInvoker {
		 * 		...
		 * 		@Override
		 * 		public Object invoke(InvocationContext context) {
		 * 			//如果请求中有 body 内容或 parameter 或 配置了安全相关的准则，则不走缓存
		 * 			if (hasInput(context)) {
		 * 				return this.invoker.invoke(context);
		 * 			}
		 * 			long accessTime = System.currentTimeMillis();
		 * 			...
		 * 			CachedResponse cached = this.cachedResponses.get(cacheKey);
		 * 			if (cached == null || cached.isStale(accessTime, this.timeToLive)) {
		 * 				Object response = this.invoker.invoke(context);
		 * 				//将结果缓存下来
		 * 				cached = createCachedResponse(response, accessTime);
		 * 				this.cachedResponses.put(cacheKey, cached);
		 * 			}
		 * 			return cached.getResponse();
		 * 		}
		 * 		...
		 * }
		 */
	}
	public void 请求生效流程PathMappedEndpoints() {
		/**
		 * 在WebEndpointAutoConfiguration()中初始化：
		 * @Bean
		 * @ConditionalOnMissingBean
		 * public PathMappedEndpoints pathMappedEndpoints(Collection<EndpointsSupplier<?>> endpointSuppliers) {
		 * 		//通过basepath和所有的endpointSuppliers，得到对象
		 * 		return new PathMappedEndpoints(this.properties.getBasePath(), endpointSuppliers);
		 * }
		 * 
		 * public PathMappedEndpoints(String basePath, Collection<EndpointsSupplier<?>> suppliers) {
		 * 		Assert.notNull(suppliers, "Suppliers must not be null");
		 * 		this.basePath = (basePath != null) ? basePath : "";
		 * 		this.endpoints = getEndpoints(suppliers);
		 * }
		 * 
		 * //获取所有类型的 endpoint
		 * private Map<EndpointId, PathMappedEndpoint> getEndpoints(Collection<EndpointsSupplier<?>> suppliers) {
		 * 		Map<EndpointId, PathMappedEndpoint> endpoints = new LinkedHashMap<>();
		 * 		suppliers.forEach((supplier) -> supplier.getEndpoints().forEach((endpoint) -> {
		 * 			if (endpoint instanceof PathMappedEndpoint) {
		 * 				endpoints.put(endpoint.getEndpointId(), (PathMappedEndpoint) endpoint);
		 * 			}
		 * 		}
		 * }
		 */
	}
	public void 请求生效流程EndpointDiscoverer() {
		/**
		 * //supplier.getEndpoints()，得到一个 supplier 所有的 endpoints
		 * public final Collection<E> getEndpoints() {
		 * 		if (this.endpoints == null) {
		 * 			this.endpoints = discoverEndpoints();
		 * 		}
		 * 		return this.endpoints;
		 * }
		 * private Collection<E> discoverEndpoints() {
		 * 		//得到所有的 endpoint
		 * 		Collection<EndpointBean> endpointBeans = createEndpointBeans();
		 * 		//将 extension 加入所有 extension id 对应的 endpoint id 的 endpointBean（filter 必须匹配，否则不填充）
		 * 		//目前有：healthEndpointWebExtension、environmentEndpointWebExtension
		 * 		addExtensionBeans(endpointBeans);
		 * 		//最终处理 endpoint
		 * 		return convertToEndpoints(endpointBeans);
		 * }
		 */
	}
	
	public void 请求生效流程EndpointDiscoverer的discoverEndpoints() {
		//createEndpointBeans()
		/**
		 * private Collection<EndpointBean> createEndpointBeans() {
		 * 		Map<EndpointId, EndpointBean> byId = new LinkedHashMap<>();
		 * 		//得到所有 @Endpoint 及其子注解的类名
		 * 		String[] beanNames = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors(this.applicationContext,Endpoint.class);
		 * 		for (String beanName : beanNames) {
		 * 			//不是被代理的类(!beanName.startsWith(scopedTarget.))，才能进
		 * 			if (!ScopedProxyUtils.isScopedTarget(beanName)) {
		 * 				EndpointBean endpointBean = createEndpointBean(beanName);
		 * 				EndpointBean previous = byId.putIfAbsent(endpointBean.getId(), endpointBean);
		 * 				...
		 * 			}
		 * 		}
		 * 		return byId.values();
		 * }
		 * 
		 * private EndpointBean createEndpointBean(String beanName) {
		 * 		Class<?> beanType = ClassUtils.getUserClass(this.applicationContext.getType(beanName, false));
		 * 		Supplier<Object> beanSupplier = () -> this.applicationContext.getBean(beanName);
		 * 		return new EndpointBean(this.applicationContext.getEnvironment(), beanName, beanType, beanSupplier);
		 * }
		 * 
		 * EndpointBean(Environment environment, String beanName, Class<?> beanType, Supplier<Object> beanSupplier) {
		 * 		MergedAnnotation<Endpoint> annotation = MergedAnnotations.from(beanType, SearchStrategy.TYPE_HIERARCHY).get(Endpoint.class);
		 * 		String id = annotation.getString("id");
		 * 		Assert.state(StringUtils.hasText(id), () -> "No @Endpoint id attribute specified for " + beanType.getName());
		 * 		this.beanName = beanName;
		 * 		this.beanType = beanType;
		 * 		this.beanSupplier = beanSupplier;
		 * 		this.id = EndpointId.of(environment, id);
		 * 		this.enabledByDefault = annotation.getBoolean("enableByDefault");
		 * 		//获取对应的 filter,controller 对应的是 ControllerEndpointFilter
		 * 		this.filter = getFilter(beanType);
		 * }
		 */
		//addExtensionBeans(endpointBeans)
		/**
		 * 将所有 endpointBean 对应的 extensionBean 填充到其中（必须匹配）
		 * private void addExtensionBeans(Collection<EndpointBean> endpointBeans) {
		 * 		Map<EndpointId, EndpointBean> byId = endpointBeans.stream()
		 * 			.collect(Collectors.toMap(EndpointBean::getId, Function.identity()));
		 * 		//得到所有 @EndpointExtension 及其子注解的类名
		 * 		//注意 @EndpointExtension 上的 id 对应 @Endpoint 上的 id
		 * 		//说明，此 EndpointExtension 是对 对应的 Endpoint 类的扩展
		 * 		String[] beanNames = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors(this.applicationContext,EndpointExtension.class);
		 * 		for (String beanName : beanNames) {
		 * 			ExtensionBean extensionBean = createExtensionBean(beanName);
		 * 			//得到 ExtensionBean 对应的 EndpointBean
		 * 			EndpointBean endpointBean = byId.get(extensionBean.getEndpointId());
		 * 			...
		 * 			addExtensionBean(endpointBean, extensionBean);
		 * 		}
		 * }
		 * 
		 * private void addExtensionBean(EndpointBean endpointBean, ExtensionBean extensionBean) {
		 * 		//只有 endpointBean、extensionBean、对应的 supllier 匹配，才能将 extensionBean 加入 endpointBean
		 * 		if (isExtensionExposed(endpointBean, extensionBean)) {
		 * 			Assert.state(isEndpointExposed(endpointBean) || isEndpointFiltered(endpointBean),
		 * 				() -> "Endpoint bean '" + endpointBean.getBeanName() + "' cannot support the extension bean '"
		 * 					+ extensionBean.getBeanName() + "'");
		 * 			endpointBean.addExtension(extensionBean);
		 * 		}
		 * }
		 * 
		 * private boolean isExtensionExposed(EndpointBean endpointBean, ExtensionBean extensionBean) {
		 * 		//isFilterMatch：判断 extensionBean 对应的 filter 和 endpointBean 对应的是否一样
		 * 		//isExtensionExposed:一般为 true
		 * 		return isFilterMatch(extensionBean.getFilter(), endpointBean)
		 * 			&& isExtensionTypeExposed(extensionBean.getBeanType());
		 * }
		 * 
		 * //判断 endpointbean 是否匹配对应的 filter
		 * //判断此 bean 是否被 exclude 了！(配置文件配置)
		 * //判断 bean 上的注解是否匹配当前 supplier
		 * private boolean isEndpointExposed(EndpointBean endpointBean) {
		 * 		//isFilterMatch：判断 bean 是否匹配对应的 filter，具体可查看：isExposed(ExposableEndpoint<?> endpoint)、isExcluded(ExposableEndpoint<?> endpoint) 方法
		 * 		//isEndpointFiltered：判断此 bean 是否被 exclude 了！(配置文件配置)
		 * 		//isEndpointExposed：判断 bean 上的注解是否匹配当前 supplier
		 * 		//其中@Endpoint、@WebEndpoint 都只适用于 WebDiscoverer！原因可跟踪一下方法源码
		 * 		return isFilterMatch(endpointBean.getFilter(), endpointBean) && !isEndpointFiltered(endpointBean)
		 * 			&& isEndpointTypeExposed(endpointBean.getBeanType());
		 * }
		 */
		//convertToEndpoints(endpointBeans)
		/**
		 * private Collection<E> convertToEndpoints(Collection<EndpointBean> endpointBeans) {
		 * 		Set<E> endpoints = new LinkedHashSet<>();
		 * 		for (EndpointBean endpointBean : endpointBeans) {
		 * 			//这里就过滤掉不属于当前 supplier 的 endpoint 的了
		 * 			if (isEndpointExposed(endpointBean)) {
		 * 				endpoints.add(convertToEndpoint(endpointBean));
		 * 			}
		 * 		}
		 * 		//得到属于自己的包装了 对应的 extendendpoint 的 endpoint
		 * 		return Collections.unmodifiableSet(endpoints);
		 * }
		 * 
		 * private E convertToEndpoint(EndpointBean endpointBean) {
		 * 		MultiValueMap<OperationKey, O> indexed = new LinkedMultiValueMap<>();
		 * 		EndpointId id = endpointBean.getId();
		 * 		//为 endpoint 创建 operations
		 * 		addOperations(indexed, id, endpointBean.getBean(), false);
		 * 		//一个 endpoint 只能有一个 extension
		 * 		if (endpointBean.getExtensions().size() > 1) {
		 * 			//报错
		 * 		}
		 * 		for (ExtensionBean extensionBean : endpointBean.getExtensions()) {
		 * 			//为 endpoint 对应的 extension 添加 operation
		 * 			addOperations(indexed, id, extensionBean.getBean(), true);
		 * 		}
		 * 		//判断 operation 有没有重复
		 * 		assertNoDuplicateOperations(endpointBean, indexed);
		 * 		List<O> operations = indexed.values().stream().map(this::getLast).filter(Objects::nonNull)
		 * 			.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
		 * 		//创建
		 * 		return createEndpoint(endpointBean.getBean(), id, endpointBean.isEnabledByDefault(), operations);
		 * }
		 * 
		 * private void addOperations(MultiValueMap<OperationKey, O> indexed, EndpointId id, Object target,boolean replaceLast) {
		 * 		Set<OperationKey> replacedLast = new HashSet<>();
		 * 		//创建此 endpoint 对应的所有 Operations
		 * 		Collection<O> operations = this.operationsFactory.createOperations(id, target);
		 * 		for (O operation : operations) {
		 * 			OperationKey key = createOperationKey(operation);
		 * 			O last = getLast(indexed.get(key));
		 * 			if (replaceLast && replacedLast.add(key) && last != null) {
		 * 				ndexed.get(key).remove(last);
		 * 			}
		 * 			indexed.add(key, operation);
		 * 		}
		 * }
		 */
	}
	
	public void 请求生效流程DiscoveredOperationsFactory() {
		/**
		 * Collection<O> createOperations(EndpointId id, Object target) {
		 * 		return MethodIntrospector
		 * 			.selectMethods(target.getClass(), (MetadataLookup<O>) (method) -> createOperation(id, target, method))
		 * 			.values();
		 * }
		 * 
		 * //又是各种嵌套，最终会走到不同 Discoverer 的各自实现中
		 * 		ControllerEndpointDiscoverer.createOperation（不支持）
		 * 		JmxEndpointDiscoverer.createOperation
		 * 		ServletEndpointDiscoverer.createOperation（不支持）
		 * 		WebEndpointDiscoverer.createOperation
		 * 
		 * 同样的：
		 * 		ControllerEndpointDiscoverer.createEndpoint
		 * 		JmxEndpointDiscoverer.createEndpoint
		 * 		ServletEndpointDiscoverer.createEndpoint
		 * 		WebEndpointDiscoverer.createEndpoint
		 */
	}
	
	public void endpoint() {
		/**
		 * 使用@Endpoint(id = "xxx")注解
		 * 使用@EndpointExtension(endpoint = XxxEndpoint.class)注解
		 * 使用@ControllerEndpoint(id = "xxx")注解
		 * 使用@RestControllerEndpoint(id = "xxx")注解
		 * 使用@ServletEndpoint(id = "xxx")注解
		 * 使用@WebEndpoint(id = "xxx")注解
		 * 使用@JmxEndpoint(id = "xxx")注解
		 * 使用@EndpointJmxExtension(endpoint = XxxEndpoint.class)注解
		 */
		/**
		 * archaius、nacosconfig、nacosdiscovery、ping2、beans、caches、health、info、
		 * 			conditions、configprops、env、loggers、heapdump、threaddump、metrics、
		 * 			quartz、scheduledtasks、mappings、refresh、features、serviceregistry
		 * 
		 * auditevents（默认：jmx1，web0）：显示当前应用程序的审计事件信息
		 * beans（默认：jmx1，web0）：显示应用 Spring Beans 的完整列表，即获取应用上下文中创建的所有 Bean
		 * caches（默认：jmx1，web0）：显示可用缓存信息
		 * conditions（默认：jmx1，web0）：显示自动装配类的状态及及应用信息
		 * configprops（默认：jmx1，web0）：显示所有 @ConfigurationProperties 列表，即获取应用中配置的所有属性的信息报告
		 * env（默认：jmx1，web0）：显示 ConfigurableEnvironment 中的属性，它用来获取应用所有可用的环境属性报告
		 * 		包括环境变量、JVM 属性、应用的配置属性、命令行中的参数
		 * flyway（默认：jmx1，web0）：显示 Flyway 数据库迁移信息
		 * health（默认：jmx1，web1）：显示应用的健康信息（未认证只显示status，认证显示全部信息详情）
		 * loggers（默认：jmx1，web0）：展示并修改应用的日志配置
		 * info（默认：jmx1，web1）：显示任意的应用信息（在资源文件写 info.xxx 即可）
		 * liquibase（默认：jmx1，web0）：展示 Liquibase 数据库迁移
		 * metrics（默认：jmx1，web0）：展示当前应用的 metrics 信息，比如内存信息、线程信息、垃圾回收信息等
		 * mappings（默认：jmx1，web0）：显示所有 @RequestMapping 路径集列表，即返回所有 Spring MVC 的控制器映射关系报告
		 * scheduledtasks（默认：jmx1，web0）：显示应用程序中的计划任务
		 * sessions（默认：jmx1，web0）：允许从 Spring 会话支持的会话存储中检索和删除用户会话
		 * shutdown（默认：jmx1，web0）：允许应用以优雅的方式关闭（默认情况下不启用）
		 * threaddump（默认：jmx1，web0）：执行一个线程 dump
		 * httptrace（默认：jmx1，web0）：显示 HTTP 跟踪信息（默认显示最后 100 个 HTTP 请求 - 响应交换）
		 * 
		 * 如果是一个 Web 应用，还会有如下所示的端点：
		 * 		heapdump（默认：jmx，web1）：返回一个 GZip 压缩的 hprof 堆转储文件
		 * 		jolokia（默认：jmx，web0）：展示通过 HTTP 暴露的 JMX beans
		 * 		logfile（默认：jmx，web0）：返回日志文件内容
		 * 		prometheus（默认：jmx，web0）：展示一个可以被 Prometheus 服务器抓取的 metrics 数据
		 * 
		 * 开启、关闭端点：
		 * 		management.endpoint.shutdown.enabled=true
		 * 
		 * 如果不想暴露那么多端点，可以关闭默认的配置，然后手动指定需要开启哪些端点：
		 * 比如下面配置表示关闭所有端点， 只开启 info 端点：
		 * 		management.endpoints.enabled-by-default=false
		 * 		management.endpoint.info.enabled=true
		 * 
		 * 可以在 application.properties 中自定义需要暴露哪些端点
		 * 例如要暴露 mapping 和 metrics 端点，添加如下配置即可：
		 * management.endpoints.web.exposure.include=mapping,metric
		 * 如果要暴露所有端点，则添加如下配置即可：
		 * management.endpoints.web.exposure.include=*
		 */
	}
	
	public void endpoint_保护() {
		//可以使用 Spring Security 保护
		/**
		 * @Configuration
		 * public class ActuatorSecurity extends WebSecurityConfigurerAdapter{
		 * 		@Override
		 * 		protected void configure(HttpSecurity http) throws Exception {
		 * 			http.requestMatcher(EndpointRequest.toAnyEndpoint())
		 * 				.authorizeRequests()
		 * 				.anyRequest().hasRole("ADMIN")
		 * 				.and()
		 * 				.httpBasic();
		 * 		}
		 * }
		 * 
		 * properties：
		 * spring.security.user.name=hangge
		 * spring.security.user.password=123
		 * spring.security.user.roles=ADMIN
		 */
	}
	
	public void endpoint_缓存() {
		/**
		 * CachingOperationInvokerAdvisor：
		 * 配置：
		 * 		management.endpoint.<endpoint_id>.cache.time-to-live
		 */
	}
	
	public void health() {
		//HealthEndpointAutoConfiguration
		/**
		 * 会调用：
		 * 		HealthEndpointConfiguration
		 * 		ReactiveHealthEndpointConfiguration
		 * 		HealthEndpointWebExtensionConfiguration
		 * 		HealthEndpointReactiveWebExtensionConfiguration
		 */
		//HealthEndpointConfiguration
		/**
		 * SimpleStatusAggregator：
		 * 		为了将通过 {@link Health#getStatus()} 表示的子系统状态组合成整个系统的状态所必需的
		 * HttpCodeStatusMapper：
		 * 		用于将 {@link Status 健康状态} 映射到 HTTP 状态代码的策略
		 * HealthEndpointGroups：
		 * 		用于健康端点的 {@link HealthEndpointGroup groups} 集合
		 * HealthContributorRegistry：
		 * 
		 * HealthEndpointGroupsBeanPostProcessor：
		 */
		//HealthEndpointWebExtensionConfiguration
		/**
		 * 该类作用为：初始化HealthEndpointWebExtension，并将HealthContributorRegistry对象注入进来
		 */
		//HealthEndpointWebExtension
		/**
		 * 这个是对HealthEndpoint的扩展
		 * 请求/actuator/health就会请求到HealthEndpointWebExtension中
		 * 该对象new的时候会接收一个HealthContributorRegistry对象
		 * HealthContributorRegistry对象中包含了所有HealthContributor接口的实现类作为健康检查的component
		 * 调用该类时，只有每一个component都是UP，返回结果才会是UP
		 * 
		 * http://localhost:8080/actuator/health会进：
		 * 		WebEndpointResponse<HealthComponent> health(ApiVersion apiVersion, SecurityContext securityContext)
		 * http://localhost:8080/actuator/health/xxx会进：
		 * 		WebEndpointResponse<HealthComponent> health(ApiVersion apiVersion, SecurityContext securityContext,String... path) 
		 * 
		 * 当执行http://localhost:8080/actuator/health：
		 * 		HealthEndpointWebExtension会调用各个子HealthIndicator进行检查
		 * 当执行http://localhost:8080/actuator/health/<componentName>
		 * 		检查该HealthContributor实现的健康状况
		 */
		//HealthContributorRegistry
		/**
		 * 在HealthEndpointConfiguration初始化
		 * 该类在new的时候会获取所有存在的HealthContributor接口的实现Bean
		 * HealthEndpointWebExtension中会使用该HealthContributorRegistry来获取每一个HealthContributor实现Bean的状态
		 */
		//HealthEndpointProperties
		/**
		 * management.endpoint.health:
		 * 		showDetails: NEVER 	#默认值为NEVER，还可以是WHEN_AUTHORIZED、ALWAYS
		 * 当showDetails: NEVER时：
		 * 		http://localhost:8080/actuator/health显示：
		 * 			{"status": "UP"}
		 * 当showDetails: ALWAYS时：
		 * 		http://localhost:8080/actuator/health显示：
		 * 			{
		 * 				"status": "UP",
		 * 				"components": {
		 * 					discoveryComposite...
		 * 					diskSpace...
		 * 					nacosConfig...
		 * 					"nacosDiscovery": {
		 * 						"status": "UP"
		 * 					},
		 * 					"ping": {
		 * 						"status": "UP"
		 * 					},
		 * 					refreshScope...
		 * 				}
		 * 			}
		 * 
		 * 当showDetails: NEVER时：
		 * 		http://localhost:8080/actuator/health/nacosDiscovery显示：
		 * 			404
		 * 当showDetails: ALWAYS时：(只有/health能显示时，这里才会显示，否则就会显示404)
		 * 		http://localhost:8080/actuator/health/nacosDiscovery显示：
		 * 			{"status":"UP"}
		 * 当showDetails：WHEN-AUTHORIZED时，未登录则实现和NEVER一样，登录后显示和ALWAYS一样
		 * 
		 * management.endpoint.health:
		 * 		status:
		 * 			order: List<String> //按严重程度排列的以逗号分隔的健康状态列表
		 * 			httpMapping: Map<String, Integer> //将健康状态映射到 HTTP 状态代码。 默认情况下，注册的健康状态映射到合理的默认值（例如，UP 映射到 200）
		 * 
		 * management.endpoint.health:
		 * 		showComponents: <NEVER、WHEN_AUTHORIZED、ALWAYS> //何时显示组件。 如果未指定，将使用“ALWAYS”设置
		 * 
		 */
	}
	
	
	public void health_HealthIndicator() {
		/**
		 * 默认情况下 Spring Boot 会根据 classpath 中依赖的添加情况来自动配置一些健康指示器（HealthIndicators）：
		 * CassandraHealthIndicator：检查 Cassandra 数据库状况
		 * DiskSpaceHealthIndicator：检查磁盘空间是否不足
		 * DataSourceHealthIndicator：检查是否可以从 DataSource 获取一个 Connection
		 * ElasticsearchHealthIndicator：检查 Elasticsearch 集群状况
		 * InfluxDbHealthIndicator：检查 InfluxDB 服务器状况
		 * JmsHealthIndicator：检查 JMS 消息代理状况
		 * MailHealthIndicator：检查邮件服务器状况
		 * MongoHealthIndicator：检查 Mongo 数据库状况
		 * Neo4jHealthIndicator：检查 Neo4j 服务器状况
		 * RabbitHealthIndicator：检查 Rabbit 服务器状况
		 * RedisHealthIndicator：检查 Redis 服务器状况
		 * SolrHealthIndicator：检查 Solr 服务器状况
		 * 
		 * 如果我们不需要这么多 HealthIndicators，则可以通过如下配置关闭所有的 HealthIndicators 自动化配置：
		 * management.health.defaults.enabled=false
		 * 然后根据情况开启：
		 * management.health.db.enabled=true单独开启，但这也不是唯一条件
		 * 		例如：DataSourceHealthContributorAutoConfiguration还要如下条件
		 * 			@ConditionalOnClass({ JdbcTemplate.class, AbstractRoutingDataSource.class })
		 * 			@ConditionalOnBean(DataSource.class)
		 */
	}
	
	public void health_HealthIndicator_自定义() {
		/**
		 * @Component
		 * public class MyHealth implements HealthIndicator {
		 * 		@Override
		 * 		public Health health() {
		 * 			if(Math.random() > 0.5) {
		 * 				return Health.up().withDetail("msg", "网络连接正常...").build();
		 * 			}
		 * 			return Health.down().withDetail("msg", "网络断开...").build();
		 * 		}
		 * }
		 * 
		 */
		//增加响应状态
		/**
		 * 默认的响应状态一共有 4 种：DOWN、OUT_OF_SERVICE、UP、UNKNOWN
		 * 如果需要加增加响应状态，可以在 application.properties 种通过 management.health.status.order 属性进行配置
		 * 比如下面增加一个响应状态 FATAL：
		 * management.health.status.order=FATAL,DOWN,OUT_OF_SERVICE,UP,UNKNOWN
		 * 配置完毕后，就可以在 helath 方法中返回自定义的响应状态了：
		 * @Component
		 * public class MyHealth implements HealthIndicator {
		 * 		@Override
		 * 		public Health health() {
		 * 			return Health.status("FATAL").withDetail("msg", "网络断开...").build();
		 * 		}
		 * }
		 */
		//命名
		/**
		 * XxxHealthIndicator，会自动截掉后面的HealthIndicator，名字叫Xxx
		 * @Bean
		 * public Ping2HealthIndicator ping2HealthIndicator() {
		 * 		...
		 * }
		 * 这里会以方法名为该自定义HealthIndicator命名
		 */
	}
	
	public void metrics_基础() {
		/**
		 * MetricsAutoConfiguration
		 * SimpleMetricsExportAutoConfiguration
		 * 		SimpleMeterRegistry
		 * 		SimpleProperties
		 * CompositeMeterRegistryAutoConfiguration
		 * 		NoOpMeterRegistryConfiguration
		 * 		CompositeMeterRegistryConfiguration（CompositeMeterRegistry）
		 * MetricsEndpointAutoConfiguration
		 * 		MetricsEndpoint
		 */
		//MetricsProperties
		/**
		 * management.metrics:
		 * 		//自动配置的MeterRegistry实现是否应该绑定到Metrics上的全局静态注册表
		 * 		//对于测试，将其设置为“false”以最大化测试独立性
		 * 		useGlobalRegistry: ture
		 * 		//是否应启用以指定名称开头的meter ID
		 * 		//key：meter ID，key为all表示配置所有的meter
		 * 		enable:	Map<String, Boolean>
		 * 		//应用于每个meter的通用标签
		 * 		tags:	Map<String, String>
		 * 		web:
		 * 			client:
		 * 				request:
		 * 					metricName: http.client.requests
		 * 					autotime: 
		 * 						enabled: true
		 * 						percentilesHistogram: 
		 * 						percentiles: 
		 * 				maxUriTags: 100
		 * 			server:
		 * 				request:
		 * 					metricName: http.server.requests
		 * 					ignoreTrailingSlash: true
		 * 					autotime:
		 * 						...
		 * 				maxUriTags: 100
		 * 		data:
		 * 			repository:
		 * 				metricName: spring.data.repository.invocations
		 * 				autotime: 
		 * 					...
		 * 		distribution:
		 * 			percentilesHistogram: Map<String, Boolean>
		 * 			percentiles: Map<String, double[]>
		 * 			slo: Map<String, ServiceLevelObjectiveBoundary[]>
		 * 			minimumExpectedValue: Map<String, String>
		 * 			maximumExpectedValue: Map<String, String>
		 * 			
		 */
		//MetricsEndpointAutoConfiguration
		/**
		 * 初始化：
		 * 		@Endpoint(id = "metrics") MetricsEndpoint(registry)
		 * 		其中registry默认实现为SimpleMeterRegistry，是MeterRegistry的实现之一
		 * MetricsEndpoint会根据是否传入requiredMetricName和tag来判断返回哪些Meter
		 */
		//http://localhost:8080/actuator/metrics
		/**
		 * 调用该url，会执行MetricsEndpoint.listNames()，
		 * 该方法会从MeterRegistry registry中获取Set<String> names
		 * 即：[jvm.buffer.count, jvm.buffer.memory.used, ...]
		 * 
		 * JVM：
		 * jvm.memory.max：JVM 最大内存
		 * jvm.memory.committed：JVM 可用内存 #展示并监控堆内存和 Metaspace
		 * jvm.memory.used：JVM 已用内存 #展示并监控堆内存和 Metaspace
		 * jvm.buffer.memory.used：JVM 缓冲区已用内存
		 * jvm.buffer.count：当前缓冲区数
		 * jvm.threads.daemon：	JVM 守护线程数 #显示在监控页面
		 * 	jvm.threads.live：JVM 当前活跃线程数 #显示在监控页面；监控达到阈值时报警
		 * jvm.threads.peak：JVM 峰值线程数 #显示在监控页面
		 * jvm.classes.loaded：加载 classes 数
		 * jvm.classes.unloaded：未加载的 classes 数
		 * jvm.gc.memory.allocated：GC 时，年轻代分配的内存空间
		 * jvm.gc.memory.promoted：GC 时，老年代分配的内存空间
		 * jvm.gc.max.data.size：GC 时，老年代的最大内存空间
		 * jvm.gc.live.data.size：FullGC 时，老年代的内存空间
		 * 	jvm.gc.pause：GC 耗时 #显示在监控页面
		 * 
		 * TOMCAT：
		 * tomcat.sessions.created：tomcat 已创建 session 数
		 * tomcat.sessions.expired：tomcat 已过期 session 数
		 * tomcat.sessions.active.current：tomcat 活跃 session 数
		 * tomcat.sessions.active.max：tomcat 最多活跃 session 数
		 * tomcat.sessions.alive.max.second：tomcat 最多活跃 session 数持续时间
		 * tomcat.sessions.rejected：超过 session 最大配置后，拒绝的 session 个数
		 * tomcat.global.error：错误总数
		 * tomcat.global.sent：发送的字节数
		 * tomcat.global.request.max：request 最长时间
		 * tomcat.global.request：全局 request 次数和时间
		 * tomcat.global.received：全局 received 次数和时间
		 * tomcat.servlet.request：servlet 的请求次数和时间
		 * tomcat.servlet.error：servlet 发生错误总数
		 * tomcat.servlet.request.max：servlet 请求最长时间
		 * tomcat.threads.busy：tomcat 繁忙线程
		 * tomcat.threads.current：tomcat 当前线程数（包括守护线程）
		 * tomcat.threads.config.max：tomcat 配置的线程最大数
		 * tomcat.cache.access：tomcat 读取缓存次数
		 * tomcat.cache.hit：tomcat 缓存命中次数
		 * 
		 * CPU：
		 * system.cpu.count：CPU 数量
		 * system.load.average.1m：load average
		 * system.cpu.usage：系统 CPU 使用率
		 * process.cpu.usage：当前进程 CPU 使用率
		 * http.server.requests：http 请求调用情况
		 * process.uptime：应用已运行时间
		 * process.files.max：允许最大句柄数
		 * process.start.time：应用启动时间点
		 * process.files.open：当前打开句柄数
		 */
		//http://localhost:8080/actuator/metrics/jvm.buffer.count
		/**
		 * 调用该url，会执行MetricsEndpoint.metric(@Selector String requiredMetricName, @Nullable List<String> tag)
		 * 该方法会从MeterRegistry registry中找requiredMetricName和tags匹配的Meter
		 * 		可能有多个Meter，实现如：JvmMemoryMetrics等
		 * 		//TODO
		 */
	}
	
	public void metrics_注解ConditionalOnEnabledMetricsExport() {
		//@ConditionalOnEnabledMetricsExport("simple")
		/**
		 * management.metrics.export.<simple>.enabled，单独配置<simple>是否可用
		 * 如果上面这个没有配置，则会使用如下配置项：
		 * management.metrics.export.defaults.enabled进行统一配置，这个没有配置，则默认为true
		 */
	}
	
	public void metric_SimpleMeterRegistry() {
		/**
		 * SimpleMetricsExportAutoConfiguration中初始化
		 * 
		 * Micrometer 包含一个 SimpleMeterRegistry，它将每个仪表的最新值保存在内存中，
		 * 		并且不会将数据导出到任何地方
		 * 如果您还没有首选的监控系统，您可以使用简单的注册表开始使用指标
		 * SimpleMeterRegistry 在基于 Spring 的应用程序中为您自动装配
		 * 
		 * management.metrics.export.simple:
		 * 		enabled: true #默认值为true
		 * 		step: 1 #默认值为1
		 * 		mode: CUMULATIVE #默认值为CUMULATIVE
		 * 
		 * mode: 
		 * 		CUMULATIVE：累计
		 * 		STEP：步进
		 */
	}
	
	public void metrics_CompositeMeterRegistry() {
		/**
		 * 在CompositeMeterRegistryAutoConfiguration中初始化
		 * CompositeMeterRegistryAutoConfiguration会import：
		 * 		NoOpMeterRegistryConfiguration：初始化：CompositeMeterRegistry
		 * 		CompositeMeterRegistryConfiguration：初始化：AutoConfiguredCompositeMeterRegistry
		 * 
		 * NoOpMeterRegistryConfiguration：
		 * 		初始化的条件是：@ConditionalOnMissingBean(MeterRegistry.class)
		 * 			即：容器中没有其他MeterRegistry
		 * 		该类的所有行为都是无操作，即不生效的
		 * 
		 * AutoConfiguredCompositeMeterRegistry：
		 * 		初始化条件是：当容器中有MeterRegistry但没有一个 primary MeterRegistry时，才注入该MeterRegistry
		 * 			默认情况下会初始化SimpleMeterRegistry，所以不会初始化AutoConfiguredCompositeMeterRegistry
		 * 			当再引入或者自定义一个MeterRegistry，则会初始化该AutoConfiguredCompositeMeterRegistry
		 * 		CompositeMeterRegistryConfiguration初始化该Bean的时候会传入一个List<MeterRegistry> registries
		 * 		即：这里会将系统中的MeterRegistry实现的bean注入到这里，
		 * 			而NoOpMeterRegistryConfiguration只是初始化但是不注入
		 * 		重：这里的多个MeterRegistry可以理解为多个不同的监控系统，例如：本地监控，Prometheus监控
		 * 			将一个指标注册到CompositeMeterRegistry，即里面的多个监控系统都会收到该指标
		 * 
		 * Micrometer 提供了一个 CompositeMeterRegistry，
		 * 		您可以向其中添加多个注册表，让您可以同时将指标发布到多个监控系统
		 * 
		 * 举例：
		 * CompositeMeterRegistry composite = new CompositeMeterRegistry();
		 * Counter compositeCounter = composite.counter("counter");
		 * compositeCounter.increment();（1）
		 * SimpleMeterRegistry simple = new SimpleMeterRegistry();
		 * composite.add(simple);（2）
		 * compositeCounter.increment();（3）
		 * 1：在组合中存在注册表之前，增量是 NOOP 的。 此时计数器的计数仍为 0
		 * 2：一个名为 counter 的计数器被注册到简单的注册表中
		 * 3：简单注册表计数器与组合中任何其他注册表的计数器一起递增
		 */
	}
	
	public void metrics_Metrics中的globalRegistry(){
		/**
		 * Metrics类中有一个类变量：
		 *  public static final CompositeMeterRegistry globalRegistry = new CompositeMeterRegistry();
		 *  Micrometer 提供了一个静态全局注册表 Metrics.globalRegistry 和一组静态构建器，
		 *  用于基于此注册表生成仪表（注意 globalRegistry 是一个复合注册表）：
		 *  
		 *  举例：
		 *  Counter featureCounter = Metrics.counter("feature", "region", "test"); (1)
		 *  featureCounter.increment();
		 *  Metrics.counter("feature.2", "type", type).increment(); (2)
		 *  Metrics.addRegistry(new SimpleMeterRegistry()); (3)
		 *  1：在可能的情况下（尤其是在仪表性能至关重要的情况下），将 Meter 实例存储在字段中，
		 *  	以避免在每次使用时查找它们的名称或标签
		 *  2：当需要从本地上下文中确定标签时，您别无选择，只能在方法体内构造或查找 Meter
		 *  	查找成本只是单个哈希查找，因此对于大多数用例来说是可以接受的
		 *  3：可以在使用 Metrics.counter(...​) 之类的代码创建计量表后添加注册表
		 *  	这些仪表被添加到每个注册表中，因为它绑定到全局组合
		 */
	}
	
	public void metrics_指标命名约束() {
		/**
		 * Micrometer 采用一种命名约定，将小写单词与 . （点）字符
		 * 不同的监控系统对命名约定有不同的建议，一些命名约定可能在一个系统和另一个系统之间不兼容
		 * 监控系统的每个 Micrometer 实现都带有一个命名约定，将小写的点符号名称转换为监控系统的推荐命名约定
		 * 此外，此命名约定实现从指标名称和标签中删除了监控系统不允许的特殊字符
		 * 您可以通过实现 NamingConvention 并在注册表上设置它来覆盖注册表的默认命名约定：
		 * registry.config().namingConvention(myCustomNamingConvention);
		 * 解释：其实就是修改registry中的如下类变量：
		 * 		private NamingConvention namingConvention = NamingConvention.snakeCase;
		 * 有了命名约定，在 Micrometer 中注册的以下计时器在各种监控系统中看起来都很好：
		 * registry.timer("http.server.requests");
		 * 		Prometheus - http_server_requests_duration_seconds
		 * 		Atlas - httpServerRequests
		 * 		Graphite - http.server.requests
		 * 		InfluxDB - http_server_requests
		 */
		
	}
	
	public void metrics_tag和指标下钻() {
		/**
		 * 将多个指标命名为同一个指标名称，但是命名不同的tag
		 * 例如：有2个DataSource，指标都叫arch.datasource2.execute.count
		 * 但一个tag为name=druidId3DS，另一个tag为name=duridUserW10DS
		 * 
		 * http://localhost:8081/actuator/metrics/arch.datasource2.execute.count
		 * 会获得以上2个数据源的访问总和
		 * http://localhost:8081/actuator/metrics/arch.datasource2.execute.count?tag=name:druidId3DS
		 * 会获得tag为name=druidId3DS的数据源指标
		 */
		/**
		 * 我们建议您在命名标签时遵循与计量器名称相同的小写点符号
		 * 对标签使用这种一致的命名约定可以更好地转换为相应监控系统的惯用命名方案
		 * 
		 * 假设我们正在尝试测量 http 请求的数量和数据库调用的数量：
		 * registry.counter("database.calls", "db", "users")
		 * registry.counter("http.requests", "uri", "/api/users")
		 * 此变体提供了足够的上下文，因此，如果仅选择名称，则可以推断该值并且至少具有潜在的意义
		 * 例如，如果我们选择 database.calls，我们可以看到对所有数据库的调用总数
		 * 然后我们可以group by或者select by db来进一步下钻或者对比分析调用对每个数据库的贡献
		 * 
		 * registry.counter("calls", "class", "database", "db", "users");
		 * registry.counter("calls", "class", "http", "uri", "/api/users");
		 * 在这种方法中，如果我们选择调用，我们会得到一个值，该值是对数据库和 API 端点的调用次数的总和
		 * 如果没有进一步的维度钻取，这个时间序列是没有用的
		 */
		/**
		 * Common Tags：
		 * 您可以在注册表级别定义通用标签，并将它们添加到报告给监控系统的每个指标中
		 * 这通常用于对操作环境的维度下钻，例如主机、实例、区域、堆栈等：
		 * registry.config().commonTags("stack", "prod", "region", "us-east-1");
		 * registry.config().commonTags(Arrays.asList(Tag.of("stack", "prod"), Tag.of("region", "us-east-1")));
		 * 通用标签通常必须在任何（可能是自动配置的）仪表绑定器之前添加到注册表中
		 * 根据您的环境，有不同的方法可以实现这一点：
		 * 如果你使用 Spring Boot，你有两种选择：
		 * 		1：使用配置属性添加常用标签
		 * 		2：如果您需要更大的灵活性（例如，您必须向共享库中定义的注册表添加通用标签），
		 * 			请将 MeterRegistryCustomizer 回调接口注册为 bean 以添加您的通用标签
		 * 			有关更多信息，请参阅 Spring Boot 参考文档
		 */
		/**
		 * Tag Values：
		 * 标记值必须非空
		 * 请注意来自用户提供的来源的标签值可能会破坏指标的基数
		 * 您应该始终仔细规范化并为用户提供的输入添加边界
		 * 有时，原因是偷偷摸摸的。 考虑用于记录服务端点上的 HTTP 请求的 URI 标记
		 * 如果我们不将 404 限制为 NOT_FOUND 之类的值，则度量的维度将随着每个无法找到的资源而增长
		 */
	}
	
	public void metrics_MeterFilters() {
		/**
		 * 在MetricsAutoConfiguration中初始化
		 * 您可以使用仪表过滤器配置每个注册表，
		 * 这使您可以更好地控制仪表的注册方式和时间以及它们发出的统计信息类型
		 * 仪表过滤器具有三个基本功能：
		 * 1：拒绝（或接受）正在注册的仪表
		 * 2：转换仪表 ID（例如，更改名称、添加或删除标签以及更改描述或基本单位）
		 * 3：Configure distribution statistics for some meter types
		 * 
		 * MeterFilter 的实现以编程方式添加到注册表中：
		 * 		registry.config()
		 * 			.meterFilter(MeterFilter.ignoreTags("too.much.information"))
		 * 			.meterFilter(MeterFilter.denyNameStartsWith("jvm"));
		 * 这段代码的意思是向MeterRegistry注册了2个Filter用于hi表过滤
		 * 仪表过滤器按顺序应用，转换或配置仪表的结果是链式的，MeterRegistry内部的Filter是通过一个数组存储的
		 */
		/**
		 * Deny or Accept Meters：
		 * new MeterFilter() {
		 * 		@Override
		 * 		public MeterFilterReply accept(Meter.Id id) {
		 * 			if(id.getName().contains("test")) {
		 * 				return MeterFilterReply.DENY;
		 * 			}
		 * 			 return MeterFilterReply.NEUTRAL;
		 * 		}
		 * }
		 * https://micrometer.io/docs/concepts#_registry
		 * 6.1. Deny or Accept Meters
		 * 
		 * registry.config()
		 * 		.meterFilter(MeterFilter.acceptNameStartsWith("http"))
		 * 		.meterFilter(MeterFilter.deny());
		 * 这通过将两个过滤器堆叠在一起实现了另一种形式的白名单，此注册表中只允许存在 http 指标
		 * 
		 * 转换指标：
		 * new MeterFilter() {
		 * 		@Override
		 * 		public Meter.Id map(Meter.Id id) {
		 * 			if(id.getName().startsWith("test")) {
		 * 				return id.withName("extra." + id.getName()).withTag("extra.tag", "value");
		 * 			}
		 * 			return id;
		 * 		}
		 * }
		 * 转换指标此过滤器有条件地向以 test 名称开头的计量器添加名称前缀和附加标签
		 * 
		 * 配置分布统计：
		 * new MeterFilter() {
		 * 		@Override
		 * 		public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
		 * 			if (id.getName().startsWith(prefix)) {
		 * 				return DistributionStatisticConfig.builder()
		 * 					.publishPercentiles(0.9, 0.95)
		 * 					.build()
		 * 					.merge(config);
		 * 			}
		 * 			return config;
		 * 		}
		 * }
		 * 通常，您应该创建一个仅包含您希望配置的部分的新 DistributionStatisticConfig，然后将其与输入配置合并
		 * 这使您可以下拉注册表提供的分布统计默认值，并将多个过滤器链接在一起，
		 * 每个过滤器都配置分布统计的某些部分（例如，您可能希望所有 HTTP 请求的 100 毫秒 SLA，
		 * 但只有几个关键的百分位直方图 端点）
		 */
	}
	
	public void metrics_RateAggregation_速率聚合() {
		/**
		 * Micrometer 知道特定监控系统是否期望在发布指标之前在客户端发生速率聚合，
		 * 或者作为服务器上查询的一部分即席发生。 它根据监控系统期望的风格来积累指标
		 * 
		 * 并非所有测量都被报告或最好被视为一个速率。 例如，仪表值和活动任务计数长任务计时器不是速率
		 * 
		 * https://micrometer.io/docs/concepts#_registry
		 * 7. Rate Aggregation
		 * 		7.1. Server-side
		 * 		7.2. Client-side
		 */
	}
	
	public void metrics_计数器() {
		/**
		 * 除了使用 metrics 端点默认的这些统计指标外，我们还可以实现自定义统计指标
		 * Metrics 提供 4 种基本的度量类型：Gauge、Counter、Timer、Summary
		 */
		//Gauge（计量器）：
		/**
		 * 是最简单的度量类型，只有一个简单的返回值，他用来记录一些对象或者事物的瞬时值
		 * 
		 * 在任何地方：
		 * 		Metrics.gauge("user.test.gauge", 3);
		 * 即可通过http://localhost:8080/actuator/metrics/user.test.gauge获取到值3
		 */
		//Counter（计数器）：
		/**
		 * static final Counter userCounter = Metrics.counter("user.counter.total", "services", "demo");
		 * userCounter.increment(1D);
		 * 即可沟通过http://localhost:8080/actuator/metrics/user.counter.total查看到该计数值
		 */
		//Timer（计时器）
		//可以同时测量一个特定的代码逻辑块的调用（执行）速度和它的时间分布
		//简单来说，就是在调用结束的时间点记录整个调用块执行的总时间
		//适用于测量短时间执行的事件的耗时分布，例如消息队列消息的消费速率
		/**
		 * private Timer timer = Metrics.timer("user.test.timer","timer", "timersample");
		 * // 执行createOrder方法并记录执行时间
		 * timer.record(() -> createOrder());
		 * 即可沟通过http://localhost:8080/actuator/metrics/user.test.timer查看到该计时值
		 */
		//DistributionSummary（摘要）
		//用于跟踪事件的分布
		//它类似于一个计时器，但更一般的情况是，它的大小并不一定是一段时间的测量值
		//在 micrometer 中，对应的类是 DistributionSummary
		//它的用法有点像 Timer，但是记录的值是需要直接指定，而不是通过测量一个任务的执行时间
		/**
		 *  private DistributionSummary summary = Metrics.summary("user.test.summary","summary", "summarysample");
		 *  summary.record(2D);
		 *  summary.record(3D);
		 *  summary.record(4D);
		 *  即可沟通过http://localhost:8080/actuator/metrics/user.test.summary查看到该值：
		 *  [
		 *  	{"statistic":"COUNT","value":3},	
		 *  	{"statistic":"TOTAL","value":9},	
		 *  	{"statistic":"MAX","value":4},	
		 *  ]
		 */
		//LongTaskTimer
		//FunctionCounter
		//FunctionTimer
		//TimeGauge
	}
	public void metrics_自定义() {
		//自定义一个Meter指标
		/**
		 * 我们通过实现MeterBinder接口，并通过自动配置的MeterRegistry来注册指标数据。如示例：
		 * @Component
		 * public class MyMetrics implements MeterBinder{
		 * 		public void bindTo(MeterRegistry registry) {
		 * 			Gauge.builder("top.wisely.size", () -> MyEndpoint.status.size()).baseUnit("个")
		 * 				.description("获取自定义端点中状态数量")
		 * 				.register(registry);
		 * 		}
		 * }
		 */
	}
	
	public void info() {
		/**
		 * 自定义信息可以在 application.properties 配置文件中添加，这些以 info 开头的信息将在 info 端点中显示出来：
		 * info.build.version=${java.version}
		 * info.author.name=hangge
		 * info.author.email=service@hangge.com
		 * 请求http://localhost:8080/actuator/info，会展示出来
		 */
		/**
		 * 也可以通过 Java 代码自定义信息：
		 * 只需要将自定义类继承自 InfoContributor
		 * @Component
		 * public class MyInfo implements InfoContributor {
		 * 		@Override
		 * 		public void contribute(Info.Builder builder) {
		 * 			Map<String, String> info = new HashMap<>();
		 * 			info.put("name", "航歌");
		 * 			info.put("email", "service@hangge.com");
		 * 			builder.withDetail("author", info);
		 * 		}
		 * }
		 * 这种扩展方式，对http://localhost:8080/actuator/info请求的响应体进行内容扩充，不是一个独立的新info
		 */
		//原理
		/**
		 * InfoEndpointAutoConfiguration会初始化一个InfoEndpoint
		 * 初始化InfoEndpoint的时候会接收Spring容器中初始化好的所有实现了InfoContributor的InfoContributor
		 * InfoContributorAutoConfiguration中已经初始化好了：
		 * 		EnvironmentInfoContributor、GitInfoContributor、BuildInfoContributor
		 * 当用户访问http://localhost:8080/actuator/info时，其实是访问InfoEndpoint的public Map<String, Object> info()
		 * 		该方法中会循环调用EnvironmentInfoContributor、GitInfoContributor、BuildInfoContributor
		 * 		获取他们提供的info信息，例如：builder.withDetail("git", generateContent());
		 * 因此当我们自定义扩展info信息的时候，只要实现InfoContributor接口并注入到Spring容器中即可
		 */
	}
}
