package org.cat.support.springboot3.actuator.health;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSessionFactory;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionFactoryHolder;
import org.cat.support.db3.generator.mybatis.MyBatisSqlSessionFactoryHolder.SqlSessionFactoryProps;
import org.cat.support.db3.generator.sharding.ShardingDataSourceHolder;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

import com.google.common.collect.Maps;

import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import lombok.Setter;

public class MyBatis2HealthIndicator extends AbstractHealthIndicator {
	
//	private DruidDataSourceHolder druidDataSourceHolder;
//	private ShardingDataSourceHolder shardingDataSourceHolder;
	private MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder;
	private final String MYBATIS_SQL_SESSION_FACTORY = "myBatisSqlSessionFactory";
//	private final String 

	public MyBatis2HealthIndicator(
			MyBatisSqlSessionFactoryHolder myBatisSqlSessionFactoryHolder,
			DruidDataSourceHolder druidDataSourceHolder,
			ShardingDataSourceHolder shardingDataSourceHolder) {
		this.myBatisSqlSessionFactoryHolder = myBatisSqlSessionFactoryHolder;
//		this.druidDataSourceHolder = druidDataSourceHolder;
//		this.shardingDataSourceHolder = shardingDataSourceHolder;
	}
	

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		//一般不会进入，因为初始化本Bean的条件之一就是有druidDataSourceHolder实例
		if(this.myBatisSqlSessionFactoryHolder==null) { 
			builder.status(Status.OUT_OF_SERVICE);
			builder.outOfService().withDetail(MYBATIS_SQL_SESSION_FACTORY, "本应用未使用myBatisSqlSessionFactoryHolder");
			return;
		}
		
		Map<String, SqlSessionFactory> sqlSessionFactoryMap = this.myBatisSqlSessionFactoryHolder.getSqlSessionFactoryMapper();
		if(MapUtil.isEmpty(sqlSessionFactoryMap)) {
			builder.status(Status.UP);
			builder.unknown().withDetail(MYBATIS_SQL_SESSION_FACTORY, "本应用未初始化myBatisSqlSessionFactory");
			return;
		}
		Status globalStatus = this.doHealthCheckForMyBatis(builder, sqlSessionFactoryMap);
		builder.status(globalStatus);
	}
	
	private Status doHealthCheckForMyBatis(Builder builder, Map<String, SqlSessionFactory> sqlSessionFactoryMap) {
		Status globalStatus = Status.UP;
		Map<String, SqlSessionFactoryValid> sqlSessionFactoryValids = Maps.newHashMap();
		
		for (Entry<String, SqlSessionFactory> entry : sqlSessionFactoryMap.entrySet()) {
			String sqlSessionFactoryName = entry.getKey();
			SqlSessionFactory sqlSessionFactory = entry.getValue();
			Status status = this.validSqlSessionFactoryForDefault(sqlSessionFactory);
			if(status.equals(Status.DOWN)) {
				globalStatus = Status.DOWN;
			}
			
			SqlSessionFactoryProps sqlSessionFactoryProps = this.myBatisSqlSessionFactoryHolder.getSqlSessionFactoryProps(sqlSessionFactoryName);
			
			SqlSessionFactoryValid sqlSessionFactoryValid = new SqlSessionFactoryValid();
			sqlSessionFactoryValid.setMybatisDataSourceName(sqlSessionFactoryProps.getMyBatisDataSourceName());
			sqlSessionFactoryValid.setDataSourceType(sqlSessionFactoryProps.getDataSourceType());
			sqlSessionFactoryValid.setDataSourceName(sqlSessionFactoryProps.getDataSourceName());
			sqlSessionFactoryValid.setMapperBeanPackages(sqlSessionFactoryProps.getMapperBeanPackages());
			sqlSessionFactoryValid.setMapperSqlXmls(sqlSessionFactoryProps.getMapperSqlXmls());
			sqlSessionFactoryValid.setMapperInterfacePackages(sqlSessionFactoryProps.getMapperInterfacePackages());
			sqlSessionFactoryValid.setExecutorType(sqlSessionFactoryProps.getExecutorType());
			sqlSessionFactoryValid.setLocalCacheScope(sqlSessionFactoryProps.getLocalCacheScope());
			sqlSessionFactoryValid.setLevel2Cache(sqlSessionFactoryProps.isLevel2Cache());
			sqlSessionFactoryValid.setStatus(status);
			sqlSessionFactoryValids.put(sqlSessionFactoryName, sqlSessionFactoryValid);
		}
		
		builder.withDetails(sqlSessionFactoryValids);
		return globalStatus;
	}
	
	private Status validSqlSessionFactoryForDefault(SqlSessionFactory sqlSessionFactory) {
		boolean result = false;
		try {
			result = sqlSessionFactory.openSession().getConnection().isValid(0);
		} catch (Exception e) {
//			e.printStackTrace();
			result = false;
		}
		return result?Status.UP:Status.DOWN;
	}
	
	@Getter
	@Setter
	public class SqlSessionFactoryValid {
		private String mybatisDataSourceName; //在Mybatis中的逻辑数据源名称，可能是Sharding的，可能是Druid的
		private String dataSourceType;  //DataSourceConstants.DataSourceType.SHARDING_SPHERE
		private String dataSourceName;
		private String mapperBeanPackages; //org.cat.app.user3.impl.dao.bean
		private String mapperSqlXmls; //classpath:org/cat/app/user3/impl/dao/xml/*.xml
		private String mapperInterfacePackages; //org.cat.app.user3.impl.dao.mapper
		private ExecutorType executorType;
		private LocalCacheScope localCacheScope;
		private boolean level2Cache;
		private Status status;
	}
	

}
