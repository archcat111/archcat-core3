package org.cat.support.id3.generator.snowflake.workerid.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.RandomUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.cat.support.id3.generator.snowflake.workerid.IWorkerIdCustomGenerator;
import org.cat.support.id3.generator.snowflake.workerid.IWorkerIdDbGenerator;
import org.cat.support.id3.generator.snowflake.workerid.IWorkerIdRandomGenerator;
import org.cat.support.id3.generator.snowflake.workerid.dao.bean.WorkerNode;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 
 * @author 王云龙
 * @date 2021年10月12日 下午6:15:53
 * @version 1.0
 * @description Snowflake Id算法中生成workerId的生成器实现
 * 		当用户自行制定workerId时，直接返回用户配置的workId
 * 		否则根据workerIdBits返回对应的随机数(位数越小，重复的概率越大)，默认22位，最大为：4,194,303
 * 		例如：设置workerIdBits为22，则生成的随机数为0,000,0001到4,194,302之间的随机数
 * 
 * 		注意1：如果使用customWokerId初始化，如果多个服务往同一张表中写数据：
 * 			a：如果使用相同的customWokerId，则可能会有概率出现主键冲突
 * 			b：如果使用不同的customWorkerId(不同的实例需要不同的配置文件)，不会有主键冲突的问题，但是MySQL的插入性能会下降，
 * 				因为分布式插入时主键不一定是自增的
 * 		注意２：如果使用workerId的位数初始化随机workerId，如果多个服务往同一张表中写数据：
 * 			a：22位的workerId发生几个服务初始化为同一个workerId的概率极小，又有Sequeue，即出现主键冲突的概率极小
 * 			b：正常情况下，不会有主键冲突的问题，但是MySQL的插入性能会下降
 * 				因为分布式插入时主键不一定是自增的
 * 		注意3：如果使用db生成workerId，如果多个服务往同一张表中写数据：
 * 			a：每个微服务实例会固定使用自己唯一的workerId，不会出现逐渐冲突，且有一定的跟踪效果
 * 			b：不会有主键冲突的问题，但是MySQL的插入性能会下降
 * 				因为分布式插入时主键不一定是自增的
 * 			
 */
public class WorkerIdGeneratorImpl implements IWorkerIdCustomGenerator, IWorkerIdRandomGenerator, IWorkerIdDbGenerator {
	
	private int workerIdBits = 22;
	private long workerId = 0L;
	
	private boolean customize = false;
	private boolean random = false;
	private boolean db = false;
	
	public WorkerIdGeneratorImpl() {
		
	}
	
	@Override
	public boolean isCustomize() {
		return this.customize;
	}

	@Override
	public void setCustomWorkId(long workerId) {
		this.workerId = workerId;
		this.customize = true;
	}
	
	@Override
	public boolean isRandom() {
		return this.random;
	}

	@Override
	public void setRandom(int workerIdBits) {
		this.workerIdBits = workerIdBits;
		//如果workerIdBits为22，则会生成"1111111111111111111110"
		//换算成最大的十进制则为4194302
		long maxWorkerId = ~(-1L << workerIdBits) - 1 ;
		//在1到4194302之间获取一个随机数
		this.workerId = RandomUtils.nextLong(1, maxWorkerId);
		this.random = true;
	}
	
	@Override
	public boolean isDB() {
		return this.db;
	}

	@Override
	public void setDB(int workerIdBits, DataSource dataSource, String platform, String appName, String appModule, String hostIp, String port, int type) throws SQLException {
		if(!TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.initSynchronization();
		}
//		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		
		Connection connection = DataSourceUtils.getConnection(dataSource);
		boolean defaultAutoCommit = connection.getAutoCommit();
		connection.setAutoCommit(false);
		
		Date now = new Date();
		try {
			//通过platform，appName，hostIp，port获取之前已经存在的workerNode数据
			String selectSql = "select * from worker_node where "
					+ "platform=:platform and app_name=:appName and app_module=:appModule and host_ip=:hostIp and port=:port";
			 Map<String, Object> selectParamMap = Maps.newHashMap();
			 selectParamMap.put("platform", platform);
			 selectParamMap.put("appName", appName);
			 selectParamMap.put("appModule", appModule);
			 selectParamMap.put("hostIp", hostIp);
			 selectParamMap.put("port", port);
			 List<Map<String, Object>> result =  jdbcTemplate.queryForList(selectSql, selectParamMap);
			 //如果workerNode数据不存在，则新增
			 if(result == null || result.size()==0) {
				 KeyHolder keyHolder = new GeneratedKeyHolder();
				 String insertSql = "insert into worker_node "
				 		+ "(platform, app_name, app_module, host_ip, port, type, last_start_timestamp, create_timestamp, last_update_timestamp) "
				 		+ "values "
				 		+ "(:platform, :appName, :appModule, :hostIp, :port, :type, :lastStartTimestamp, :createTimestamp, :lastUpdateTimestamp)";
				 WorkerNode insertWorkerNode = new WorkerNode();
				 insertWorkerNode.setPlatform(platform);
				 insertWorkerNode.setAppName(appName);
				 insertWorkerNode.setAppModule(appModule);
				 insertWorkerNode.setHostIp(hostIp);
				 insertWorkerNode.setPort(port);
				 insertWorkerNode.setType(type);
				 insertWorkerNode.setLastStartTimestamp(now);
				 insertWorkerNode.setCreateTimestamp(now);
				 insertWorkerNode.setLastUpdateTimestamp(now);
				 int resultCount = jdbcTemplate.update(insertSql, new BeanPropertySqlParameterSource(insertWorkerNode) ,keyHolder);
				 if(resultCount == 1) {
					 long wokerId = keyHolder.getKey().longValue();
					 this.workerId = wokerId;
				 }else {
					 throw new SQLException("新增idGenerator的WorkerNode失败");
				 }
				 connection.commit();
			 }else { //如果workerNode数据存在，则使用其Id，并且更新last_start_timestamp
				 Map<String, Object> result1 = result.get(0);
				 long wokerId = (long) result1.get("id");
				 this.workerId = wokerId;
				 //更新最后使用时间
				 String updateSql = "update worker_node set last_update_timestamp=:lastUpdateTimestamp where id=:id";
				 Map<String, Object> updateParamMap = Maps.newHashMap();
				 updateParamMap.put("lastUpdateTimestamp", now);
				 updateParamMap.put("id", wokerId);
				 jdbcTemplate.update(updateSql, updateParamMap);
				 connection.commit();
			 }
		} catch (SQLException e) {
			connection.rollback();
			throw e;
		} finally {
			connection.setAutoCommit(defaultAutoCommit);
		}
		this.db = true;
	}
	

	@Override
	public long assignWorkerId() {
		return this.workerId;		
	}

	@Override
	public String toString() {
		return "WorkerIdGeneratorImpl [workerIdBits=" + workerIdBits + ", workerId=" + workerId + ", customize="
				+ customize + ", random=" + random + ", db=" + db + "]";
	}
	
}
