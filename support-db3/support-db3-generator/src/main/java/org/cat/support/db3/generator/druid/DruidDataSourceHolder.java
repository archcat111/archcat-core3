package org.cat.support.db3.generator.druid;

import java.util.Map;

import org.cat.support.db3.generator.datasource.DataSourceHolder;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月17日 下午4:32:09
 * @version 1.0
 * @description DruidDataSource持有者
 *
 */
public class DruidDataSourceHolder extends DataSourceHolder<DruidDataSource> {
	
	public Map<String, DruidDataSource> getDruidDatasourceMapper(){
		Map<String, DruidDataSource> dataSourceMap = Maps.newHashMap();
		this.getDatasourceMapper().forEach((name, dataSource) -> {
			dataSourceMap.put(name, (DruidDataSource)dataSource);
		});
		return dataSourceMap;
	}
	
	private Map<String, DruidDataSourceProps> druidDataSourcePropsMapper = Maps.newHashMap();
	
	public DruidDataSourceProps getDruidDataSourceProps(String druidDataSourceName) {
		DruidDataSourceProps druidDataSourceProps = this.druidDataSourcePropsMapper.get(druidDataSourceName);
		return druidDataSourceProps;
	}
	
	public void setDruidDataSourceProps(String druidDataSourceName, DruidDataSourceProps druidDataSourceProps) {
		this.druidDataSourcePropsMapper.put(druidDataSourceName, druidDataSourceProps);
	}
		
	@Getter
	@Setter
	public static class DruidDataSourceProps {
		private String dateSourceType; //DataSourceConstants.Type.DRUID
		private String url;
	}
}
