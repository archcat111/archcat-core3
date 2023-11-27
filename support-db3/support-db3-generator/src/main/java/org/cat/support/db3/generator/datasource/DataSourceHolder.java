package org.cat.support.db3.generator.datasource;

import java.util.Map;

import javax.sql.DataSource;

import org.cat.core.util3.map.SourceHolder;

import com.google.common.collect.Maps;

/**
 * 
 * @author 王云龙
 * @date 2021年9月13日 下午3:02:02
 * @version 1.0
 * @description DataSource及其子类的持有者
 *
 */
public class DataSourceHolder<T extends DataSource> extends SourceHolder<DataSource>{

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月17日 下午4:31:01
	 * @version 1.0
	 * @description 返回value为本类顶层泛型Class的Map
	 * @return
	 */
	public Map<String, DataSource> getDatasourceMapper(){
		Map<String, DataSource> dataSourceMap = Maps.newHashMap();
		this.getSelfSourceMapper().forEach((name, druidDataSource) -> {
			dataSourceMap.put(name, druidDataSource);
		});
		return dataSourceMap;
	}
	
}
