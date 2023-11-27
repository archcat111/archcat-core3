package org.cat.support.springboot3.starter.db.sharding;

import org.cat.support.db3.generator.constants.ShardingConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月18日 下午1:43:16
 * @version 1.0
 * @description 某张表进行分库的规则
 *
 */
@Getter
@Setter
public class ShardingDataSourceShardingTablesDbProperties {
	
	private boolean enabled = false;
	
	//如：user_id, user_code
	private String dbShardingColumns; 
	
	 //StandardShardingStrategyConfiguration
	 //ComplexShardingStrategyConfiguration
	 //HintShardingStrategyConfiguration
	 //NoneShardingStrategyConfiguration
	private String dbShardingStrategyName = ShardingConstants.ShardingStrategyName.STANDARD;
	
	/**
	 * 不同的分库分表算法名称
	 * 在同一个ShardingSphere数据源的所有表的分库分表策略及算法中，所有的算法名称应该唯一
	 * 因为不同的表引用分库分表的算法是通过算法名称来引用的
	 */
	private String dbShardingAlgorithmName; 
}
