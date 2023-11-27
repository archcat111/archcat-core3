package org.cat.support.springboot3.starter.db.sharding;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.driver.governance.api.GovernanceShardingSphereDataSourceFactory;
import org.apache.shardingsphere.governance.repository.api.config.GovernanceConfiguration;
import org.apache.shardingsphere.governance.repository.api.config.RegistryCenterConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.database.DefaultSchema;
import org.apache.shardingsphere.readwritesplitting.api.ReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.rule.ReadwriteSplittingDataSourceRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.HintShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.ShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.support.db3.generator.SupportDb3ConditionalFlag;
import org.cat.support.db3.generator.constants.DataSourceConstants.Type;
import org.cat.support.db3.generator.constants.ShardingConstants;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder;
import org.cat.support.db3.generator.sharding.ShardingDataSourceHolder;
import org.cat.support.db3.generator.sharding.ShardingDataSourceHolder.ShardingDataSourceProps;
import org.cat.support.db3.generator.sharding.ShardingException;
import org.cat.support.db3.generator.sharding.ShardingRegisterCenterHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import com.google.common.collect.Maps;

@Configuration
@ConditionalOnClass(SupportDb3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.db.sharding-sphere", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingAutoConfiguration {
	
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	protected final transient String logPrefix = "ShardingSphere数据源初始化：";
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ShardingProperties shardingProperties;
	@Autowired
	private DruidDataSourceHolder druidDataSourceHolder;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月17日 下午5:45:21
	 * @version 1.0
	 * @description 当初始化多个注册中心时，如果有问题，并不会报错，而是会尝试初始化其他的注册中心
	 * 		如果所有的注册中心都初始化失败，则会返回一个空的ShardingRegisterCenterHolder
	 * @return
	 */
	@ConditionalOnClass(GovernanceConfiguration.class)
	@ConditionalOnMissingBean(ShardingRegisterCenterHolder.class)
	@Bean
	public ShardingRegisterCenterHolder shardingRegisterCenterHolder() {
		//创建一个空的ShardingRegisterCenterHolder
		ShardingRegisterCenterHolder shardingRegisterCenterHolder = new ShardingRegisterCenterHolder();
		
		Map<String, ShardingRegisterCenterProperties> shardingRegisterCenterPropertiesMapper = this.shardingProperties.getRegisterCenters();
		
		//如果没有配注册中心，则注册一个空的ShardingRegisterCenterHolder
		if(MapUtils.isEmpty(shardingRegisterCenterPropertiesMapper)) {
			this.coreLogger.info(logPrefix + "您没有配置registerCenters，将采用标准模式初始化ShardingDataSource");
			return shardingRegisterCenterHolder;
		}
		
		//初始化每一个注册中心
		shardingRegisterCenterPropertiesMapper.forEach((registerCenterName, shardingRegisterCenterProperties) -> {
			//开关
			boolean enabled = shardingRegisterCenterProperties.isEnabled();
			if(!enabled) {//RegisterCluster开关为关闭状态，则不初始化该注册中心
				this.coreLogger.info(logPrefix + "registerCenter["+registerCenterName+"]当前开关状态为关闭，放弃初始化该注册中心");
				return;
			}
			
			//注册中心类型
			String type = shardingRegisterCenterProperties.getType();
			if(StringUtils.isBlank(type)) {//如果用户没有配置注册中心或者填写了空，则默认按照auto进行初始化
				this.coreLogger.info(logPrefix + "registerCenter["+registerCenterName+"]没有配置type，系统将根据情况进行自适应初始化");
				type = ShardingConstants.RegisterCenterName.AUTO;
			}
			if(ShardingConstants.RegisterCenterName.ZOOKEEPER.equalsIgnoreCase(type)) {
				if(!ClassUtils.isPresent(ShardingConstants.RegisterCenterClassName.ZOOKEEPER, null)) {
					this.coreLogger.info(logPrefix + "RegisterCenter["+registerCenterName+"]当前配置类型为Zookeeper，但未找到对应的CuratorZookeeperRepository，放弃初始化该注册中心");
					return;
				}
			}else if(ShardingConstants.RegisterCenterName.ETCD.equalsIgnoreCase(type)) {
				if(!ClassUtils.isPresent(ShardingConstants.RegisterCenterClassName.ETCD, null)) {
					this.coreLogger.info(logPrefix + "RegisterCenter["+registerCenterName+"]当前配置类型为Etcd，但未找到对应的EtcdRepository，放弃初始化该注册中心");
					return;
				}
			}else if(ShardingConstants.RegisterCenterName.AUTO.equalsIgnoreCase(type)) {
				if(!ClassUtils.isPresent(ShardingConstants.RegisterCenterClassName.ZOOKEEPER, null) 
						&& !ClassUtils.isPresent(ShardingConstants.RegisterCenterClassName.ETCD, null)) {
					this.coreLogger.warn(logPrefix + "RegisterCenter["+registerCenterName+"]当前配置类型为Auto，但系统中没有任何可用的Repository实现，放弃初始化该注册中心");
					return;
				}
			}else {
				this.coreLogger.warn(logPrefix + "RegisterCluster["+registerCenterName+"]type["+type+"]初始化失败");
				return;
			}
			
			//serverLists
			String serverLists = shardingRegisterCenterProperties.getServerLists();
			
			//其他属性
			Properties properties = new Properties();
			properties.putAll(shardingRegisterCenterProperties.getProps());
			
			//是否覆盖注册中心里的已有配置
			boolean overWrite = shardingRegisterCenterProperties.isOverWrite();
			
			RegistryCenterConfiguration registryCenterConfig = new RegistryCenterConfiguration(type, serverLists, properties);
			GovernanceConfiguration governanceConfiguration = new GovernanceConfiguration(registerCenterName, registryCenterConfig, overWrite);

			shardingRegisterCenterHolder.put(registerCenterName, governanceConfiguration);
		});
		
		return shardingRegisterCenterHolder;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月22日 下午3:17:27
	 * @version 1.0
	 * @description 创建一个ShardingDataSourceHolder，持有多个ShardingDataSource
	 * 		该方法会在创建ShardingDataSource时从druidDataSourceHolder尝试获取
	 * 		该方法会在创建ShardingDataSource时会根据是否有配置registerCenters名称而使用不同的Factory来创建ShardingDataSource
	 * @param shardingRegisterCenterHolder
	 * @return
	 */
	@ConditionalOnMissingBean(ShardingDataSourceHolder.class)
	@Bean
	public ShardingDataSourceHolder shardingDataSourceHolder(ShardingRegisterCenterHolder shardingRegisterCenterHolder) {
		//初始化一个空的ShardingDataSourceHolder
		ShardingDataSourceHolder shardingDataSourceHolder = new ShardingDataSourceHolder();
		
		//获取多个Sharding数据源的配置，每个Sharding数据源就是用户真实使用的数据源，可以由Mybatis集成
		Map<String, ShardingDataSourceProperties> shardingDataSourcePropertiesMapper = this.shardingProperties.getShardingDataSources();
		//如果没有配置任何数据源，则返回一个空的ShardingDataSourceHolder
		if(MapUtils.isEmpty(shardingDataSourcePropertiesMapper)) {
			this.coreLogger.warn(logPrefix + "没有配置任何数据源，如非本意，请检查配置");
			return shardingDataSourceHolder;
		}
		
		//遍历所有的数据源配置，创建数据源，这些数据源会被添加到Spring容器中，同时会添加到ShardingDataSourceHolder中
		shardingDataSourcePropertiesMapper.forEach((shardingDataSourceName, shardingDataSourceProperties) -> {
			boolean enabled = shardingDataSourceProperties.isEnabled();
			if(enabled) {
				this.coreLogger.info(logPrefix + "准备初始化shardingSphere数据源："+shardingDataSourceName);
				DataSource shardingDataSource = shardingDataSource(shardingDataSourceName, shardingDataSourceProperties, shardingRegisterCenterHolder);
				shardingDataSourceHolder.put(shardingDataSourceName, shardingDataSource);
				this.manageProps(shardingDataSourceHolder, shardingDataSourceName, shardingDataSourceProperties);
				this.coreLogger.info(logPrefix + "完成初始化shardingSphere数据源："+shardingDataSourceName);
			}else {
				this.coreLogger.info(logPrefix + "shardingSphere数据源["+shardingDataSourceName+"]的开关为关闭，放弃初始化该数据源");
			}
		});
		return shardingDataSourceHolder;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月22日 下午3:14:25
	 * @version 1.0
	 * @description 创建一个SharingDataSource，并注入到Spring容器中 
	 * @param shardingDataSourceName
	 * @param shardingDataSourceProperties
	 * @param shardingRegisterCenterHolder
	 * @return
	 */
	private DataSource shardingDataSource(
			String shardingDataSourceName, 
			ShardingDataSourceProperties shardingDataSourceProperties,
			ShardingRegisterCenterHolder shardingRegisterCenterHolder) {
		
		if(applicationContext.containsBean(shardingDataSourceName)) {
			throw new IllegalArgumentException(this.logPrefix + "Spring容器中已经包含名为["+shardingDataSourceName+"]的Bean，请更换数据源名称");
		}
		
		//构建数据源Map，用于最终创建ShardingDataSource
		//<该数据源在该Sharding数据源中的别名>: DruidDataSource
		//这些DruidDataSource数据源就是在该ShardingDataSource需要使用到的原始数据源
		Map<String, DataSource> dataSourceMap = Maps.newHashMap();
		
		//该Sharding数据源涉及到的DruidDataSource
		//<真实数据源在该Sharding数据源中的别名>: <真实的配置了JDBC连接的数据源>
		Map<String, String> shardingDataSourceMap = shardingDataSourceProperties.getDataSources();
		if(MapUtils.isEmpty(shardingDataSourceMap)) {
			throw new IllegalArgumentException(this.logPrefix + "数据源["+shardingDataSourceName+"]代理的实际数据源配置为空，请检查配置");
		}
		
		//该配置标识了该Sharding数据源：是否是读写分离，是否是分库分表
		ShardingDataSourceTypeProperties shardingDataSourceTypeProperties = shardingDataSourceProperties.getDataSourceType();
		boolean isReadwriteSplitting = shardingDataSourceTypeProperties.isReadwriteSplitting();
		boolean isSharding = shardingDataSourceTypeProperties.isSharding();
		
		//判断数据源是是否是读写分离
		if(isReadwriteSplitting) { 
			ShardingDataSourceRwProperties shardingDataSourceRwProperties = shardingDataSourceProperties.getReadwriteSplitting();
			dataSourceMap = createRwDataSourceMapper(shardingDataSourceName, shardingDataSourceMap, shardingDataSourceRwProperties);
		}else { //普通数据源
			dataSourceMap = createNotRwDataSourceMapper(shardingDataSourceName, shardingDataSourceMap);
		}
		
		///////////////////////////////构建规则，该规则包括：读写分离、分库分表///////////////////////////////////////
		//构建规则Map，用于最终创建ShardingDataSource
		Collection<RuleConfiguration> ruleConfigurations = Lists.newArrayList();
		
		///////////////////////////////构建读写分离负载均衡规则///////////////////////////////////////
		if(isReadwriteSplitting) {
			this.coreLogger.info(this.logPrefix + "创建shardingSphere数据源["+shardingDataSourceName+"]读写分离：初始化开始...");
			ShardingDataSourceRwProperties shardingDataSourceRwProperties = shardingDataSourceProperties.getReadwriteSplitting();
			
			//创建负载均衡
			Map<String, ShardingSphereAlgorithmConfiguration> rwLoadBalancers = createRwLoadBalancerMapper(shardingDataSourceName, shardingDataSourceRwProperties);
			//创建数据源读写分离rule
			Collection<ReadwriteSplittingDataSourceRuleConfiguration> rwDataSources = createRwRuleCollection(shardingDataSourceName, shardingDataSourceRwProperties);
			
			ReadwriteSplittingRuleConfiguration readwriteSplittingRuleConfiguration = new ReadwriteSplittingRuleConfiguration(rwDataSources, rwLoadBalancers);
			
			ruleConfigurations.add(readwriteSplittingRuleConfiguration);
			this.coreLogger.info(this.logPrefix + "创建shardingSphere数据源["+shardingDataSourceName+"]读写分离：初始化完成...");
		}
		
		///////////////////////////////构建分库分表算法及规则///////////////////////////////////////
		if(isSharding) {
			this.coreLogger.info(this.logPrefix + "创建shardingSphere数据源["+shardingDataSourceName+"]分库分表：初始化开始...");
			ShardingDataSourceShardingProperties shardingDataSourceShardingProperties = shardingDataSourceProperties.getSharding();
			
			//shardingRuleConfig中包含该Sharding数据源中涉及到分库分表的所有算法、所有表、所有表与算法之间的关联关系
			ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
			//构建分库分表算法
			Map<String, ShardingSphereAlgorithmConfiguration> shardingAlgorithmMapper = createShardingAlgorithmMapper(shardingDataSourceName, shardingDataSourceShardingProperties);
			shardingRuleConfig.getShardingAlgorithms().putAll(shardingAlgorithmMapper);
			//构建分库分表规则以及引用的算法名称
			Collection<ShardingTableRuleConfiguration> shardingTableRuleCollection = createShardingTableRuleCollection(shardingDataSourceName, shardingDataSourceShardingProperties);
			shardingRuleConfig.getTables().addAll(shardingTableRuleCollection);
			
			ruleConfigurations.add(shardingRuleConfig);
			this.coreLogger.info(this.logPrefix + "创建shardingSphere数据源["+shardingDataSourceName+"]分库分表：初始化完成...");
		}
		
		
		DataSource dataSource = null;
		String shardingSchema = DefaultSchema.LOGIC_NAME;
		String registerCenters = shardingDataSourceProperties.getRegisterCenters();
		if(StringUtils.isBlank(registerCenters)) {//数据源没有配置注册中心
			this.coreLogger.info("创建shardingSphere数据源["+shardingDataSourceName+"]没有配置注册中心，将采用标准数据源方式进行初始化");
			dataSource = createNormalDataSource(shardingDataSourceName, shardingSchema, dataSourceMap, ruleConfigurations);
		}else {//数据源配置了注册中心
			this.coreLogger.info("创建shardingSphere数据源["+shardingDataSourceName+"]有配置注册中心，将采用分布式数据源方式进行初始化");
			dataSource = createGovernanceDataSource(shardingDataSourceName, dataSourceMap, ruleConfigurations, registerCenters, shardingRegisterCenterHolder);
		}
		
		//注入到Spring容器
		AutowireCapableBeanFactory autowireCapableBeanFactory = this.applicationContext.getAutowireCapableBeanFactory();
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) autowireCapableBeanFactory;
		defaultListableBeanFactory.registerSingleton(shardingDataSourceName, dataSource);
		return applicationContext.getBean(shardingDataSourceName, DataSource.class);
	}
	

	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月13日 下午5:01:52
	 * @version 1.0
	 * @description 根据ShardingDataSourceRwProperties创建一个SharingDataSource下的多个实际数据源的Map 
	 * 最终构建出来的Map：
	 * 		Key：Sharding的读写配置中配置的该数据源在Sharding中的别名，例如：shardingUserW10DS
	 * 		Value：真实数据源源的实例，例如：DruidDataSource
	 * @param shardingDataSourceName 用户真实使用的多个Sharding数据源中的一个的名称
	 * @param shardingDataSourceMap 配置中配置的该Sharding数据源涉及到的真实数据源
	 * @param shardingDataSourceRwProperties 该Sharding数据源的读写分离配置
	 * @return
	 */
	private Map<String, DataSource> createRwDataSourceMapper(String shardingDataSourceName, 
			Map<String, String> shardingDataSourceMap, ShardingDataSourceRwProperties shardingDataSourceRwProperties) {
		Map<String, DataSource> rwDataSourceMap = Maps.newHashMap();
		
		Map<String, ShardingDataSourceRwRuleProperties> shardingDataSourceRwRulePropertiesMapper = shardingDataSourceRwProperties.getDataSourcesRwRule();
		//判断是否有dataSourcesRwRule下面的配置
		if(MapUtils.isEmpty(shardingDataSourceRwRulePropertiesMapper)) {
			throw new IllegalArgumentException(this.logPrefix + "数据源["+shardingDataSourceName+"][读写分离]代理的实际数据源规则配置为空，请检查配置");
		}
		
		//该sharding数据源中的其中一个读写库数据源，有可能会有多个读写库，例如分片的时候
		shardingDataSourceRwRulePropertiesMapper.forEach((rwShardingDataSourceShardingName, shardingDataSourceRwRuleProperties) -> {
			boolean enabled = shardingDataSourceRwRuleProperties.isEnabled();
			if(enabled) {
				//获取主写库配置
				String wShardingDataSourceAliasName = shardingDataSourceRwRuleProperties.getWriteDataSourceName();
				if(StringUtils.isBlank( wShardingDataSourceAliasName)) {
					throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]分库数据源["+rwShardingDataSourceShardingName+"]的写库为空");
				}
				String wDruidDataSourceName = shardingDataSourceMap.get(wShardingDataSourceAliasName.trim());
				DataSource wDruidDataSource = this.druidDataSourceHolder.get(wDruidDataSourceName.trim());
				//如果对应的写库没有可用的DruidDataSource，则抛出异常
				if(wDruidDataSource == null) {
					throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]分库数据源["+rwShardingDataSourceShardingName+"]的写库["+wShardingDataSourceAliasName.trim()+"]未找到可用的实例");
				}
				rwDataSourceMap.put(wShardingDataSourceAliasName, wDruidDataSource);
				String[] rShardingDataSourceAliasNameArray = shardingDataSourceRwRuleProperties.getReadDataSourceNames().split(",");
				//如果没有配置读库配置则进行警告
				if(rShardingDataSourceAliasNameArray.length == 0) {
					this.coreLogger.warn(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]分库数据源["+rwShardingDataSourceShardingName+"]的读库列表配置为空，将会使用写库进行读取");
				}
				for (int i = 0; i < rShardingDataSourceAliasNameArray.length; i++) {
					String rShardingDataSourceAliasName = rShardingDataSourceAliasNameArray[i].trim();
					String rDruidDataSourceName = shardingDataSourceMap.get(rShardingDataSourceAliasName.trim());
					DataSource rDruidDataSource = this.druidDataSourceHolder.get(rDruidDataSourceName.trim());
					//如果对应的读库没有可用的DruidDataSource，则抛出异常
					if(rDruidDataSource == null) {
						throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]分库数据源["+rwShardingDataSourceShardingName+"]的读库["+rShardingDataSourceAliasName.trim()+"]未找到可用的实例");
					}
					rwDataSourceMap.put(rShardingDataSourceAliasName, rDruidDataSource);
				}
			}else {
				this.coreLogger.info(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]分库数据源["+rwShardingDataSourceShardingName+"]的开关为关闭，放弃初始化该数据源");
			}
		});
		
		//如果配置了数据源，但是所有数据源的开关都是关闭的，则抛出异常
		if(MapUtils.isEmpty(rwDataSourceMap)) {
			throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]中dataSourcesRwRule配置的所有分片数据源的开关都是关闭的，请检查配置");
		}
		return rwDataSourceMap;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月13日 下午5:32:44
	 * @version 1.0
	 * @description 根据ShardingDataSourceRwProperties创建一个SharingDataSource下的多个实际数据源的Map 
	 * 最终构建出来的Map：
	 * 		Key：Sharding的读写配置中配置的该数据源在Sharding中的别名，例如：shardingUserW10DS
	 * 		Value：真实数据源源的实例，例如：DruidDataSource
	 * @param shardingDataSourceName 用户真实使用的多个Sharding数据源中的一个的名称
	 * @param dataSourceNamesMap 配置中配置的该Sharding数据源涉及到的真实数据源
	 * @return
	 */
	private Map<String, DataSource> createNotRwDataSourceMapper(String shardingDataSourceName, Map<String, String> dataSourceNamesMap){
		Map<String, DataSource> notRwDataSourceMapper = Maps.newHashMap();
		
		dataSourceNamesMap.forEach((shardingDataSourceAliasName, druidDataSourceName) -> {
			DataSource druidDataSource = this.druidDataSourceHolder.get(druidDataSourceName.trim());
			if(druidDataSource==null) {
				throw new IllegalArgumentException(logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][非读写分离]时，["+shardingDataSourceAliasName.trim()+"]未找到可用的实例");
			}else {
				notRwDataSourceMapper.put(shardingDataSourceAliasName, druidDataSource);
			}
		});
		
		if(MapUtils.isEmpty(notRwDataSourceMapper)) {
			throw new IllegalArgumentException("创建shardingSphere数据源["+shardingDataSourceName+"]时，您配置的数据源列表为["+dataSourceNamesMap+"]，但没有任何一个可用数据源，请检查");
		}
		return notRwDataSourceMapper;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月23日 下午5:04:58
	 * @version 1.0
	 * @description 创建读写分离中的读库负载均衡算法Mapper 
	 * @param shardingDataSourceName
	 * @param shardingDataSourceRwProperties
	 * @return Map<String, ShardingSphereAlgorithmConfiguration>
	 * 		key：负载均衡算法的名称（在RwRule中会使用该名称）
	 * 		value：负载均衡算法的实现配置，ShardSphere会根据该配置加载具体的负载均衡实现
	 */
	private Map<String, ShardingSphereAlgorithmConfiguration> createRwLoadBalancerMapper(String shardingDataSourceName, ShardingDataSourceRwProperties shardingDataSourceRwProperties) {
		Map<String, ShardingSphereAlgorithmConfiguration> rwLoadBalancers = Maps.newHashMap();
		
		Map<String, ShardingDataSourceRwLoadBalancerProperties> shardingDataSourceRwLoadBalancerPropertiesMapper = shardingDataSourceRwProperties.getLoadBalancers();
		//如果没有配置读写分离的负载均衡策略配置，则抛出异常
		if(MapUtils.isEmpty(shardingDataSourceRwLoadBalancerPropertiesMapper)) {
			throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]的负载均衡策略配置不能为空");
		}
		shardingDataSourceRwLoadBalancerPropertiesMapper.forEach((loadBalancerName, shardingDataSourceRwLoadBalancerProperties) -> {
			boolean enabled = shardingDataSourceRwLoadBalancerProperties.isEnabled();
			if(enabled) {
				String loadBalanceType = shardingDataSourceRwLoadBalancerProperties.getType();
				Map<String, String> propsMapper = shardingDataSourceRwLoadBalancerProperties.getProps();
				Properties loadBalanceProps = new Properties();
				loadBalanceProps.putAll(propsMapper);
				ShardingSphereAlgorithmConfiguration shardingSphereAlgorithmConfiguration = new ShardingSphereAlgorithmConfiguration(loadBalanceType, loadBalanceProps);
				rwLoadBalancers.put(loadBalancerName, shardingSphereAlgorithmConfiguration);
				this.coreLogger.info(this.logPrefix + "创建shardingSphere数据源["+shardingDataSourceName+"][读写分离]初始化进行中，初始化负载均衡策略["+loadBalancerName+"]");
			}
		});
		
		//如果没有配置负载均衡策略，则默认使用轮询
		if(MapUtils.isEmpty(rwLoadBalancers)) {
			String loadBalanceType = ShardingConstants.RwLoadBalanceType.ROUND_ROBIN;
			Properties loadBalanceProps = new Properties();
			ShardingSphereAlgorithmConfiguration shardingSphereAlgorithmConfiguration = new ShardingSphereAlgorithmConfiguration(loadBalanceType, loadBalanceProps);
			rwLoadBalancers.put(ShardingConstants.RwLoadBalanceName.DEFAULT, shardingSphereAlgorithmConfiguration);
		}
		return rwLoadBalancers;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月23日 下午5:06:53
	 * @version 1.0
	 * @description 创建读写分离中的读写规则 
	 * @param shardingDataSourceName
	 * @param shardingDataSourceRwProperties
	 * @return	ReadwriteSplittingDataSourceRuleConfiguration
	 * 		包括：
	 * 			1：该数据源涉及到的shardingDataSourceAliasName是什么，在分库分表中会使用该名称
	 * 			2：写库对应的DruidDataSource数据源的名称是什么
	 * 			3：读库对应的DruidDataSource数据源的名称是什么
	 * 			4：读库负载均衡应用的算法名称是什么
	 */
	private Collection<ReadwriteSplittingDataSourceRuleConfiguration> createRwRuleCollection(String shardingDataSourceName, ShardingDataSourceRwProperties shardingDataSourceRwProperties) {
		Collection<ReadwriteSplittingDataSourceRuleConfiguration> rwDataSourceCollection = Lists.newArrayList();
		
		Map<String, ShardingDataSourceRwRuleProperties> dataSourcesRwRuleMapper = shardingDataSourceRwProperties.getDataSourcesRwRule();
		//判断是否有dataSourcesRwRule下面的配置
		if(MapUtils.isEmpty(dataSourcesRwRuleMapper)) {
			throw new IllegalArgumentException(this.logPrefix + "数据源["+shardingDataSourceName+"][读写分离]代理的实际数据源规则配置为空，请检查配置");
		}
				
		dataSourcesRwRuleMapper.forEach((rwShardingDataSourceShardingName, shardingDataSourceRwRuleProperties) -> {
			boolean enabled = shardingDataSourceRwRuleProperties.isEnabled();
			if(enabled) {
				////获取写库配置
				String wShardingDataSourceAliasName = shardingDataSourceRwRuleProperties.getWriteDataSourceName();
				if(StringUtils.isBlank(wShardingDataSourceAliasName)) {
					throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]分库数据源["+rwShardingDataSourceShardingName+"]的写库为空");
				}
				////获取读库配置
				String rShardingDataSourceAliasNameArrayStr = shardingDataSourceRwRuleProperties.getReadDataSourceNames();
				//如果没有配置读库配置则进行警告
				if(StringUtils.isBlank(rShardingDataSourceAliasNameArrayStr)) {
					this.coreLogger.warn(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]分库数据源["+rwShardingDataSourceShardingName+"]的读库列表配置为空");
				}
				////构建读写数据源Properties
				Properties rwDSProperties = new Properties();
				rwDSProperties.put("write-data-source-name", wShardingDataSourceAliasName);
				rwDSProperties.put("read-data-source-names", rShardingDataSourceAliasNameArrayStr);
				////处理数据源的动态或者静态的问题
				String autoAwareDataSourceName = shardingDataSourceRwRuleProperties.getAutoAwareDataSourceName();
				String type = ShardingConstants.RwType.STATIC;
				if(StringUtils.isNoneBlank(autoAwareDataSourceName)) {
					type = ShardingConstants.RwType.DYNAMIC;
					rwDSProperties.put("auto-aware-data-source-name", autoAwareDataSourceName);
				}
				////负载均衡
				String loadBalancerName = shardingDataSourceRwRuleProperties.getLoadBalancerName();
				//如果没有配置读库的负载均衡器名称，则抛出异常
				if(StringUtils.isBlank(loadBalancerName)) {
					throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]分库数据源["+rwShardingDataSourceShardingName+"]的负载均衡器名称为空");
				}
				////构建ReadwriteSplittingDataSourceRuleConfiguration
				ReadwriteSplittingDataSourceRuleConfiguration readwriteSplittingDataSourceRuleConfiguration = 
						new ReadwriteSplittingDataSourceRuleConfiguration(rwShardingDataSourceShardingName, type, rwDSProperties, loadBalancerName);
				
				rwDataSourceCollection.add(readwriteSplittingDataSourceRuleConfiguration);
				this.coreLogger.info(this.logPrefix + "创建shardingSphere数据源["+shardingDataSourceName+"]读写分离：初始化进行中，["+rwDSProperties.toString()+"]完成");
			}else {
				this.coreLogger.info(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]分库数据源["+rwShardingDataSourceShardingName+"]的开关为关闭，放弃初始化该数据源");
			}
		});
		
		//如果配置了数据源，但是所有数据源的开关都是关闭的，则抛出异常
		if(CollectionUtils.isEmpty(rwDataSourceCollection)) {
			throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][读写分离]中dataSourcesRwRule配置的所有分片数据源的开关都是关闭的，请检查配置");
		}
		return rwDataSourceCollection;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月23日 下午5:19:13
	 * @version 1.0
	 * @description 创建分库分表中的算法Mapper 
	 * @param shardingDataSourceName
	 * @param shardingDataSourceShardingProperties
	 * @return
	 */
	private Map<String, ShardingSphereAlgorithmConfiguration> createShardingAlgorithmMapper(String shardingDataSourceName, ShardingDataSourceShardingProperties shardingDataSourceShardingProperties) {
		Map<String, ShardingSphereAlgorithmConfiguration> shardingAlgorithmMapper = Maps.newHashMap();
		
		Map<String, ShardingDataSourceShardingAlgorithmProperties> shardingDataSourceShardingAlgorithmPropertiesMapper = shardingDataSourceShardingProperties.getShardingAlgorithms();
		if(MapUtils.isEmpty(shardingDataSourceShardingAlgorithmPropertiesMapper)) {
			throw new IllegalArgumentException("创建shardingSphere数据源["+shardingDataSourceName+"]时，发现没有配置分片算法集合，请配置相关参数");
		}
		
		shardingDataSourceShardingAlgorithmPropertiesMapper.forEach((shardingAlgorithmName, shardingDataSourceShardingAlgorithmProperties) -> {
			boolean enabled = shardingDataSourceShardingAlgorithmProperties.isEnabled();
			if(enabled) {
				String algorithmType = shardingDataSourceShardingAlgorithmProperties.getType();
				if(StringUtils.isBlank(algorithmType)) {
					throw new IllegalArgumentException(logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]["+shardingAlgorithmName+"]的type不能为空");
				}
				Map<String, String> algorithmProperties = shardingDataSourceShardingAlgorithmProperties.getProps();
				Properties shardingAlgorithmrProps = new Properties();
				shardingAlgorithmrProps.putAll(algorithmProperties);
				
				shardingAlgorithmMapper.put(shardingAlgorithmName, new ShardingSphereAlgorithmConfiguration(algorithmType, shardingAlgorithmrProps));
			}else {
				this.coreLogger.info(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"]分库分表算法["+shardingAlgorithmName+"]的开关为关闭，放弃初始化该分库分表算法");
			}
			this.coreLogger.info(this.logPrefix + "创建shardingSphere数据源["+shardingDataSourceName+"][分库分表]初始化进行中，初始化分库分表算法["+shardingAlgorithmName+"]完成");
		});
		
		//如果配置了分库分表算法，但是所有分库分表算法的开关都是关闭的，则抛出异常
		if(MapUtils.isEmpty(shardingAlgorithmMapper)) {
			throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"]分库分表算法的开关都是关闭的，请检查配置");
		}
		return shardingAlgorithmMapper;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月23日 下午5:25:27
	 * @version 1.0
	 * @description 创建ShardingSphere中的一个抽象数据库中的多个表的分库分表规则
	 * 		包括：
	 * 			1：t_order表的分库分表形式为ds${0..1}.t_order${0..1}
	 * 			2：分库字段，分库规则Strategy，分库算法名称 
	 * 			3：分表字段，分表规则Strategy，分表算法名称 
	 * @param shardingDataSourceName
	 * @param shardingDataSourceShardingProperties
	 * @return
	 */
	private Collection<ShardingTableRuleConfiguration> createShardingTableRuleCollection(String shardingDataSourceName, ShardingDataSourceShardingProperties shardingDataSourceShardingProperties) {
		Collection<ShardingTableRuleConfiguration> shardingTableRuleCollection = Lists.newArrayList();
		
		Map<String, ShardingDataSourceShardingTablesProperties> shardingDataSourceShardingTablesPropertiesMapper = shardingDataSourceShardingProperties.getShardingTables();
		if(MapUtils.isEmpty(shardingDataSourceShardingTablesPropertiesMapper)) {
			throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]，您没有配置分库分表规则集合，请配置相关参数");
		}
		
		shardingDataSourceShardingTablesPropertiesMapper.forEach((shardingTableName, shardingDataSourceShardingTablesProperties) -> {
			boolean enabled = shardingDataSourceShardingTablesProperties.isEnabled();
			//是否开启该表的分库分表规则
			if(enabled) {
				String actualDataNodes = shardingDataSourceShardingTablesProperties.getActualDataNodes();
				//该表的虚拟节点groovy表达式不能为空
				if(StringUtils.isBlank(actualDataNodes)) {
					throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]，表名["+shardingTableName+"]的actualDataNodes不能为空");
				}
				ShardingTableRuleConfiguration shardingTableRuleConfiguration = new ShardingTableRuleConfiguration(shardingTableName, actualDataNodes);
				
				//该表的分库规则
				ShardingDataSourceShardingTablesDbProperties shardingDataSourceShardingTablesDbProperties = shardingDataSourceShardingTablesProperties.getDbSharding();
				boolean enabled1 = shardingDataSourceShardingTablesDbProperties.isEnabled();
				if(enabled1) {
					String dbShardingColumns = shardingDataSourceShardingTablesDbProperties.getDbShardingColumns();
					if(StringUtils.isBlank(dbShardingColumns)) {
						throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]，表名["+shardingTableName+"]的dbShardingColumns不能为空");
					}
					String dbShardingStrategyName = shardingDataSourceShardingTablesDbProperties.getDbShardingStrategyName();
					String dbShardingAlgorithmName = shardingDataSourceShardingTablesDbProperties.getDbShardingAlgorithmName();
					if(StringUtils.isBlank(dbShardingAlgorithmName)) {
						throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]，表名["+shardingTableName+"][分库]的dbShardingAlgorithmName不能为空");
					}
					ShardingStrategyConfiguration dbShardingStrategyConfiguration = shardingStrategyConfiguration(dbShardingStrategyName, dbShardingColumns, dbShardingAlgorithmName);
					shardingTableRuleConfiguration.setDatabaseShardingStrategy(dbShardingStrategyConfiguration);
				}else {
					this.coreLogger.info(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]，表名["+shardingTableName+"]的分库规则开关为关闭，放弃初始化该分库规则");
				}
				this.coreLogger.info(this.logPrefix + "创建shardingSphere数据源["+shardingDataSourceName+"][分库分表]初始化进行中，初始化分库规则["+shardingTableName+"]完成");
				
				//该表的分表规则
				ShardingDataSourceShardingTablesTableProperties shardingDataSourceShardingTablesTableProperties = shardingDataSourceShardingTablesProperties.getTableSharding();
				boolean enabled2 = shardingDataSourceShardingTablesTableProperties.isEnabled();
				if(enabled2) {
					String tableShardingColumns = shardingDataSourceShardingTablesTableProperties.getTableShardingColumns();
					if(StringUtils.isBlank(tableShardingColumns)) {
						throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]，表名["+shardingTableName+"][分表]的tableShardingColumns不能为空");
					}
					String tableShardingStrategyName = shardingDataSourceShardingTablesTableProperties.getTableShardingStrategyName();
					String tableShardingAlgorithmName = shardingDataSourceShardingTablesTableProperties.getTableShardingAlgorithmName();
					if(StringUtils.isBlank(tableShardingAlgorithmName)) {
						throw new IllegalArgumentException(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]，表名["+shardingTableName+"][分表]的tableShardingAlgorithmName不能为空");
					}
					ShardingStrategyConfiguration tableShardingStrategyConfiguration = shardingStrategyConfiguration(tableShardingStrategyName, tableShardingColumns, tableShardingAlgorithmName);
					shardingTableRuleConfiguration.setTableShardingStrategy(tableShardingStrategyConfiguration);
				}else {
					this.coreLogger.info(this.logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]，表名["+shardingTableName+"]的分表规则开关为关闭，放弃初始化该分表规则");
				}
				this.coreLogger.info(this.logPrefix + "创建shardingSphere数据源["+shardingDataSourceName+"][分库分表]初始化进行中，初始化分表规则["+shardingTableName+"]完成");
				
				//当用户开启分库分表规则开关后，至少配置了分库or分表规则，才能会将规则加入到shardingTableRuleCollection中
				if(enabled1 || enabled2) {
					shardingTableRuleCollection.add(shardingTableRuleConfiguration);
				}
			}else {
				this.coreLogger.info(logPrefix + "shardingSphere数据源["+shardingDataSourceName+"][分库分表]，表名["+shardingTableName+"]的规则开关为关闭，放弃初始化该分库分表规则");
			}
		});
		
		return shardingTableRuleCollection;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月22日 下午3:15:05
	 * @version 1.0
	 * @description 根据配置的分片策列名称、以及分片、算法名称列创建一个ShardingStrategyConfiguration
	 * @param shardingStrategyName
	 * @param shardingColumns
	 * @param shardingAlgorithmName
	 * @return
	 */
	private ShardingStrategyConfiguration shardingStrategyConfiguration(String shardingStrategyName, String shardingColumns, String shardingAlgorithmName) {
		ShardingStrategyConfiguration shardingStrategyConfiguration = null;
		
		switch (shardingStrategyName.toUpperCase()) {
		case ShardingConstants.ShardingStrategyName.STANDARD:
			shardingStrategyConfiguration = new StandardShardingStrategyConfiguration(shardingColumns, shardingAlgorithmName); 
			break;
		case ShardingConstants.ShardingStrategyName.COMPLEX:
			shardingStrategyConfiguration = new ComplexShardingStrategyConfiguration(shardingColumns, shardingAlgorithmName);
			break;
		case ShardingConstants.ShardingStrategyName.HINT:
			shardingStrategyConfiguration = new HintShardingStrategyConfiguration(shardingAlgorithmName);
			break;
		case ShardingConstants.ShardingStrategyName.NONE:
			shardingStrategyConfiguration = new NoneShardingStrategyConfiguration();
			break;
		default:
			this.coreLogger.warn(shardingAlgorithmName);
			shardingStrategyConfiguration = new NoneShardingStrategyConfiguration();
			break;
		}
		
		return shardingStrategyConfiguration;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月15日 下午1:54:25
	 * @version 1.0
	 * @description 创建一个Sharding对外暴漏的数据源 
	 * @param shardingDataSourceName 该Sharding数据源的名称，在配置文件中配置
	 * @param dataSourceMap 数据源的Map
	 * @param ruleConfigurations 数据源规则
	 * @return
	 */
	private DataSource createNormalDataSource(
			String shardingDataSourceName, 
			String shardingSchema,
			Map<String, DataSource> dataSourceMap, 
			Collection<RuleConfiguration> ruleConfigurations) {
		
		DataSource dataSource = null;
		try {
			dataSource = ShardingSphereDataSourceFactory.createDataSource(shardingSchema, dataSourceMap, ruleConfigurations, new Properties());
//			dataSource = ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, ruleConfigurations, new Properties());
		} catch (SQLException e) {
			throw new ShardingException("创建shardingSphere数据源["+shardingDataSourceName+"]失败", e);
		}
		
		return dataSource;
	}
	
	private DataSource createGovernanceDataSource(
			String shardingDataSourceName,
			Map<String, DataSource> dataSourceMap, 
			Collection<RuleConfiguration> ruleConfigurations,
			String registerCenters,
			ShardingRegisterCenterHolder shardingRegisterCenterHolder) {
		
		DataSource dataSource = null;
		String[] registerCentersArr = registerCenters.split(",");
		String registerCenter1 = registerCentersArr[0].trim(); //目前只支持一个注册中心，如果配置多个，只有第一个生效
		GovernanceConfiguration governanceConfiguration = shardingRegisterCenterHolder.get(registerCenter1);
		if(governanceConfiguration==null) {
			throw new ShardingException("创建shardingSphere数据源["+shardingDataSourceName+"]时，您配置了注册中心["+registerCenter1+"]，但是该注册中心不存在，请检查该注册中心的配置");
		}
		try {
			dataSource = GovernanceShardingSphereDataSourceFactory.createDataSource(dataSourceMap, ruleConfigurations, new Properties(), governanceConfiguration);
		} catch (SQLException e) {
			throw new ShardingException("创建shardingSphere数据源["+shardingDataSourceName+"]失败", e);
		}
		
		return dataSource;
	}
	
	private void manageProps(
			ShardingDataSourceHolder shardingDataSourceHolder, 
			String shardingDataSourceName,
			ShardingDataSourceProperties shardingDataSourceProperties) {
		ShardingDataSourceProps shardingDataSourceProps = new ShardingDataSourceProps();
		shardingDataSourceProps.setDateSourceType(Type.SHARDING_SPHERE);
		shardingDataSourceProps.setReadwriteSplitting(shardingDataSourceProperties.getDataSourceType().isReadwriteSplitting());
		shardingDataSourceProps.setSharding(shardingDataSourceProperties.getDataSourceType().isSharding());
		shardingDataSourceProps.setRegisterCenters(shardingDataSourceProperties.getRegisterCenters());
		shardingDataSourceHolder.setShardingDataSourceProps(shardingDataSourceName, shardingDataSourceProps);
	}
	
}
