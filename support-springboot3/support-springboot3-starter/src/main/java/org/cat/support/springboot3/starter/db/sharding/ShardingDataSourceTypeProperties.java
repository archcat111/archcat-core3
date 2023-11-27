package org.cat.support.springboot3.starter.db.sharding;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月23日 下午3:39:13
 * @version 1.0
 * @description Sharding中的一个数据源可能对应多个实际数据源
 * 		该配置文件控制这些Sharding数据源是否启用读写分离或者分库分表
 *
 */
@Getter
@Setter
public class ShardingDataSourceTypeProperties {
	private boolean readwriteSplitting = false;
	private boolean sharding = false;
}
