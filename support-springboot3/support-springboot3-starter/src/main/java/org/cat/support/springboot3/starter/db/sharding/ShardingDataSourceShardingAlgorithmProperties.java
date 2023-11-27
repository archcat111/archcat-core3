package org.cat.support.springboot3.starter.db.sharding;

import java.util.Map;

import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.cat.support.db3.generator.constants.ShardingConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月18日 下午5:08:07
 * @version 1.0
 * @description 分片配置下的算法相关配置
 *
 */
@Getter
@Setter
public class ShardingDataSourceShardingAlgorithmProperties {
	
	private boolean enabled = true;
	
	/**
	 * 算法的类型
	 * 如：InlineShardingAlgorithm的typeName叫INLINE
	 * ShardingSphere内置的算法实现有：
	 * 		BoundaryBasedRangeShardingAlgorithm：BOUNDARY_RANGE：基于分片边界的范围分片算法
	 * 		VolumeBasedRangeShardingAlgorithm：VOLUME_RANGE：基于分片容量的范围分片算法
	 * 		ComplexInlineShardingAlgorithm：COMPLEX_INLINE：基于行表达式的复合分片算法
	 * 		AutoIntervalShardingAlgorithm：AUTO_INTERVAL：基于可变时间范围的分片算法
	 * 		ClassBasedShardingAlgorithm：CLASS_BASED：基于自定义类的分片算法
	 * 		HintInlineShardingAlgorithm：HINT_INLINE：基于行表达式的Hint分片算法
	 * 		IntervalShardingAlgorithm：INTERVAL：基于固定时间范围的分片算法
	 * 		HashModShardingAlgorithm：HASH_MOD：基于哈希取模的分片算法
	 * 		InlineShardingAlgorithm：INLINE：基于行表达式的分片算法
	 * 		ModShardingAlgorithm：MOD：基于取模的分片算法
	 */
	private String type = ShardingConstants.ShardingAlgorithmType.INLINE;
	
	/**
	 * 不同的ShardingAlgorithm用到的参数可能不同
	 * 如：INLINE
	 * 		algorithm-expression：t_order${order_id % 2}
	 * 		allow-range-query-with-inline-sharding：false #是否允许范围查询。注意：范围查询会无视分片策略，进行全路由
	 */
	private Map<String, String> props = Maps.newHashMap();
	
}
