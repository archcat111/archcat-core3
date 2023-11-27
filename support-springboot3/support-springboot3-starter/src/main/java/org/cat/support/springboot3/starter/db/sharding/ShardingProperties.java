package org.cat.support.springboot3.starter.db.sharding;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.db.sharding-sphere")
@Getter
@Setter
public class ShardingProperties {
	private boolean enabled = false;
	
	/**
	 * key：shardingDataSource，即这个由ShardingSphere返回的由用户最终使用的DataSource的名称
	 * value：这个shardingDataSource的实际数据源配置、读写分离配置、分库分表配置、注册中心使用配置
	 */
	private Map<String, ShardingDataSourceProperties> shardingDataSources;
	
	/**
	 * key：注册中心的名称，在配置shardingDataSources中的每个数据源的registerCenters属性时，可以引用该名称
	 * value：注册中心的配置
	 */
	private Map<String, ShardingRegisterCenterProperties> registerCenters;
}
