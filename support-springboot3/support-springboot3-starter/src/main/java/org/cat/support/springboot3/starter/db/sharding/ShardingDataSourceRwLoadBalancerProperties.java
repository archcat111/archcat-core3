package org.cat.support.springboot3.starter.db.sharding;

import java.util.Map;

import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.shardingsphere.readwritesplitting.algorithm.loadbalance.RandomReplicaLoadBalanceAlgorithm;
import org.apache.shardingsphere.readwritesplitting.algorithm.loadbalance.RoundRobinReplicaLoadBalanceAlgorithm;
import org.apache.shardingsphere.readwritesplitting.algorithm.loadbalance.WeightReplicaLoadBalanceAlgorithm;
import org.cat.support.db3.generator.constants.ShardingConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2022年4月13日 下午6:15:59
 * @version 1.0
 * @description 
 * {@linkplain RoundRobinReplicaLoadBalanceAlgorithm}
 * {@linkplain RandomReplicaLoadBalanceAlgorithm}
 * {@linkplain WeightReplicaLoadBalanceAlgorithm} 使用中的读库都必须配置权重
 *
 */
@Getter
@Setter
public class ShardingDataSourceRwLoadBalancerProperties {
	private boolean enabled = true;
	
	//具体的名称可以查看对应的类
	private String type = ShardingConstants.RwLoadBalanceType.ROUND_ROBIN;
	
	//具体的属性可以查看对应的类
	private Map<String, String> props = Maps.newHashMap();
	
}
