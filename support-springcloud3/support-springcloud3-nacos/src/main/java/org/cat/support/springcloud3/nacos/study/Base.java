package org.cat.support.springcloud3.nacos.study;

/**
 * 
 * @author 王云龙
 * @date 2022年9月27日 下午2:02:23
 * @version 1.0
 * @description 原理
 *
 */
public class Base {
	public void DiscoveryClient() {
		/**
		 * DiscoveryClient是SpringCloud提供的服务发现客户端的标准接口
		 * 
		 * List<ServiceInstance> getInstances(String serviceId)：根据serviceId获取服务实例信息
		 * List<String> getServices()：获取所有的服务名称
		 */
	}
	
	public void NacosDiscoveryClient() {
		/**
		 * NacosDiscoveryClient实现DiscoveryClient接口
		 * 初始化的时候注入NacosServiceDiscovery实例
		 * 
		 * NacosDiscoveryClientConfiguration中创建
		 * 
		 * getInstances(String serviceId)方法调用：nacosServiceDiscovery.getInstances(serviceId);
		 * getServices()方法调用：nacosServiceDiscovery.getServices();
		 * 
		 * ：
		 * 		
		 */
	}
	
	public void SimpleDiscoveryClient() {
		/**
		 * 这是DiscoveryClient的另一个实现，是SpringCloud的
		 * 初始化的地方在SimpleDiscoveryClientAutoConfiguration
		 * 
		 * 如果在类路径中没有支持从注册中心发现服务的DiscoveryClient实例，则将使用SimpleDiscoveryClient实例
		 * 该实例使用SimpleDiscoveryProperties来获取有关服务和实例的信息
		 */
	}
	
	public void NacosReactiveDiscoveryClient() {
		/**
		 * NacosReactiveDiscoveryClientConfiguration中创建
		 */
	}
	
	public void NacosWatch() {
		/**
		 * NacosDiscoveryClientConfiguration中初始化
		 * 默认情况下会初始化，如果不想让其初始化可以配置spring.cloud.nacos.discovery.watch.enabled：false
		 * 
		 * NacosWatch有一个Map<String, EventListener> listenerMap保存着监听器
		 * 		key为：<serviceName>:<group>（即：本app的配置文件中配置的的group和serviceName）
		 * 		value为：EventListener，下面介绍
		 * 
		 * NacosWatch会初始化一个定时任务，定时间隔默认为30秒，配置在NacosDiscoveryProperties
		 * 		spring.cloud.nacos.discovery.watchDelay：30000
		 * 		调用nameService实例的方法监听本app的配置文件中配置的的group和serviceName对应的实例
		 * 
		 * 定时任务内容：
		 * 		1：每次都会调用nameService实例的方法注册监听（可以在断开后自动恢复）
		 * 		2：如果通过group和serviceName从nacos获取到的serviceInstances中有本实例（即：IP和Port和本实例匹配）
		 * 			则从nacos注册中心获取metaData数据和自己的metaData进行对比，不同则set到自己的metadata中
		 * 			即NacosDiscoveryProperties.metadata，对应配置为spring.cloud.nacos.discovery.metadata
		 * 		3：发送一个心跳事件：this.publisher.publishEvent(new HeartbeatEvent(this, nacosWatchIndex.getAndIncrement()));
		 * 
		 */
	}
	
	public void NacosServiceDiscovery() {
		/**
		 * 可以理解为Nacos服务发现和SpringCloud服务发现之间的adatper
		 * 因为nacos和springcloud的discovery还是有些区别的，例如：
		 * 		1：group的概念在springcloud中是不存在的
		 * 		2：springcloud中使用的服务实例是ServiceInstance，Nacos中是Instance，需要一些转换
		 * 
		 * NacosDiscoveryAutoConfiguration中创建
		 * 初始化，需要：
		 * 		NacosDiscoveryProperties discoveryProperties：Nacos的配置文件
		 * 			这里的作用1：获取nameService实例时，需要提供NacosDiscoveryProperties
		 * 			这里的作用2：获取配置的本实例的group，用于调用nameService获取对应group下的serviceInstance
		 * 		NacosServiceManager nacosServiceManager：nacos统一管理类
		 * 			这里的作用1：传递NacosDiscoveryProperties创建或者获取nameService实例
		 * 
		 * List<ServiceInstance> getInstances(String serviceId)：
		 * 		从NacosDiscoveryProperties获取本服务的group调用namingService的selectInstances(serviceId, group, true)方法
		 * 			在本group中找到对应的服务实例列表，true标识监听该服务的变化
		 * 
		 * List<String> getServices()：
		 * 		从NacosDiscoveryProperties获取本服务的group调用namingService的getServicesOfServer(1,Integer.MAX_VALUE, group)方法
		 * 			在本group中找到找到所有的服务serviceId，1表示第一页，Integer.MAX_VALUE表示该页显示无数条
		 * 
		 * List<ServiceInstance> hostToServiceInstanceList(List<Instance> instances, String serviceId)：
		 * 		将Nacos的服务信息列表转换为SpringCloud的服务信息列表
		 * 
		 */
	}
	
	public void NacosServiceManager() {
		/**
		 * 
		 * nacos顶层的统一管理类
		 * 
		 * NacosServiceAutoConfiguration中创建
		 * 
		 * 创建条件：
		 * 		1：@ConditionalOnDiscoveryEnabled
		 * 		2：@ConditionalOnNacosDiscoveryEnabled
		 * 
		 * 原理和作用：
		 * 		1：创建nameService或者NamingMaintainService
		 * 		2：监听事件，即：本实例注册到Nacos注册中心前，获取nacosDiscoveryPropertiesCache并缓存
		 * 		3：如果NacosDiscoveryProperties发生变更，则更新nacosDiscoveryPropertiesCache缓存
		 * 		4：nacosServiceShutDown时，调用namingService或者NamingMaintainService执行shutDown()方法
		 */
	}
	
	public void NacosNamingService() {
		/**
		 * nacosClient和nacos注册中心交互的核心类
		 * 
		 * 使用NacosServiceManager.getNamingService(Properties properties)创建（单例模式）
		 * 因为NacosServiceManager为Spring容器管理的Bean，所以NamingService也只会有一个实例
		 * 
		 * 持有：
		 * 		ServiceInfoHolder serviceInfoHolder
		 * 		InstancesChangeNotifier changeNotifier
		 * 		NamingClientProxy clientProxy
		 * 
		 * 作用：
		 * 		registerInstance：注册实例
		 * 		deregisterInstance：反注册实例
		 * 		getAllInstances：获取某个group、某个cluster的所有实例
		 * 			cluster：不填写默认为：new ArrayList<String>()
		 * 			group：不填写默认为："DEFAULT_GROUP"
		 * 			subscribe：不填写默认为：true
		 * 		selectInstances：获取名为<serviceName>的实例列表
		 * 			cluster：不填写默认为：new ArrayList<String>()
		 * 			group：不填写默认为："DEFAULT_GROUP"
		 * 			subscribe：不填写默认为：true
		 * 		selectOneHealthyInstance：获取一个health status为true的实例
		 * 			会调用：Balancer.RandomByWeight的selectHost(ServiceInfo dom)方法
		 * 			参数：
		 * 				cluster：不填写默认为：new ArrayList<String>()
		 * 				group：不填写默认为："DEFAULT_GROUP"
		 * 				subscribe：不填写默认为：true
		 * 		subscribe：监听某个serviceName变化，会调用：changeNotifier.registerListener(...)、clientProxy.subscribe(...)
		 * 			cluster：不填写默认为：new ArrayList<String>()
		 * 			group：不填写默认为："DEFAULT_GROUP"
		 * 		unsubscribe：取消监听某个serviceName变化，会调用：changeNotifier.deregisterListener(...)、clientProxy.unsubscribe(...)
		 * 			cluster：不填写默认为：new ArrayList<String>()
		 * 			group：不填写默认为："DEFAULT_GROUP"
		 * 		getSubscribeServices：获取所有监听的List<ServiceInfo>
		 * 		getServicesOfServer：获取服务信息list(分页)，返回结果为：ListView<String>
		 * 			group：不填写默认为："DEFAULT_GROUP"
		 * 			AbstractSelector：不填写默认为：null
		 * 		getServerStatus()：获取nacos注册中心的状态
		 * 		shutDown()：关闭本实例
		 * 			serviceInfoHolder.shutdown()
		 * 			clientProxy.shutdown()
		 * 				beatReactor.shutdown()
		 * 				NamingHttpClientManager.getInstance().shutdown()
		 * 				SpasAdapter.freeCredentialInstance()
		 * 
		 * getAllInstances、selectInstances、selectOneHealthyInstance：
		 * 		如果subscribe为true：
		 * 			先调用serviceInfoHolder.getServiceInfo(...)获取serviceInfo
		 * 			如果serviceInfo==null，则clientProxy.subscribe(...)获取serviceInfo
		 * 		如果subscribe为true：
		 * 			直接调用clientProxy.queryInstancesOfService(...)获取serviceInfo
		 * 			
		 */
	}
	
	public void NacosNamingMaintainService() {
		
	}
	
	public void ServiceInfoHolder() {
		/**
		 * serviceInfo的持有者
		 * 每次客户端从注册中心获取新的服务信息时都会调用该类
		 * 其中processServiceInfo方法来进行本地化处理，包括更新缓存服务、发布事件、更新本地文件等
		 * 
		 * ServiceInfoHolder(String namespace, Properties properties)
		 * 本实例持有一个serviceInfoMap类变量
		 * 		key为：<groupName>@@<serviceName> 或者 <groupName>@@<serviceName>@@<clusterName>
		 * 		value为：ServiceInfo
		 * 	
		 * 功能：
		 * 		1：缓存ServiceInfo
		 * 		2：判断ServiceInfo是否更新
		 * 		3：发起写入本地缓存
		 * 		4：发布变更事件
		 * 		除了这些核心功能以外，该类在实例化的时候，还做了本地缓存目录初始化、故障转移初始化等操作
		 * 
		 * 本地缓存：
		 * 		如果配置NacosDiscoveryProperties.namingLoadCacheAtStart：true才会开启
		 * 		默认为false
		 * 		配置参数为namingCacheRegistryDir，默认是没有配置的
		 * 		完整路径cacheDir为：System.getProperty("user.home")+"/nacos"+<namingCacheRegistryDir>+"naming"+<namespace>（在ServiceInfoHolder.initCacheDir方法中）
		 * 		保存：
		 * 			1：把ServiceInfoHolder的serviceInfoMap写入本地
		 * 			2：FailoverReactor的保存路径failoverDir为：<cacheDir>+"/failover"
		 * 
		 * FailoverReactor：
		 * 		初始化时，创建3个定时任务，
		 * 		定时任务1：每5秒执行一次
		 * 			File switchFile = new File(failoverDir + "00-00---000-VIPSRV_FAILOVER_SWITCH-000---00-00");
		 * 			里面写入：failover-mode：true 或者failover-mode：false
		 * 			根据文件内的信息修改本实例持有的一个Map<String, String> switchParams
		 * 				switchParams.put(FAILOVER_MODE_PARAM, Boolean.FALSE.toString());
		 * 			如果为true，则从磁盘读取ServiceInfo并写入到FailoverReactor.serviceMap中
		 * 		定时任务2：每30分钟执行一次
		 * 			将Map<String, ServiceInfo> map = serviceInfoHolder.getServiceInfoMap()写入本地文件
		 * 		定时任务3：每10秒执行一次
		 * 			检查File cacheDir = new File(failoverDir);是否存在，如果不存在执行一次<定时任务1>
		 * 
		 * getServiceInfo(final String serviceName, final String groupName, final String clusters)方法：
		 * 		serviceName：不能为空
		 * 		groupName：不能为空
		 * 		如果cluster为空key则为： <groupName>@@<serviceName>
		 * 		如果clusters不为空key则为：<groupName>@@<serviceName>@@<clusterName>
		 * 		如果FailoverReactor.switchParams中是true则从FailoverReactor.serviceMap读写ServiceInfo
		 * 			否则从自己的serviceInfoMap中读取ServiceInfo
		 * 		
		 */
		
	}
	
	public void NamingClientProxyDelegate() {
		/**
		 * 与nacos注册中心交互的底层类
		 * 
		 * 初始化：
		 * NacosNamingService创建的时候，会new NamingClientProxyDelegate(this.namespace, serviceInfoHolder, properties, changeNotifier)
		 * 		this.changeNotifier = new InstancesChangeNotifier()：负责订阅实例变更事件InstancesChangeEvent
		 * 		NotifyCenter.registerToPublisher(InstancesChangeEvent.class, 16384);
		 * 		NotifyCenter.registerSubscriber(changeNotifier);
		 * 		this.serviceInfoHolder = new ServiceInfoHolder(namespace, properties);
		 * 		this.clientProxy = new NamingClientProxyDelegate(this.namespace, serviceInfoHolder, properties, changeNotifier);
		 * 
		 * ServiceInfoUpdateService：
		 * 		NamingClientProxyDelegate在初始化的时候会创建一个ServiceInfoUpdateService
		 * 		this.serviceInfoUpdateService = new ServiceInfoUpdateService(properties, serviceInfoHolder, this,changeNotifier);
		 * 		ServiceInfoUpdateService会创建一个定时任务
		 * 			定时任务的线程数为配置项namingPollingThreadCount，默认会根据CPU核心数来计算线程数
		 * 
		 * 当NamingClientProxyDelegate执行subscribe(...)方法时，ServiceInfoUpdateService会增加一个执行任务，每秒执行1次
		 * 		该任务会获取nacos的对应serviceName的所有实例信息，并更新到ServiceInfoHolder
		 * 
		 * 该实例会调用grpcClientProxy或者httpClientProxy来实际调用nacos注册中心
		 * 		registerService和deregisterService，对于临时节点用grpcClientProxy，否则用httpClientProxy
		 * 		queryInstancesOfService，用grpcClientProxy
		 * 		getServiceList，用grpcClientProxy
		 * 		subscribe和unsubscribe，用grpcClientProxy
		 * 		updateBeatInfo，httpClientProxy
		 * 
		 */
	}
	
	public void InstancesChangeNotifier() {
		/**
		 * 实例变更通知器
		 * 
		 * 初始化：
		 * 		NacosNamingService构造函数中，会创建InstancesChangeNotifier并与InstancesChangeEvent绑定
		 * 
		 * 哪里会用1：
		 * 		NacosNamingService构造函数中，会创建NamingClientProxyDelegate，会把InstancesChangeNotifier传递过去
		 * 		NamingClientProxyDelegate构造函数中，会创建ServiceInfoUpdateService，会把InstancesChangeNotifier传递过去
		 * 		ServiceInfoUpdateService会通过changeNotifier.isSubscribed(groupName, serviceName, clusters)判断是否有监听
		 * 		该服务的InstancesChangeEvent来决定是否来执行后续更新serviceInfoHolder的操作，如果没有监听该服务的
		 * 		InstancesChangeEvent，则不会执行后续更新serviceInfoHolder的操作
		 * 哪里会用2：
		 * 		NacosNamingService本实例中的如下方法会使用：
		 * 			subscribe会执行：
		 * 				changeNotifier.registerListener...
		 * 				namingClientProxyDelegate.subscribe...这里调用serviceInfoUpdateService来持续更新serviceInfoHolder
		 * 			unsubscribe会执行：
		 * 				参考subscribe
		 */
	}
	
	public void DiscoveryClientHealthIndicator() {
		/**
		 * 从注册中心获取服务列表，能获取到的所有serviceName就都是健康的
		 * 
		 * 会调用NacosDiscoveryClient.getServices()
		 */
	}
	
	public void DiscoveryClientRouteDefinitionLocator() {
		/**
		 * 该实例自动获取注册在注册中心的服务列表，生成对应的 RouteDefinition
		 * 
		 * 初始化：
		 * 		GatewayDiscoveryClientAutoConfiguration.ReactiveDiscoveryClientRouteDefinitionLocatorConfiguration
		 * 		初始化条件：spring.cloud.gateway.discovery.locator.enabled，默认为false
		 * 		有两个构造参数：
		 * 			DiscoveryLocatorProperties
		 * 			ReactiveDiscoveryClient，对应的nacos实现为NacosReactiveDiscoveryClient
		 * 
		 * 在构造函数中，会调用discoveryClient.getServices()获取serviceName列表，并逐个访问获取每个serviceName对应的实例列表
		 */
	}
	
}
