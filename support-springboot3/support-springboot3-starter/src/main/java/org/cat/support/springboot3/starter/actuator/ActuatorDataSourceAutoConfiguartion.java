package org.cat.support.springboot3.starter.actuator;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.db3.generator.SupportDb3ConditionalFlag;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder;
import org.cat.support.db3.generator.mybatis.MyBatisMapperScannerConfigurerHolder;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionFactoryHolder;
import org.cat.support.db3.generator.sharding.ShardingDataSourceHolder;
import org.cat.support.springboot3.actuator.SupportActuator3ConditionalFlag;
import org.cat.support.springboot3.actuator.health.DataSource2HealthIndicator;
import org.cat.support.springboot3.actuator.health.MyBatis2HealthIndicator;
import org.cat.support.springboot3.actuator.health.ShardingDataSource2HealthIndicator;
import org.cat.support.springboot3.actuator.metrics.DataSource2Metrics;
import org.cat.support.springboot3.starter.actuator.props.ActuatorProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnClass({SupportActuator3ConditionalFlag.class, SupportDb3ConditionalFlag.class})
@ConditionalOnProperty(prefix = "cat.support3.actuator", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE-10)
@EnableConfigurationProperties(ActuatorProperties.class)
public class ActuatorDataSourceAutoConfiguartion {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Actuator初始化：";
	
	@Autowired(required = false)
	private DruidDataSourceHolder druidDataSourceHolder;
	@Autowired(required = false)
	private ShardingDataSourceHolder shardingDataSourceHolder;
	@Autowired(required = false)
	private MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder;
//	@Autowired(required = false)
//	private MyBatisMapperScannerConfigurerHolder myBatisMapperScannerConfigurerHolder;
	
	@ConditionalOnBean(DruidDataSourceHolder.class)
	@ConditionalOnProperty(prefix = "cat.support3.actuator.health.dataSource2", name = "enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnEnabledHealthIndicator("dataSource2")
	@Bean
	public DataSource2HealthIndicator dataSource2HealthIndicator(DruidDataSourceHolder druidDataSourceHolder) {
		DataSource2HealthIndicator DataSource2HealthIndicator = new DataSource2HealthIndicator(druidDataSourceHolder);
		return DataSource2HealthIndicator;
	}
	
	@ConditionalOnBean(ShardingDataSourceHolder.class)
	@ConditionalOnProperty(prefix = "cat.support3.actuator.health.shardingDataSource2", name = "enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnEnabledHealthIndicator("shardingDataSource2")
	@Bean
	public ShardingDataSource2HealthIndicator shardingDataSource2HealthIndicator() {
		ShardingDataSource2HealthIndicator shardingDataSource2HealthIndicator = 
				new ShardingDataSource2HealthIndicator(this.shardingDataSourceHolder, this.druidDataSourceHolder);
		return shardingDataSource2HealthIndicator;
	}
	
	@ConditionalOnBean({MyBatisSqlSessionFactoryHolder.class, MyBatisMapperScannerConfigurerHolder.class})
	@ConditionalOnProperty(prefix = "cat.support3.actuator.health.myBatis2", name = "enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnEnabledHealthIndicator("myBatis2")
	@Bean
	public MyBatis2HealthIndicator myBatis2HealthIndicator() {
		MyBatis2HealthIndicator myBatis2HealthIndicator = 
				new MyBatis2HealthIndicator(
						myBatisSqlSessionFactoryHolder, 
						druidDataSourceHolder,
						shardingDataSourceHolder);
		return myBatis2HealthIndicator;
	}

	@ConditionalOnBean(DruidDataSourceHolder.class)
	@ConditionalOnProperty(prefix = "cat.support3.actuator.metrics.dataSource2", name = "enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public DataSource2Metrics dataSource2Metrics(DruidDataSourceHolder druidDataSourceHolder) {
		DataSource2Metrics dataSource2Metrics = new DataSource2Metrics(druidDataSourceHolder);
		return dataSource2Metrics;
	}
	
}
