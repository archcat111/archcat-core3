package org.cat.support.springboot3.starter.db.transaction;

import java.util.Optional;

import javax.sql.DataSource;

import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.core.web3.util.ArchSpringBeanUtil;
import org.cat.support.db3.generator.SupportDb3ConditionalFlag;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder;
import org.cat.support.db3.generator.sharding.ShardingDataSourceHolder;
import org.cat.support.db3.generator.transaction.ArchTransactionException;
import org.cat.support.db3.generator.transaction.TransactionManagerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 
 * @author 王云龙
 * @date 2022年5月14日 下午4:07:46
 * @version 1.0
 * @description 
 * 		在bootstrap.yaml中设置了如下：
 * 			spring.autoconfigure.exclude:
 * 				- org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
 * 		也就是说DataSourceTransactionManagerAutoConfiguration不满足初始化的条件，必须要有DataSource的Bean
 * 		而arch框架自己的druid和sharding都是采用动态注册DataSource的方式
 * 		因此在Spring初始化的时候，无法感知到DataSource的Bean的初始化，
 * 		因此当Arch框架初始化的时候如果只配置一个事务管理器的时候并不会出问题
 * 
 * 当需要配置多个事务管理器的时候，例如：
 * ...transaction:
 * 		roles:
 * 			shardingUserDS:
 * 				enabled: true
 * 			druidId3DS:
 * 				enabled: true
 * 那么在执行ServiceImpl的时候Spring会探测到有多个TransactionManager类型的实现，就不知道用哪一个了
 * 怎么办：
 * 		@Transactional添加transactionManager = "shardingUserDS-transactionManager"即可
 * 		transactionManager的名称在项目启动时会显示，也可以使用数据源名称后面加"-transactionManager"即可
 * @Service
 * @Transactional(rollbackFor=Exception.class,propagation=Propagation.REQUIRED, transactionManager = "shardingUserDS-transactionManager")
 * @ShardingSphereTransactionType(TransactionType.LOCAL)
 * public class UserServiceImpl extends AbsIdAndMyBatisService implements UserService {
 *
 */
@Configuration
@ConditionalOnClass(SupportDb3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.db.transaction", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(DbTransactionProperties.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class DbTransactionAutoConfiguration {
	
	@Autowired
	private DbTransactionProperties dbTransactionProperties;
	
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	protected final transient String logPrefix = "事务初始化：";
	
	@Autowired
	protected ApplicationContext applicationContext;
	@Autowired(required = false)
	protected DruidDataSourceHolder druidDataSourceHolder;
	@Autowired(required = false)
	protected ShardingDataSourceHolder shardingDataSourceHolder;
	
	private final String transactionManagerNamePrefix = "-transactionManager";
	
	
	@Bean
	public TransactionManagerHolder transactionManagerHolder() {
		TransactionManagerHolder transactionManagerHolder = new TransactionManagerHolder();
		
		Optional.ofNullable(this.dbTransactionProperties.getRoles())
			.orElseThrow(() -> new ArchTransactionException(this.logPrefix + "您打开了Mybatis的开关，但没有配置MyBatis的数据源"))
			.forEach((dataSourceName, dbTransactionDataSourceProperties) -> {
				String transactionManagerName = getTransactionManagerName(dataSourceName);
				if(dbTransactionDataSourceProperties.isEnabled()) {
					PlatformTransactionManager transactionManager = createPlatformTransactionManager(dataSourceName, dbTransactionDataSourceProperties);
					transactionManagerHolder.put(transactionManagerName, transactionManager);
				}else {
					coreLogger.info(this.logPrefix + "事务管理器["+transactionManagerName+"]开关为关闭，不进行transactionManager的初始化");
				}
		});
		
		return transactionManagerHolder;
	}
	
	public PlatformTransactionManager createPlatformTransactionManager(
			String dataSourceName, 
			DbTransactionDataSourceProperties dbTransactionDataSourceProperties) {
		
		DataSource dataSource = findDataSource(dataSourceName);
		
		DataSourceTransactionManager dataSourceTransactionManager  = new DataSourceTransactionManager();
		dataSourceTransactionManager.setDataSource(dataSource);
		
		String transactionManagerName = getTransactionManagerName(dataSourceName);
		ArchSpringBeanUtil.registerBean(this.applicationContext, transactionManagerName, dataSourceTransactionManager);
		this.coreLogger.info(this.logPrefix+"将transactionManager["+transactionManagerName+"]注入到Spring容器中");
		return applicationContext.getBean(transactionManagerName, PlatformTransactionManager.class);
	}
	
	private DataSource findDataSource(String dataSourceName) {
		DataSource dataSource = Optional.ofNullable(this.druidDataSourceHolder.get(dataSourceName))
				.orElse(this.shardingDataSourceHolder.get(dataSourceName));
		
		if(dataSource==null) {
			throw new ArchTransactionException(this.logPrefix + "初始化事务管理器，没有找到数据源["+dataSourceName+"]");
		}
		
		return dataSource;
	}
	
	private String getTransactionManagerName(String dataSourceName) {
		String transactionManagerName = dataSourceName + this.transactionManagerNamePrefix;
		return transactionManagerName;
	}
}
