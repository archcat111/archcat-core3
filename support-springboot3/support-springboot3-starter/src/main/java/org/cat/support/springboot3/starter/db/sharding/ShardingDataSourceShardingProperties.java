package org.cat.support.springboot3.starter.db.sharding;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShardingDataSourceShardingProperties {
	
	/**
	 * 算法集合的配置
	 * key是算法名称，在分库分表配置中引用该名称
	 */
	private Map<String, ShardingDataSourceShardingAlgorithmProperties> shardingAlgorithms;
	
	/**
	 * 需要分库分表的表的集合配置
	 * key是需要分库分表的表名
	 */
	private Map<String, ShardingDataSourceShardingTablesProperties> shardingTables;
	
}
