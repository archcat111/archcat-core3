package org.cat.support.springboot3.starter.db.sharding;

import org.apache.shardingsphere.dbdiscovery.aware.DatabaseDiscoveryDataSourceNameAware;
import org.apache.shardingsphere.infra.aware.DataSourceNameAware;
import org.apache.shardingsphere.readwritesplitting.algorithm.DynamicReadwriteSplittingType;
import org.apache.shardingsphere.readwritesplitting.algorithm.StaticReadwriteSplittingType;
import org.apache.shardingsphere.readwritesplitting.route.impl.ReadwriteSplittingDataSourceRouter;
import org.cat.support.db3.generator.constants.ShardingConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月23日 下午2:59:47
 * @version 1.0
 * @description 读写分离配置中的数据源细节配置
 *
 */
@Getter
@Setter
public class ShardingDataSourceRwRuleProperties {
	private boolean enabled = true;
	
	/**
	 * 在读写分离的数据库中，当用户发送一条SQL的时候，首先会ReadwriteSplittingDataSourceRouter#route()方法中选择执行该SQL的数据库
	 * 获取autoAwareDataSourceName，如果有值，则根据该值对应的DataSourceNameAware来获取数据库
	 * 如果没有值，直接返会配置的writeDataSource
	 * 
	 * Optional<DataSourceNameAware> dataSourceNameAware = DataSourceNameAwareFactory.getInstance().getDataSourceNameAware();
	 * return dataSourceNameAware.get().getPrimaryDataSourceName(autoAwareDataSourceName);
	 * 
	 * {@linkplain ReadwriteSplittingDataSourceRouter#route(org.apache.shardingsphere.sql.parser.sql.common.statement.SQLStatement)}
	 * {@linkplain DataSourceNameAware}
	 * {@linkplain DatabaseDiscoveryDataSourceNameAware}
	 * 
	 * 创建ReadwriteSplittingDataSourceRuleConfiguration的时候需要一个type
	 * type有：STATIC、DYNAMIC
	 * 可以在如下类中找到对应的对比字符串，Sharding就是根据这个值找到使用哪些ReadwriteSplittingType
	 * {@linkplain StaticReadwriteSplittingType}
	 * {@linkplain DynamicReadwriteSplittingType}
	 */
	private String autoAwareDataSourceName = null;
	private String writeDataSourceName;
	private String readDataSourceNames;
	private String loadBalancerName = ShardingConstants.RwLoadBalanceName.DEFAULT;
}
