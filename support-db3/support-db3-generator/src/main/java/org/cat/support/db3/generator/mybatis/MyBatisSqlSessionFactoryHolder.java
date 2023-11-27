package org.cat.support.db3.generator.mybatis;

import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.cat.core.util3.map.SourceHolder;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月17日 下午4:32:09
 * @version 1.0
 * @description MyBatisSqlSessionFactory持有者
 *
 */
public class MyBatisSqlSessionFactoryHolder extends SourceHolder<SqlSessionFactory> {
	
	
	public Map<String, SqlSessionFactory> getSqlSessionFactoryMapper(){
		Map<String, SqlSessionFactory> sqlSessionFactoryMap = Maps.newHashMap();
		this.getSelfSourceMapper().forEach((name, sqlSessionFactory) -> {
			sqlSessionFactoryMap.put(name, sqlSessionFactory);
		});
		return sqlSessionFactoryMap;
	}
	
private Map<String, SqlSessionFactoryProps> sqlSessionFactoryPropsMapper = Maps.newHashMap();
	
	public SqlSessionFactoryProps getSqlSessionFactoryProps(String sqlSessionFactoryName) {
		SqlSessionFactoryProps sqlSessionFactoryProps = this.sqlSessionFactoryPropsMapper.get(sqlSessionFactoryName);
		return sqlSessionFactoryProps;
	}
	
	public void setSqlSessionFactoryProps(String sqlSessionFactoryName, SqlSessionFactoryProps sqlSessionFactoryProps) {
		this.sqlSessionFactoryPropsMapper.put(sqlSessionFactoryName, sqlSessionFactoryProps);
	}
		
	@Getter
	@Setter
	public static class SqlSessionFactoryProps {
		private String myBatisDataSourceName; //Mabatis中的逻辑数据源的名称，MybatisHolder中可以有多套数据源
		private String dataSourceType; //DataSourceConstants.Type.DRUID
		private String dataSourceName;
		private String mapperBeanPackages; //bean的package
		private String mapperSqlXmls; //xml的package
		private String mapperInterfacePackages; //mapper接口的package
		private ExecutorType executorType;
		private LocalCacheScope localCacheScope;
		private boolean level2Cache;
	}
}
