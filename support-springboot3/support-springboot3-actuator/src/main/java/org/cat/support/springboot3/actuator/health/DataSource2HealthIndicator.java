package org.cat.support.springboot3.actuator.health;

import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.cat.support.db3.generator.druid.DruidDataSourceHolder;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder.DruidDataSourceProps;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

import com.google.common.collect.Maps;

import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import lombok.Setter;

public class DataSource2HealthIndicator extends AbstractHealthIndicator {
	
	@Setter
	private DruidDataSourceHolder druidDataSourceHolder;
	private final String DRUID_DATA_SOURCES = "druidDataSources";
	
	public DataSource2HealthIndicator() {
	}

	public DataSource2HealthIndicator(DruidDataSourceHolder druidDataSourceHolder) {
		this.druidDataSourceHolder = druidDataSourceHolder;
	}
	
	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		//一般不会进入，因为初始化本Bean的条件之一就是有druidDataSourceHolder实例
		if(this.druidDataSourceHolder==null) { 
			builder.status(Status.OUT_OF_SERVICE);
			builder.outOfService().withDetail(DRUID_DATA_SOURCES, "本应用未使用druidDataSourceHolder");
			return;
		}
		
		Map<String, DataSource> dataSourceMap = this.druidDataSourceHolder.getDatasourceMapper();
		if(MapUtil.isEmpty(dataSourceMap)) {
			builder.status(Status.UP);
			builder.unknown().withDetail(DRUID_DATA_SOURCES, "本应用未初始化druidDataSource");
			return;
		}
		Status status = this.doHealthCheckForDruidDataSource(builder, dataSourceMap);
		builder.status(status);
	}
	
	private Status doHealthCheckForDruidDataSource(Builder builder, Map<String, DataSource> dataSourceMap) {
		Status globalStatus = Status.UP;
		Map<String, DataSourceValid> dataSourceValidDetail = Maps.newHashMap();
		
		for (Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
			String dataSourceName = entry.getKey();
			DataSource dataSource = entry.getValue();
			Status status = this.validDataSourceForDefault(dataSource);
			if(status.equals(Status.DOWN)) {
				globalStatus = Status.DOWN;
			}
			
			DruidDataSourceProps druidDataSourceProps = this.druidDataSourceHolder.getDruidDataSourceProps(dataSourceName);
			DataSourceValid dataSourceValid = new DataSourceValid();
			dataSourceValid.setDataSourceType(druidDataSourceProps.getDateSourceType());
			dataSourceValid.setUrl(druidDataSourceProps.getUrl());
			dataSourceValid.setStatus(status);
			dataSourceValidDetail.put(dataSourceName, dataSourceValid);
		}
		
		builder.withDetails(dataSourceValidDetail);
		return globalStatus;
	}
	
	private Status validDataSourceForDefault(DataSource dataSource) {
		boolean result = false;
		try {
			result = dataSource.getConnection().isValid(0);
		} catch (Exception e) {
//			e.printStackTrace();
			result = false;
		}
		return result?Status.UP:Status.DOWN;
	}
	
	@Setter
	@Getter
	public static class DataSourceValid {
		private String url;
		private String dataSourceType;
		private Status status;
		
	}
	

}
