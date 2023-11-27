package org.cat.support.springboot3.actuator.metrics;

import java.util.Map;
import java.util.Map.Entry;

import org.cat.support.db3.generator.druid.DruidDataSourceHolder;

import com.alibaba.druid.pool.DruidDataSource;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;

/**
 * 
 * @author 王云龙
 * @date 2022年5月25日 下午1:46:48
 * @version 1.0
 * @description DruidDataSourceHolder中的数据源监控
 *
 */
public class DataSource2Metrics implements MeterBinder {
	
	private DruidDataSourceHolder druidDataSourceHolder;
	
	public static final String ARCH_DATASOURCE2="arch.datasource2";
	public static final String ARCH_DATASOURCE2_EXECUTE_COUNT=ARCH_DATASOURCE2+"."+"execute.count";
	public static final String ARCH_DATASOURCE2_CONNECT_COUNT_MAX=ARCH_DATASOURCE2+"."+"connect.count.max";
	public static final String ARCH_DATASOURCE2_CONNECT_COUNT_ACTIVE=ARCH_DATASOURCE2+"."+"connect.count.active";
	public static final String ARCH_DATASOURCE2_CONNECT_WAIT_COUNT=ARCH_DATASOURCE2+"."+"connect.wait.count";
	public static final String ARCH_DATASOURCE2_CONNECT_WAIT_COUNT_ACCUMULATION=ARCH_DATASOURCE2+"."+"connect.wait.count.accumulation";
	
	public static final String ARCH_TAG_NAME="datasource2.name";
	
	public DataSource2Metrics(DruidDataSourceHolder druidDataSourceHolder) {
		this.druidDataSourceHolder = druidDataSourceHolder;
	}

	@Override
	public void bindTo(MeterRegistry registry) {
//		Gauge.builder(ARCH_DATASOURCE2_EXECUTE_COUNT, this, DataSource2Metrics::getExecuteCount)
//			.baseUnit("次")
//			.description("数据库请求总次数")
//			.register(registry);
		bindMeter(registry);
	}
	
	private void bindMeter(MeterRegistry registry) {
		if(druidDataSourceHolder==null) {
			return;
		}
		Map<String, DruidDataSource> dataSourceMapper = druidDataSourceHolder.getDruidDatasourceMapper();
		if(dataSourceMapper==null || dataSourceMapper.size()==0) {
			return;
		}
		
		for (Entry<String, DruidDataSource> entry : dataSourceMapper.entrySet()) {
			String druidDataSourceName = entry.getKey();
			DruidDataSource druidDataSource = entry.getValue();
			bindMeterForDataSource(registry, druidDataSourceName, druidDataSource);
		}
	}
	
	private void bindMeterForDataSource(MeterRegistry registry, String druidDataSourceName, DruidDataSource druidDataSource) {
		Gauge.builder(ARCH_DATASOURCE2_EXECUTE_COUNT, druidDataSource, this::getExecuteCount)
		.baseUnit("次")
		.description("数据库SQL请求总次数")
		.tag(ARCH_TAG_NAME, druidDataSourceName)
		.register(registry);
		
		Gauge.builder(ARCH_DATASOURCE2_CONNECT_COUNT_MAX, druidDataSource, this::getMaxConnectCount)
		.baseUnit("个")
		.description("允许最大的连接数量")
		.tag(ARCH_TAG_NAME, druidDataSourceName)
		.register(registry);
		
		Gauge.builder(ARCH_DATASOURCE2_CONNECT_COUNT_ACTIVE, druidDataSource, this::getActiveConnectCount)
		.baseUnit("个")
		.description("当前活跃连接数量")
		.tag(ARCH_TAG_NAME, druidDataSourceName)
		.register(registry);
		
		Gauge.builder(ARCH_DATASOURCE2_CONNECT_WAIT_COUNT, druidDataSource, this::getWaitConnectThreadCount)
		.baseUnit("个")
		.description("当前等待获取连接的线程数")
		.tag(ARCH_TAG_NAME, druidDataSourceName)
		.register(registry);
		
		Gauge.builder(ARCH_DATASOURCE2_CONNECT_WAIT_COUNT_ACCUMULATION, druidDataSource, this::getWaitConnectThreadAccumulationCount)
		.baseUnit("个")
		.description("累计等待获取连接的总次数")
		.tag(ARCH_TAG_NAME, druidDataSourceName)
		.register(registry);
		
	}
	
	private long getExecuteCount(DruidDataSource druidDataSource) {
		long executeCount = druidDataSource.getExecuteCount();
		return executeCount;
	}
	
	private long getMaxConnectCount(DruidDataSource druidDataSource) {
		long maxActiveConnectCount = druidDataSource.getMaxActive();
		return maxActiveConnectCount;
	}
	
	private long getActiveConnectCount(DruidDataSource druidDataSource) {
		long activeConnectCount = druidDataSource.getActiveCount();
		return activeConnectCount;
	}
	
	private long getWaitConnectThreadCount(DruidDataSource druidDataSource) {
		long waitConnectThreadCount = druidDataSource.getWaitThreadCount();
		return waitConnectThreadCount;
	}
	
	private long getWaitConnectThreadAccumulationCount(DruidDataSource druidDataSource) {
		long waitConnectThreadAccumulationCount = druidDataSource.getNotEmptyWaitCount();
		return waitConnectThreadAccumulationCount;
	}
	
}
