package org.cat.support.springboot3.starter.db.sharding;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShardingDataSourceProperties {
	private boolean enabled = true;
	
	private String registerCenters = null; //<registerCenter1>,<registerCenter2>,...
	
//	private String shardingSchema = DefaultSchema.LOGIC_NAME; //logic_db
	
	//每个数据源在Sharding中的数据源别名，Sharding的读写分离以及分库分表都会以该数据源别名做为引用
	//<dataSourceAliasShardingName1>: <dataSourceName1>
	//<dataSourceAliasShardingName2>: <dataSourceName2>
	private Map<String, String> dataSources; 
	
	private ShardingDataSourceTypeProperties dataSourceType = new ShardingDataSourceTypeProperties();
	
	//读写分离
	private ShardingDataSourceRwProperties readwriteSplitting;
	
	//分库分表
	private ShardingDataSourceShardingProperties sharding;
	
	//单独数据源
	//读写分离
	
	
	//引用Druid中配置的DataSource的名称，从DruidDataSourceHolder中获取
}
