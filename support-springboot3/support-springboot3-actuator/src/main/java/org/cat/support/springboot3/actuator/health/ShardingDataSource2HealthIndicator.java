package org.cat.support.springboot3.actuator.health;

import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.infra.database.DefaultSchema;
import org.apache.shardingsphere.infra.metadata.ShardingSphereMetaData;
import org.apache.shardingsphere.infra.metadata.resource.ShardingSphereResource;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.mode.metadata.MetaDataContexts;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder.DruidDataSourceProps;
import org.cat.support.db3.generator.sharding.ShardingDataSourceHolder;
import org.cat.support.db3.generator.sharding.ShardingDataSourceHolder.ShardingDataSourceProps;
import org.cat.support.springboot3.actuator.health.DataSource2HealthIndicator.DataSourceValid;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

import com.google.common.collect.Maps;

import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import lombok.Setter;

public class ShardingDataSource2HealthIndicator extends AbstractHealthIndicator {
	
	private DruidDataSourceHolder druidDataSourceHolder;
	private ShardingDataSourceHolder shardingDataSourceHolder;
	private final String DRUID_DATA_SOURCES = "shardingDataSources";
	
	public ShardingDataSource2HealthIndicator(ShardingDataSourceHolder shardingDataSourceHolder, DruidDataSourceHolder druidDataSourceHolder) {
//		this(shardingDataSourceHolder, null);
		this.shardingDataSourceHolder = shardingDataSourceHolder;
		this.druidDataSourceHolder = druidDataSourceHolder;
	}
	
//	public ShardingDataSource2HealthIndicator(ShardingDataSourceHolder shardingDataSourceHolder, String validQuerySql) {
//		this.shardingDataSourceHolder = shardingDataSourceHolder;
//		this.validQuerySql = validQuerySql;
//	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		//一般不会进入，因为初始化本Bean的条件之一就是有shardingDataSourceHolder实例
		if(this.shardingDataSourceHolder==null) { 
			builder.status(Status.OUT_OF_SERVICE);
			builder.outOfService().withDetail(DRUID_DATA_SOURCES, "本应用未使用shardingDataSourceHolder");
			return;
		}
		
		Map<String, DataSource> dataSourceMap = this.shardingDataSourceHolder.getDatasourceMapper();
		if(MapUtil.isEmpty(dataSourceMap)) {
			builder.status(Status.UP);
			builder.unknown().withDetail(DRUID_DATA_SOURCES, "本应用未初始化shardingDataSource");
			return;
		}
		Status status = this.doHealthCheckForShardingDataSource(builder, dataSourceMap);
		builder.status(status);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月19日 下午2:33:30
	 * @version 1.0
	 * @description 多个sharding数据源，只有所有sharding数据源都是up，状态才是up
	 * 		key： sharding数据源数据源名称；value：Sharding数据源
	 * @param builder
	 * @param dataSourceMap
	 * @return
	 */
	private Status doHealthCheckForShardingDataSource(Builder builder, Map<String, DataSource> dataSourceMap) {
		Status globalStatus = Status.UP;
		//ShardingDataSourceValid为一个sharding数据源的状态，内部会包含多个DataSource的状态
		Map<String, ShardingDataSourceValid> shradingDataSourceValidDetails = Maps.newHashMap();
		
		for (Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
			String shardingDataSourceName = entry.getKey();
			DataSource shardingDataSource = entry.getValue();
			Map<String, DataSourceValid> dataSourceValids = Maps.newHashMap();
			Status shardingStatus = Status.UNKNOWN;
			if(shardingDataSource instanceof ShardingSphereDataSource) {
				shardingStatus = this.validShardingDataSourceForDefault((ShardingSphereDataSource)shardingDataSource, dataSourceValids);
			}
			if(shardingStatus.equals(Status.DOWN)) {
				globalStatus = Status.DOWN;
			}
			
			ShardingDataSourceProps shardingDataSourceProps = this.shardingDataSourceHolder.getShardingDataSourceProps(shardingDataSourceName);
			ShardingDataSourceValid shardingDataSourceValid = new ShardingDataSourceValid();
			shardingDataSourceValid.setReadwriteSplitting(shardingDataSourceProps.isReadwriteSplitting());
			shardingDataSourceValid.setSharding(shardingDataSourceProps.isSharding());
			shardingDataSourceValid.setDataSourceType(shardingDataSourceProps.getDateSourceType());
			shardingDataSourceValid.setRegisterCenters(shardingDataSourceProps.getRegisterCenters());
			shardingDataSourceValid.setDataSources(dataSourceValids);
			shardingDataSourceValid.setStatus(shardingStatus);
			shradingDataSourceValidDetails.put(shardingDataSourceName, shardingDataSourceValid);
		}
		
		builder.withDetails(shradingDataSourceValidDetails);
		return globalStatus;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月19日 下午2:36:11
	 * @version 1.0
	 * @description 一个sharding数据源，只有多个dataSource都是up，状态才是up 
	 * @param shardingDataSource 一个sharding数据源
	 * @param dataSourceValids key为DataSourceName，DataSourceValid为该实际数据源的状态
	 * @return
	 */
	private Status validShardingDataSourceForDefault(
			ShardingSphereDataSource shardingSphereDataSource, 
			Map<String, DataSourceValid> dataSourceValids) {
		
		Status shardingStatus = Status.UP;
		
		String shardingSchema = DefaultSchema.LOGIC_NAME;
		ContextManager contextManager = shardingSphereDataSource.getContextManager();
		MetaDataContexts metaDataContexts = contextManager.getMetaDataContexts();
		ShardingSphereMetaData shardingSphereMetaData = metaDataContexts.getMetaData(shardingSchema);
		ShardingSphereResource shardingSphereResource = shardingSphereMetaData.getResource();
		Map<String, DataSource> dataSources = shardingSphereResource.getDataSources();
		
		for (Entry<String, DataSource> dataSourceEntry: dataSources.entrySet()) {
			String shardingAliasDataSourceName = dataSourceEntry.getKey();
			DataSource dataSource = dataSourceEntry.getValue();
			Status dataSourceStatus = this.validDataSourceForDefault(dataSource);
			if(dataSourceStatus.equals(Status.DOWN)) {
				shardingStatus = Status.DOWN;
			}
			String dataSourceName = this.druidDataSourceHolder.get(dataSource);
			DruidDataSourceProps druidDataSourceProps = druidDataSourceHolder.getDruidDataSourceProps(dataSourceName);
			DataSourceValidForSharding dataSourceValid = new DataSourceValidForSharding();
			dataSourceValid.setName(dataSourceName);
			dataSourceValid.setUrl(druidDataSourceProps.getUrl());
			dataSourceValid.setDataSourceType(druidDataSourceProps.getDateSourceType());
			dataSourceValid.setStatus(dataSourceStatus);
			dataSourceValids.put(shardingAliasDataSourceName, dataSourceValid);
		}
		
		return shardingStatus;
	}
	
	private Status validDataSourceForDefault(DataSource dataSource) {
		boolean result = false;
		try {
			result = dataSource.getConnection().isValid(0);
		} catch (SQLException e) {
//			e.printStackTrace();
			result = false;
		}
		return result?Status.UP:Status.DOWN;
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2022年5月25日 下午3:26:08
	 * @version 1.0
	 * @description ShardingDataSource的验证信息
	 *
	 */
	@Setter
	@Getter
	public class ShardingDataSourceValid {
		private boolean readwriteSplitting;
		private boolean sharding;
		private String dataSourceType;
		private Map<String, DataSourceValid> dataSources;
		private String registerCenters;
		private Status status;
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2022年5月25日 下午3:25:47
	 * @version 1.0
	 * @description ShardingDataSource中的具体某个数据源的验证信息
	 *
	 */
	@Setter
	@Getter
	public class DataSourceValidForSharding extends DataSourceValid {
		private String name;
	}
	
}
