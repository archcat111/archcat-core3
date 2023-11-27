package org.cat.support.springboot3.starter.db.sharding;

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
public class ShardingDataSourceShardingTablesProperties {
	
	private boolean enabled = true;
	
	//例如：t_order表的分库分表形式为ds${0..1}.t_order${0..1}，其中
	//其中的ds${0..1}需要和该sharding所在的dataSources列表名称相吻合
	//如果是读写分离数据源则使用dataSourcesRwRule中的名称，如果不是读写分离则使用dataSources中的名称
	private String actualDataNodes; 
	
	//分库策略
	private ShardingDataSourceShardingTablesDbProperties dbSharding;
	
	//分表策略
	private ShardingDataSourceShardingTablesTableProperties tableSharding;
}
