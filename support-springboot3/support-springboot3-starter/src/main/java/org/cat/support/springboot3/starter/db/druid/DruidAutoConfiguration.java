package org.cat.support.springboot3.starter.db.druid;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.core.web3.util.ArchSpringBeanUtil;
import org.cat.support.db3.generator.SupportDb3ConditionalFlag;
import org.cat.support.db3.generator.constants.DataSourceConstants.Type;
import org.cat.support.db3.generator.druid.DruidCommonProperties;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder.DruidDataSourceProps;
import org.cat.support.db3.generator.druid.DruidDataSourceProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@ConditionalOnClass(SupportDb3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.db.druid", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DruidDataSourcesProperties.class)
public class DruidAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Druid数据源初始化：";
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private DruidDataSourcesProperties druidDataSourcesProperties;
	private DruidCommonProperties druidCommonProperties;
	
	@PostConstruct
	public void init() {
		this.druidCommonProperties = this.druidDataSourcesProperties.getCommonProperties();
	}
	
	@ConditionalOnMissingBean(DruidDataSourceHolder.class)
	@Bean
	public DruidDataSourceHolder druidDataSourceHolder() {
		DruidDataSourceHolder druidDatasourceHolder = new DruidDataSourceHolder();
		
		Map<String, DruidDataSourceProperties> dataSourcesProperties = this.druidDataSourcesProperties.getDruidDataSources();
//		Assert.notNull(dataSourcesProperties, "您引入了support-db3，请配置数据源参数");
		
		dataSourcesProperties.forEach((dataSourceName, druidDataSourceProperties) -> {
			boolean enabled = druidDataSourceProperties.isEnabled();
			if(enabled) {
				this.coreLogger.info(this.logPrefix+"开始初始化"+dataSourceName);
				DruidDataSource druidDataSource = druidDataSource(dataSourceName, druidDataSourceProperties);
				this.coreLogger.info(this.logPrefix+"完成初始化"+dataSourceName);
				druidDatasourceHolder.put(dataSourceName, druidDataSource);
				this.manageProps(druidDatasourceHolder, dataSourceName, druidDataSourceProperties);//管理属性
				this.coreLogger.info(this.logPrefix+"将"+dataSourceName+"添加到druidDatasourceHolder进行管理");
			}else {
				this.coreLogger.info(this.logPrefix+"将数据源["+dataSourceName+"]注入到DruidDataSourceHolder中");
			}
		});
		return druidDatasourceHolder;
	}
	
//	@Bean
////	@Primary
//	public DruidDataSource aaa() {
//		DruidDataSource druidDataSource = new DruidDataSource();
//		return druidDataSource;
//	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月14日 下午4:45:18
	 * @version 1.0
	 * @description 初始化具体的DruidDataSource实例，并注入到Spring容器中 
	 * @param name
	 * @param commonDruidProperties
	 * @param druidDataSourceProperties
	 * @return
	 */
	private DruidDataSource druidDataSource(String dataSourceName, DruidDataSourceProperties druidDataSourceProperties) {
		
		if(applicationContext.containsBean(dataSourceName)) {
			throw new IllegalArgumentException(this.logPrefix+"Spring容器中已经包含名为["+dataSourceName+"]的Bean");
		}
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setName(dataSourceName);
		String url = druidDataSourceProperties.getUrl();
		if(StringUtils.isBlank(url)) {
			druidDataSource.close();
			throw new IllegalArgumentException(this.logPrefix+"数据源["+dataSourceName+"]的url不能为空");
		}
		druidDataSource.setUrl(url);
		String userName = druidDataSourceProperties.getUserName();
		if(StringUtils.isBlank(userName)) {
			druidDataSource.close();
			throw new IllegalArgumentException(this.logPrefix+"数据源["+dataSourceName+"]的userName不能为空");
		}
		druidDataSource.setUsername(userName);
		String password = druidDataSourceProperties.getPassword();
		if(StringUtils.isBlank(password)) {
			druidDataSource.close();
			throw new IllegalArgumentException(this.logPrefix+"数据源["+dataSourceName+"]的password不能为空");
		}
		druidDataSource.setPassword(password);
		
		Integer initialSize = druidDataSourceProperties.getInitialSize()!=null?druidDataSourceProperties.getInitialSize():this.druidCommonProperties.getInitialSize();
		druidDataSource.setInitialSize(initialSize);
		Integer maxActive = druidDataSourceProperties.getMaxActive()!=null?druidDataSourceProperties.getMaxActive():this.druidCommonProperties.getMaxActive();
		druidDataSource.setMaxActive(maxActive);
		Integer minIdle = druidDataSourceProperties.getMinIdle()!=null?druidDataSourceProperties.getMinIdle():this.druidCommonProperties.getMinIdle();
		druidDataSource.setMinIdle(minIdle);
		Integer maxWaitMillis = druidDataSourceProperties.getMaxWaitMillis()!=null?druidDataSourceProperties.getMaxWaitMillis():this.druidCommonProperties.getMaxWaitMillis();
		druidDataSource.setMaxWait(maxWaitMillis);
		Boolean useUnfairLock = druidDataSourceProperties.getUseUnfairLock()!=null?druidDataSourceProperties.getUseUnfairLock():this.druidCommonProperties.getUseUnfairLock();
		druidDataSource.setUseUnfairLock(useUnfairLock);
		Integer maxWaitThreadCount = druidDataSourceProperties.getMaxWaitThreadCount()!=null?druidDataSourceProperties.getMaxWaitThreadCount():this.druidCommonProperties.getMaxWaitThreadCount();
		druidDataSource.setMaxWaitThreadCount(maxWaitThreadCount);
		Integer maxCreateTaskCount = druidDataSourceProperties.getMaxCreateTaskCount()!=null?druidDataSourceProperties.getMaxCreateTaskCount():this.druidCommonProperties.getMaxCreateTaskCount();
		druidDataSource.setMaxCreateTaskCount(maxCreateTaskCount);
		
		String validationQuery = druidDataSourceProperties.getValidationQuery()!=null?druidDataSourceProperties.getValidationQuery():this.druidCommonProperties.getValidationQuery();
		druidDataSource.setValidationQuery(validationQuery);
		Integer validationQueryTimeout = druidDataSourceProperties.getValidationQueryTimeout()!=null?druidDataSourceProperties.getValidationQueryTimeout():this.druidCommonProperties.getValidationQueryTimeout();
		druidDataSource.setValidationQueryTimeout(validationQueryTimeout);
		Boolean testOnBorrow = druidDataSourceProperties.getTestOnBorrow()!=null?druidDataSourceProperties.getTestOnBorrow():this.druidCommonProperties.getTestOnBorrow();
		druidDataSource.setTestOnBorrow(testOnBorrow);
		Boolean testOnReturn = druidDataSourceProperties.getTestOnReturn()!=null?druidDataSourceProperties.getTestOnReturn():this.druidCommonProperties.getTestOnReturn();
		druidDataSource.setTestOnReturn(testOnReturn);
		Boolean testWhileIdle = druidDataSourceProperties.getTestWhileIdle()!=null?druidDataSourceProperties.getTestWhileIdle():this.druidCommonProperties.getTestWhileIdle();
		druidDataSource.setTestWhileIdle(testWhileIdle);
		Boolean keepAlive = druidDataSourceProperties.getKeepAlive()!=null?druidDataSourceProperties.getKeepAlive():this.druidCommonProperties.getKeepAlive();
		druidDataSource.setKeepAlive(keepAlive);
		Long keepAliveBetweenTimeMillis = druidDataSourceProperties.getKeepAliveBetweenTimeMillis()!=null?druidDataSourceProperties.getKeepAliveBetweenTimeMillis():this.druidCommonProperties.getKeepAliveBetweenTimeMillis();
		druidDataSource.setKeepAliveBetweenTimeMillis(keepAliveBetweenTimeMillis);
		Long minEvictableIdleTimeMillis = druidDataSourceProperties.getMinEvictableIdleTimeMillis()!=null?druidDataSourceProperties.getMinEvictableIdleTimeMillis():this.druidCommonProperties.getMinEvictableIdleTimeMillis();
		druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		Long maxEvictableIdleTimeMillis = druidDataSourceProperties.getMaxEvictableIdleTimeMillis()!=null?druidDataSourceProperties.getMaxEvictableIdleTimeMillis():this.druidCommonProperties.getMaxWaitThreadCount();
		druidDataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
		Long timeBetweenEvictionRunsMillis = druidDataSourceProperties.getTimeBetweenEvictionRunsMillis()!=null?druidDataSourceProperties.getTimeBetweenEvictionRunsMillis():this.druidCommonProperties.getTimeBetweenEvictionRunsMillis();
		druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		
		Boolean removeAbandoned = druidDataSourceProperties.getRemoveAbandoned()!=null?druidDataSourceProperties.getRemoveAbandoned():this.druidCommonProperties.getRemoveAbandoned();
		druidDataSource.setRemoveAbandoned(removeAbandoned);
		Integer removeAbandonedTimeout = druidDataSourceProperties.getRemoveAbandonedTimeout()!=null?druidDataSourceProperties.getRemoveAbandonedTimeout():this.druidCommonProperties.getRemoveAbandonedTimeout();
		druidDataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		Boolean logAbandoned = druidDataSourceProperties.getLogAbandoned()!=null?druidDataSourceProperties.getLogAbandoned():this.druidCommonProperties.getLogAbandoned();
		druidDataSource.setLogAbandoned(logAbandoned);
		
		List<String> connectionInitSqls = druidDataSourceProperties.getConnectionInitSqls()!=null?druidDataSourceProperties.getConnectionInitSqls():this.druidCommonProperties.getConnectionInitSqls();
		druidDataSource.setConnectionInitSqls(connectionInitSqls);
		Boolean defaultAutoCommit = druidDataSourceProperties.getDefaultAutoCommit()!=null?druidDataSourceProperties.getDefaultAutoCommit():this.druidCommonProperties.getDefaultAutoCommit();
		druidDataSource.setDefaultAutoCommit(defaultAutoCommit);
		Boolean defaultReadOnly = druidDataSourceProperties.getDefaultReadOnly()!=null?druidDataSourceProperties.getDefaultReadOnly():this.druidCommonProperties.getDefaultReadOnly();
		druidDataSource.setDefaultReadOnly(defaultReadOnly);
		Integer defaultTransactionIsolation = druidDataSourceProperties.getDefaultTransactionIsolation()!=null?druidDataSourceProperties.getDefaultTransactionIsolation():this.druidCommonProperties.getDefaultTransactionIsolation();
		druidDataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
		Long transactionThresholdMillis = druidDataSourceProperties.getTransactionThresholdMillis()!=null?druidDataSourceProperties.getTransactionThresholdMillis():this.druidCommonProperties.getTransactionThresholdMillis();
		druidDataSource.setTransactionThresholdMillis(transactionThresholdMillis);
		Integer transactionQueryTimeout = druidDataSourceProperties.getTransactionQueryTimeout()!=null?druidDataSourceProperties.getTransactionQueryTimeout():this.druidCommonProperties.getTransactionQueryTimeout();
		if(transactionQueryTimeout!=null) {
			druidDataSource.setTransactionQueryTimeout(transactionQueryTimeout);
		}
		Integer queryTimeout = druidDataSourceProperties.getQueryTimeout()!=null?druidDataSourceProperties.getQueryTimeout():this.druidCommonProperties.getQueryTimeout();
		if(queryTimeout!=null) {
			druidDataSource.setQueryTimeout(queryTimeout);
		}
		
		String filters = druidDataSourceProperties.getFilters()!=null?druidDataSourceProperties.getFilters():this.druidCommonProperties.getFilters();
		try {
			druidDataSource.setFilters(filters);
		} catch (SQLException e) {
			this.coreLogger.error(this.logPrefix+"数据源["+dataSourceName+"]执行setFilters时报错，"+e.getMessage());
		}
		
		Boolean asyncInit = druidDataSourceProperties.getAsyncInit()!=null?druidDataSourceProperties.getAsyncInit():this.druidCommonProperties.getAsyncInit();
		druidDataSource.setAsyncInit(asyncInit);
		Boolean failFast = druidDataSourceProperties.getFailFast()!=null?druidDataSourceProperties.getFailFast():this.druidCommonProperties.getFailFast();
		druidDataSource.setFailFast(failFast);
		
		Long timeBetweenConnectErrorMillis = druidDataSourceProperties.getTimeBetweenConnectErrorMillis()!=null?druidDataSourceProperties.getTimeBetweenConnectErrorMillis():this.druidCommonProperties.getTimeBetweenConnectErrorMillis();
		druidDataSource.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis);
		Integer connectionErrorRetryAttempts = druidDataSourceProperties.getConnectionErrorRetryAttempts()!=null?druidDataSourceProperties.getConnectionErrorRetryAttempts():this.druidCommonProperties.getConnectionErrorRetryAttempts();
		druidDataSource.setConnectionErrorRetryAttempts(connectionErrorRetryAttempts);
		Boolean breakAfterAcquireFailure = druidDataSourceProperties.getBreakAfterAcquireFailure()!=null?druidDataSourceProperties.getBreakAfterAcquireFailure():this.druidCommonProperties.getBreakAfterAcquireFailure();
		druidDataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
		
		Boolean useGlobalDataSourceStat = druidDataSourceProperties.getUseGlobalDataSourceStat()!=null?druidDataSourceProperties.getUseGlobalDataSourceStat():this.druidCommonProperties.getUseGlobalDataSourceStat();
		druidDataSource.setUseGlobalDataSourceStat(useGlobalDataSourceStat);
		
		ArchSpringBeanUtil.registerBean(applicationContext, dataSourceName, druidDataSource);
		this.coreLogger.info(this.logPrefix+"将数据源["+dataSourceName+"]注入到Spring容器中");
		return applicationContext.getBean(dataSourceName, DruidDataSource.class);
	}
	
	private void manageProps(
			DruidDataSourceHolder druidDatasourceHolder, 
			String dataSourceName,
			DruidDataSourceProperties druidDataSourceProperties) {
		DruidDataSourceProps druidDataSourceProps = new DruidDataSourceProps();
		druidDataSourceProps.setDateSourceType(Type.DRUID);
		druidDataSourceProps.setUrl(druidDataSourceProperties.getUrl());
		druidDatasourceHolder.setDruidDataSourceProps(dataSourceName, druidDataSourceProps);
	}
	
}
