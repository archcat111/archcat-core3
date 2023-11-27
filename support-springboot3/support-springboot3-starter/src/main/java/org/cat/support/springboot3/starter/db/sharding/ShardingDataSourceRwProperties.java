package org.cat.support.springboot3.starter.db.sharding;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShardingDataSourceRwProperties {
	
	/**
	 * key为在这个ShardingDataSource中的负载均衡的名称
	 * ShardingDataSourceRwLoadBalancerProperties为多个读库之间的负载均衡策略
	 */
	private Map<String, ShardingDataSourceRwLoadBalancerProperties> loadBalancers;
	
	/**
	 * key为DruidDataSource的ShardingDataSourceName（Sharding中的数据源别名），即：<shardingDataSourceAliasName1>
	 */
	private Map<String, ShardingDataSourceRwRuleProperties> dataSourcesRwRule;
}
