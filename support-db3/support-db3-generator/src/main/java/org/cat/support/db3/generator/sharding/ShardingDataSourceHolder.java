package org.cat.support.db3.generator.sharding;

import java.util.Map;

import javax.sql.DataSource;

import org.cat.support.db3.generator.datasource.DataSourceHolder;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月17日 下午4:32:09
 * @version 1.0
 * @description ShardingSphereDataSource持有者
 *
 */
public class ShardingDataSourceHolder extends DataSourceHolder<DataSource> {
	
//	public Map<String, DataSource> getShardingDatasourceMapper(){
//		Map<String, DataSource> dataSourceMap = Maps.newHashMap();
//		this.getDatasourceMapper().forEach((name, dataSource) -> {
//			dataSourceMap.put(name, (DataSource)dataSource);
//		});
//		return dataSourceMap;
//	}
	
private Map<String, ShardingDataSourceProps> shardingDataSourcePropsMapper = Maps.newHashMap();
	
	public ShardingDataSourceProps getShardingDataSourceProps(String shardingDataSourceName) {
		ShardingDataSourceProps druidDataSourceProps = this.shardingDataSourcePropsMapper.get(shardingDataSourceName);
		return druidDataSourceProps;
	}
	
	public void setShardingDataSourceProps(String shardingDataSourceName, ShardingDataSourceProps shardingDataSourceProps) {
		this.shardingDataSourcePropsMapper.put(shardingDataSourceName, shardingDataSourceProps);
	}
	
	@Getter
	@Setter
	public static class ShardingDataSourceProps{
		private String dateSourceType; //DataSourceConstants.Type.SHARDING_SPHERE
		private boolean readwriteSplitting;
		private boolean sharding;
		private String registerCenters;
	}
}
