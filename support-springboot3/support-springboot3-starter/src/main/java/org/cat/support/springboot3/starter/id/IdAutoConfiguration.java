package org.cat.support.springboot3.starter.id;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.MapUtils;
import org.cat.core.util3.container.ArchDockerUtil;
import org.cat.core.web3.info.ArchInfoGenerator;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.core.web3.util.ArchSpringBeanUtil;
import org.cat.support.db3.generator.constants.DataSourceConstants;
import org.cat.support.db3.generator.druid.DruidDataSourceHolder;
import org.cat.support.db3.generator.sharding.ShardingDataSourceHolder;
import org.cat.support.id3.generator.IIdGenerator;
import org.cat.support.id3.generator.IdConstants;
import org.cat.support.id3.generator.IdGeneratorHolder;
import org.cat.support.id3.generator.IdGeneratorHolder.IdGeneratorProps;
import org.cat.support.id3.generator.SupportId3ConditionalFlag;
import org.cat.support.id3.generator.snowflake.IIdSnowflakeGenerator;
import org.cat.support.id3.generator.snowflake.exception.IdSnowflakeException;
import org.cat.support.id3.generator.snowflake.impl.IdSnowflakeGeneratorBuilder;
import org.cat.support.id3.generator.snowflake.impl.IdSnowflakeGeneratorImpl;
import org.cat.support.id3.generator.snowflake.pool.IIdPoolSnowflakeGenerator;
import org.cat.support.id3.generator.snowflake.pool.impl.IdPoolSnowflakeGeneratorBuilder;
import org.cat.support.id3.generator.snowflake.pool.impl.IdPoolSnowflakeGeneratorImpl;
import org.cat.support.id3.generator.snowflake.workerid.ContainerConstants;
import org.cat.support.id3.generator.snowflake.workerid.IWorkerIdCustomGenerator;
import org.cat.support.id3.generator.snowflake.workerid.IWorkerIdDbGenerator;
import org.cat.support.id3.generator.snowflake.workerid.IWorkerIdGenerator;
import org.cat.support.id3.generator.snowflake.workerid.IWorkerIdRandomGenerator;
import org.cat.support.id3.generator.snowflake.workerid.impl.WorkerIdGeneratorImpl;
import org.cat.support.id3.generator.uuid.IdUUIDGeneratorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SupportId3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.id", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties({IdProperties.class})
public class IdAutoConfiguration {
	
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	protected final transient String logPrefix = "IdGenerator初始化：";
	
	@Autowired
	private IdProperties idProperties;
	@Autowired
	private ArchInfoGenerator archInfoGenerator;
	
	@Autowired(required = false)
	private DruidDataSourceHolder druidDataSourceHolder;
	@Autowired(required = false)
	private ShardingDataSourceHolder shardingDataSourceHolder;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@ConditionalOnMissingBean(IdGeneratorHolder.class)
	@Bean
	public IdGeneratorHolder idGeneratorHolder() {
		IdGeneratorHolder idGeneratorHolder = new IdGeneratorHolder();
		Map<String, IdGeneratorProperties> idGeneratorPropertiesMap = idProperties.getGenerators();
		
		if(MapUtils.isEmpty(idGeneratorPropertiesMap)) {
			return idGeneratorHolder;
		}
		
		idGeneratorPropertiesMap.forEach((idGeneratorName, idGeneratorProperties) -> {
			if(!idGeneratorProperties.isEnabled()) {
				return;
			}
			
			IIdGenerator idGenerator = null;
			IdGeneratorProps idGeneratorProps = new IdGeneratorProps();
			
			switch (idGeneratorProperties.getType().toLowerCase()) {
			case IdConstants.Type.UUID:
				idGenerator = createIdGeneratorForUUID(idGeneratorName, idGeneratorProperties);
				idGeneratorProps.setType(IdConstants.Type.UUID);
				idGeneratorProps.setClazz(IdUUIDGeneratorImpl.class);
				break;
			case IdConstants.Type.SNOW_FLAKE:
				idGenerator = createIdGeneratorForSnowFlake(idGeneratorName, idGeneratorProperties, idGeneratorProps);
				idGeneratorProps.setType(IdConstants.Type.SNOW_FLAKE);
				idGeneratorProps.setClazz(IdSnowflakeGeneratorImpl.class);
				break;
			case IdConstants.Type.POOL_SNOW_FLAKE:
				idGenerator = createIdGeneratorForPoolSnowFlake(idGeneratorName, idGeneratorProperties, idGeneratorProps);
				idGeneratorProps.setType(IdConstants.Type.POOL_SNOW_FLAKE);
				idGeneratorProps.setClazz(IdPoolSnowflakeGeneratorImpl.class);
				break;
			default:
				break;
			}
			
			idGeneratorHolder.put(idGeneratorName, idGenerator);
			idGeneratorProps.setCreateDate(new Date());
			idGeneratorHolder.setIdGeneratorProps(idGeneratorName, idGeneratorProps);
		});
		
		return idGeneratorHolder;
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月22日 下午1:47:48
	 * @version 1.0
	 * @description 创建一个基于UUID实现的IdGenerator，同时会注入到Spring容器中
	 * @param idGeneratorName
	 * @param idGeneratorProperties
	 * @return
	 */
	private IIdGenerator createIdGeneratorForUUID(String idGeneratorName, IdGeneratorProperties idGeneratorProperties) {
		IIdGenerator idGenerator = new IdUUIDGeneratorImpl();

		ArchSpringBeanUtil.registerBean(applicationContext, idGeneratorName, idGenerator);
		return applicationContext.getBean(idGeneratorName, IIdGenerator.class);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月25日 下午4:24:52
	 * @version 1.0
	 * @description 创建一个基于SnowFlake实现的IdGenerator，同时会注入到Spring容器中 
	 * @param idGeneratorName
	 * @param idGeneratorProperties
	 * @param idGeneratorProps Holder中存储的idGenerator的相关属性，
	 * 			传入到这里是为了将相关需要记录属性添加到idGeneratorProps
	 * @return
	 */
	private IIdGenerator createIdGeneratorForSnowFlake(String idGeneratorName,
			IdGeneratorProperties idGeneratorProperties, IdGeneratorProps idGeneratorProps) {
		int deltaSecondBits = idGeneratorProperties.getDeltaSecondBits();
		int workerIdBits = idGeneratorProperties.getWorkerIdBits();
		int sequenceBits = idGeneratorProperties.getSequenceBits();
		
		long workerId = createWorkId(idGeneratorName, idGeneratorProperties, idGeneratorProps);
		
		String startDateStr = idGeneratorProperties.getStartDateStr();
		
		idGeneratorProps.setDeltaSecondBits(deltaSecondBits);
		idGeneratorProps.setWorkerIdBits(workerIdBits);
		idGeneratorProps.setSequenceBits(sequenceBits);
		idGeneratorProps.setWorkerId(workerId);
		idGeneratorProps.setStartDateStr(startDateStr);
		
		IIdSnowflakeGenerator idSnowflakeGenerator = IdSnowflakeGeneratorBuilder.create()
			.setDeltaSecondBits(deltaSecondBits)
			.setWorkerIdBits(workerIdBits)
			.setSequenceBits(sequenceBits)
			.setWorkerId(workerId)
			.setStartDateStr(startDateStr)
			.build();
		this.coreLogger.info(this.logPrefix+"创建IIdSnowflakeGenerator：["+idGeneratorName+"]["+idSnowflakeGenerator+"]");
		
		ArchSpringBeanUtil.registerBean(applicationContext, idGeneratorName, idSnowflakeGenerator);
		this.coreLogger.info(this.logPrefix+"将IIdSnowflakeGenerator["+idGeneratorName+"]注入到Spring容器中");
		return applicationContext.getBean(idGeneratorName, IIdSnowflakeGenerator.class);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月25日 下午4:43:43
	 * @version 1.0
	 * @description 创建一个基于SnowFlake实现并且配备Pool的IdGenerator，同时会注入到Spring容器中  
	 * @param idGeneratorName
	 * @param idGeneratorProperties
	 * @param idGeneratorProps Holder中存储的idGenerator的相关属性，
	 * 			传入到这里是为了将相关需要记录属性添加到idGeneratorProps
	 * @return
	 */
	private IIdGenerator createIdGeneratorForPoolSnowFlake(String idGeneratorName,
			IdGeneratorProperties idGeneratorProperties, IdGeneratorProps idGeneratorProps) {
		int deltaSecondBits = idGeneratorProperties.getDeltaSecondBits();
		int workerIdBits = idGeneratorProperties.getWorkerIdBits();
		int sequenceBits = idGeneratorProperties.getSequenceBits();
		
		long workerId = createWorkId(idGeneratorName, idGeneratorProperties, idGeneratorProps);
		
		String startDateStr = idGeneratorProperties.getStartDateStr();
		
		Integer poolSizeBoostPower = idGeneratorProperties.getPoolSizeBoostPower();
		Integer poolPaddingThresholdPercentage = idGeneratorProperties.getPoolPaddingThresholdPercentage();
		Boolean usingSchedule = idGeneratorProperties.getUsingSchedule();
		Long scheduleInterval = idGeneratorProperties.getScheduleInterval();
		
		idGeneratorProps.setDeltaSecondBits(deltaSecondBits);
		idGeneratorProps.setWorkerIdBits(workerIdBits);
		idGeneratorProps.setSequenceBits(sequenceBits);
		idGeneratorProps.setWorkerId(workerId);
		idGeneratorProps.setStartDateStr(startDateStr);
		idGeneratorProps.setUsingSchedule(usingSchedule);
		idGeneratorProps.setScheduleInterval(scheduleInterval);
		
		IIdPoolSnowflakeGenerator idPoolSnowflakeGenerator = IdPoolSnowflakeGeneratorBuilder.create()
			.setDeltaSecondBits(deltaSecondBits)
			.setWorkerIdBits(workerIdBits)
			.setSequenceBits(sequenceBits)
			.setWorkerId(workerId)
			.setStartDateStr(startDateStr)
			.setPoolSizeBoostPower(poolSizeBoostPower)
			.setPoolPaddingThresholdPercentage(poolPaddingThresholdPercentage)
			.setUsingSchedule(usingSchedule)
			.setScheduleInterval(scheduleInterval)
			.build();
		this.coreLogger.info(this.logPrefix+"创建完成IIdPoolSnowflakeGenerator：["+idPoolSnowflakeGenerator+"]");
		
		ArchSpringBeanUtil.registerBean(applicationContext, idGeneratorName, idPoolSnowflakeGenerator);
		this.coreLogger.info(this.logPrefix+"将IIdPoolSnowflakeGenerator["+idGeneratorName+"]注入到Spring容器中");
		return applicationContext.getBean(idGeneratorName, IIdPoolSnowflakeGenerator.class);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月23日 下午1:02:31
	 * @version 1.0
	 * @param idGeneratorName
	 * @param idGeneratorProperties
	 * @param idGeneratorProps Holder中存储的idGenerator的相关属性，
	 * 			传入到这里是为了将相关需要记录属性添加到idGeneratorProps
	 * @return
	 */
	private long createWorkId(String idGeneratorName, IdGeneratorProperties idGeneratorProperties,
			IdGeneratorProps idGeneratorProps) {
		String workerIdType = idGeneratorProperties.getWorkerIdType();
		int workerIdBits = idGeneratorProperties.getWorkerIdBits();
		
		IWorkerIdGenerator workerIdGenerator = null;
		switch (workerIdType.toLowerCase()) {
		case IdConstants.WorkerIdType.RANDOM:
			IWorkerIdRandomGenerator workerIdGenerator0 = new WorkerIdGeneratorImpl();
			workerIdGenerator0.setRandom(workerIdBits);
			workerIdGenerator = workerIdGenerator0;
			idGeneratorProps.setWorkerIdType(IdConstants.WorkerIdType.RANDOM);
			break;
		case IdConstants.WorkerIdType.CUSTOMIZE:
			long customWorkerId = idGeneratorProperties.getCustomWorkerId();
			IWorkerIdCustomGenerator workerIdGenerator1 = new WorkerIdGeneratorImpl();
			workerIdGenerator1.setCustomWorkId(customWorkerId);
			workerIdGenerator = workerIdGenerator1;
			idGeneratorProps.setWorkerIdType(IdConstants.WorkerIdType.CUSTOMIZE);
			break;
		case IdConstants.WorkerIdType.DB:
			IWorkerIdDbGenerator workerIdGenerator2 = new WorkerIdGeneratorImpl();
			String dataSourceType = idGeneratorProperties.getDataSourceType();
			String dataSourceName = idGeneratorProperties.getDataSourceName();
			DataSource dataSource = null;
			if(DataSourceConstants.Type.DRUID.equalsIgnoreCase(dataSourceType)) {
				dataSource = druidDataSourceHolder.get(dataSourceName);
			}else if(DataSourceConstants.Type.SHARDING_SPHERE.equalsIgnoreCase(dataSourceType)) {
				dataSource = shardingDataSourceHolder.get(dataSourceName);
			}else {
				throw new IdSnowflakeException(this.logPrefix + "id生成器["+idGeneratorName+"]创建WorkerIdGenerator时，请配置正确的DataSourceType");
			}
			try {
				String platform = this.archInfoGenerator.getPlatform();
				String appName = this.archInfoGenerator.getAppName();
				String hostIp = this.archInfoGenerator.getHostIp();
				String port = this.archInfoGenerator.getPort();
				int type = ArchDockerUtil.isDocker()==true?ContainerConstants.Type.CONTAINER : ContainerConstants.Type.ACTUAL;
				workerIdGenerator2.setDB(workerIdBits, dataSource, platform, appName, idGeneratorName, hostIp, port, type);
			} catch (SQLException e) {
				throw new IdSnowflakeException(this.logPrefix + "id生成器["+idGeneratorName+"]通过数据库获取WorkerId失败");
			}
			workerIdGenerator = workerIdGenerator2;
			
			idGeneratorProps.setWorkerIdType(IdConstants.WorkerIdType.DB);
			idGeneratorProps.setDataSourceType(dataSourceType);
			idGeneratorProps.setDataSourceName(dataSourceName);
			break;
		default:
			throw new IdSnowflakeException(this.logPrefix + "["+idGeneratorName+"]，请配置正确的workerIdType");
		}
		long workerId = workerIdGenerator.assignWorkerId();
		idGeneratorProps.setWorkerId(workerId);
		return workerId;
	}
	
}
