package org.cat.support.db3.generator.druid;

import java.util.List;

import org.cat.support.db3.generator.druid.study.DruidDemoProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月14日 下午3:52:36
 * @version 1.0
 * @description 用于多个DruidDataSource公用的配置
 * 如果每个DruidDataSource的参数值为null，则会使用DruidDataSource自己的配置
 * 配置参考：{@linkplain DruidDemoProperties}
 *
 */
@Getter
@Setter
public class DruidCommonProperties {
	private Integer initialSize = 1;
	private Integer maxActive = 20;
	private Integer minIdle = 1;
	private Integer maxWaitMillis = 5000;
	private Boolean useUnfairLock = true;
	private Integer maxWaitThreadCount = -1;
	private Integer maxCreateTaskCount = 3;
	
	private String validationQuery = "select 1";
	private Integer validationQueryTimeout = 5;
	private Boolean testOnBorrow = false;
	private Boolean testOnReturn = false;
	private Boolean testWhileIdle = true;
	private Boolean keepAlive = true;
	private Long keepAliveBetweenTimeMillis = 60 * 1000L;
	private Long minEvictableIdleTimeMillis = 60 * 30 * 1000L;
	private Long maxEvictableIdleTimeMillis = 60 * 30 * 1000L * 7;
	private Long timeBetweenEvictionRunsMillis = 45 * 1000L;
	
	private Boolean removeAbandoned = false;
	private Integer removeAbandonedTimeout = 300;
	private Boolean logAbandoned = false;
	
	private List<String> connectionInitSqls = null;
	private Boolean defaultAutoCommit = true;
	private Boolean defaultReadOnly = false;
	private Integer defaultTransactionIsolation = null;
	private Long transactionThresholdMillis = 0L;
	private Integer transactionQueryTimeout = 0;
	private Integer queryTimeout = 0;
	
	private String filters = null;
	
	private Boolean asyncInit = true;
	private Boolean failFast = false;
	
	private Long timeBetweenConnectErrorMillis = 5 * 1000L;
	private Integer connectionErrorRetryAttempts = 1; 
	private Boolean breakAfterAcquireFailure = false;
	
	private Boolean useGlobalDataSourceStat = false; //默认值为false
	
	//Oracle专用
	private boolean poolPreparedStatements = false; //默认值为false
	private int maxPoolPreparedStatementPerConnectionSize = -1; //默认值为10
	private int maxOpenPreparedStatements = -1; //默认值为-1
}
