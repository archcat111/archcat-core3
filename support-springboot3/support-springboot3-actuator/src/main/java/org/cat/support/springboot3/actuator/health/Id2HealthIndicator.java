package org.cat.support.springboot3.actuator.health;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.cat.core.util3.date.ArchDateTimeUtil.FormatConstants;
import org.cat.support.id3.generator.IIdGenerator;
import org.cat.support.id3.generator.IdConstants;
import org.cat.support.id3.generator.IdGeneratorHolder;
import org.cat.support.id3.generator.IdGeneratorHolder.IdGeneratorProps;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Maps;

import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import lombok.Setter;

public class Id2HealthIndicator extends AbstractHealthIndicator {
	
	@Setter
	private IdGeneratorHolder idGeneratorHolder;
	private final String ID_GENERATORS = "idGenerators";
	
	public Id2HealthIndicator() {
	}

	public Id2HealthIndicator(IdGeneratorHolder idGeneratorHolder) {
		this.idGeneratorHolder = idGeneratorHolder;
	}
	
	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		//一般不会进入，因为初始化本Bean的条件之一就是有druidDataSourceHolder实例
		if(this.idGeneratorHolder==null) { 
			builder.status(Status.OUT_OF_SERVICE);
			builder.outOfService().withDetail(ID_GENERATORS, "本应用未使用idGeneratorHolder");
			return;
		}
		
		Map<String, IIdGenerator> idGeneratorMap = this.idGeneratorHolder.getIdGeneratorMapper();
		if(MapUtil.isEmpty(idGeneratorMap)) {
			builder.status(Status.UP);
			builder.unknown().withDetail(ID_GENERATORS, "本应用未初始化idGeneratorHolder");
			return;
		}
		Status status = this.doHealthCheckForIdGenerator(builder, idGeneratorMap);
		builder.status(status);
	}
	
	private Status doHealthCheckForIdGenerator(Builder builder, Map<String, IIdGenerator> idGeneratorMap) {
		Status globalStatus = Status.UP;
		Map<String, IdGeneratorValid> idGeneratorValidDetail = Maps.newHashMap();
		
		for (Entry<String, IIdGenerator> entry : idGeneratorMap.entrySet()) {
			String idGeneratorName = entry.getKey();
			IIdGenerator idGenerator = entry.getValue();
			Status status = this.validIdGeneratorForDefault(idGenerator);
			if(status.equals(Status.DOWN)) {
				globalStatus = Status.DOWN;
			}
			IdGeneratorProps idGeneratorProps = this.idGeneratorHolder.getIdGeneratorProps(idGeneratorName);
			
			
			IdGeneratorValid idGeneratorValid = createIdGeneratorValid(idGeneratorProps);
			idGeneratorValid.setStatus(status);
			idGeneratorValidDetail.put(idGeneratorName, idGeneratorValid);
		}
		
		builder.withDetails(idGeneratorValidDetail);
		return globalStatus;
	}
	
	private Status validIdGeneratorForDefault(IIdGenerator idGenerator) {
		String result = null;
		try {
			result = idGenerator.getStrId();
		} catch (Exception e) {
//			e.printStackTrace();
			result = null;
		}
		return result!=null?Status.UP:Status.DOWN;
	}
	
	private IdGeneratorValid createIdGeneratorValid(IdGeneratorProps idGeneratorProps) {
		IdGeneratorValid idGeneratorValid = null;
		String type = idGeneratorProps.getType();
		if(IdConstants.Type.UUID.equals(type)) {
			idGeneratorValid = new IdGeneratorValid();
			fillingIdGeneratorValid(idGeneratorValid, idGeneratorProps);
		}else if(IdConstants.Type.SNOW_FLAKE.equals(type)) {
			String workerIdType = idGeneratorProps.getWorkerIdType();
			if(IdConstants.WorkerIdType.RANDOM.equals(workerIdType)) {
				IdGeneratorSnowflakeForRandomValid idGeneratorValid0 = new IdGeneratorSnowflakeForRandomValid();
				fillingIdGeneratorSnowflakeForRandomValid(idGeneratorValid0, idGeneratorProps);
				idGeneratorValid = idGeneratorValid0;
			}else if(IdConstants.WorkerIdType.CUSTOMIZE.equals(workerIdType)) {
				IdGeneratorSnowflakeForCustomizeValid idGeneratorValid0 = new IdGeneratorSnowflakeForCustomizeValid();
				fillingIdGeneratorSnowflakeForCustomizeValid(idGeneratorValid0, idGeneratorProps);
				idGeneratorValid = idGeneratorValid0;
			}else if(IdConstants.WorkerIdType.DB.equals(workerIdType)) {
				IdGeneratorSnowflakeForDBValid idGeneratorValid0 = new IdGeneratorSnowflakeForDBValid();
				fillingIdGeneratorSnowflakeForDBValid(idGeneratorValid0, idGeneratorProps);
				idGeneratorValid = idGeneratorValid0;
			}else {
				IdGeneratorSnowflakeValid idGeneratorValid0 = new IdGeneratorSnowflakeValid();
				fillingIdGeneratorSnowflakeValid(idGeneratorValid0, idGeneratorProps);
				idGeneratorValid = idGeneratorValid0;
			}
		}else if(IdConstants.Type.POOL_SNOW_FLAKE.equals(type)) {
			String workerIdType = idGeneratorProps.getWorkerIdType();
			if(IdConstants.WorkerIdType.RANDOM.equals(workerIdType)) {
				IdGeneratorPoolSnowflakeForRandomValid idGeneratorValid0 = new IdGeneratorPoolSnowflakeForRandomValid();
				fillingIdGeneratorPoolSnowflakeForRandomValid(idGeneratorValid0, idGeneratorProps);
				idGeneratorValid = idGeneratorValid0;
			}else if(IdConstants.WorkerIdType.CUSTOMIZE.equals(workerIdType)) {
				IdGeneratorPoolSnowflakeForCustomizeValid idGeneratorValid0 = new IdGeneratorPoolSnowflakeForCustomizeValid();
				fillingIdGeneratorPoolSnowflakeForCustomizeValid(idGeneratorValid0, idGeneratorProps);
				idGeneratorValid = idGeneratorValid0;
			}else if(IdConstants.WorkerIdType.DB.equals(workerIdType)) {
				IdGeneratorPoolSnowflakeForDBValid idGeneratorValid0 = new IdGeneratorPoolSnowflakeForDBValid();
				fillingIdGeneratorPoolSnowflakeForDBValid(idGeneratorValid0, idGeneratorProps);
				idGeneratorValid = idGeneratorValid0;
			}else {
				IdGeneratorPoolSnowflakeValid idGeneratorValid0 = new IdGeneratorPoolSnowflakeValid();
				fillingIdGeneratorPoolSnowflakeValid(idGeneratorValid0, idGeneratorProps);
				idGeneratorValid = idGeneratorValid0;
			}
		}else {
			
		}
		return idGeneratorValid;
	}
	
	private void fillingIdGeneratorValid(IdGeneratorValid idGeneratorValid, IdGeneratorProps idGeneratorProps) {
		idGeneratorValid.setType(idGeneratorProps.getType());
		idGeneratorValid.setClazz(idGeneratorProps.getClazz());
		idGeneratorValid.setCreateDate(idGeneratorProps.getCreateDate());
	}
	
	private void fillingIdGeneratorSnowflakeValid(IdGeneratorSnowflakeValid idGeneratorSnowflakeValid, IdGeneratorProps idGeneratorProps) {
		fillingIdGeneratorValid(idGeneratorSnowflakeValid, idGeneratorProps);
		
		idGeneratorSnowflakeValid.setDeltaSecondBits(idGeneratorProps.getDeltaSecondBits());
		idGeneratorSnowflakeValid.setWorkerIdBits(idGeneratorProps.getWorkerIdBits());
		idGeneratorSnowflakeValid.setSequenceBits(idGeneratorProps.getSequenceBits());
		idGeneratorSnowflakeValid.setStartDateStr(idGeneratorProps.getStartDateStr());
		idGeneratorSnowflakeValid.setWorkerIdType(idGeneratorProps.getWorkerIdType());
		idGeneratorSnowflakeValid.setWorkerId(idGeneratorProps.getWorkerId());
	}
	
	private void fillingIdGeneratorSnowflakeForRandomValid(IdGeneratorSnowflakeForRandomValid idGeneratorSnowflakeForRandomValid, IdGeneratorProps idGeneratorProps) {
		fillingIdGeneratorSnowflakeValid(idGeneratorSnowflakeForRandomValid, idGeneratorProps);
	}
	
	private void fillingIdGeneratorSnowflakeForCustomizeValid(IdGeneratorSnowflakeForCustomizeValid idGeneratorSnowflakeForCustomizeValid, IdGeneratorProps idGeneratorProps) {
		fillingIdGeneratorSnowflakeValid(idGeneratorSnowflakeForCustomizeValid, idGeneratorProps);
	}
	
	private void fillingIdGeneratorSnowflakeForDBValid(IdGeneratorSnowflakeForDBValid idGeneratorSnowflakeForDBValid, IdGeneratorProps idGeneratorProps) {
		fillingIdGeneratorSnowflakeValid(idGeneratorSnowflakeForDBValid, idGeneratorProps);
		idGeneratorSnowflakeForDBValid.setDataSourceType(idGeneratorProps.getDataSourceType());
		idGeneratorSnowflakeForDBValid.setDataSourceName(idGeneratorProps.getDataSourceName());
	}
	
	private void fillingIdGeneratorPoolSnowflakeValid(IdGeneratorPoolSnowflakeValid idGeneratorPoolSnowflakeValid, IdGeneratorProps idGeneratorProps) {
		fillingIdGeneratorSnowflakeValid(idGeneratorPoolSnowflakeValid, idGeneratorProps);
		idGeneratorPoolSnowflakeValid.setUsingSchedule(idGeneratorProps.isUsingSchedule());
		idGeneratorPoolSnowflakeValid.setScheduleInterval(idGeneratorProps.getScheduleInterval());
	}
	
	private void fillingIdGeneratorPoolSnowflakeForRandomValid(IdGeneratorPoolSnowflakeForRandomValid idGeneratorPoolSnowflakeForRandomValid, IdGeneratorProps idGeneratorProps) {
		fillingIdGeneratorPoolSnowflakeValid(idGeneratorPoolSnowflakeForRandomValid, idGeneratorProps);
	}
	
	private void fillingIdGeneratorPoolSnowflakeForCustomizeValid(IdGeneratorPoolSnowflakeForCustomizeValid idGeneratorPoolSnowflakeForCustomizeValid, IdGeneratorProps idGeneratorProps) {
		fillingIdGeneratorPoolSnowflakeValid(idGeneratorPoolSnowflakeForCustomizeValid, idGeneratorProps);
	}
	
	private void fillingIdGeneratorPoolSnowflakeForDBValid(IdGeneratorPoolSnowflakeForDBValid idGeneratorPoolSnowflakeForDBValid, IdGeneratorProps idGeneratorProps) {
		fillingIdGeneratorPoolSnowflakeValid(idGeneratorPoolSnowflakeForDBValid, idGeneratorProps);
		idGeneratorPoolSnowflakeForDBValid.setDataSourceType(idGeneratorProps.getDataSourceType());
		idGeneratorPoolSnowflakeForDBValid.setDataSourceName(idGeneratorProps.getDataSourceName());
	}
	
	@Setter
	@Getter
	public static class IdGeneratorValid {
		private String type; //IdConstants.Type.UUID
		private Class<?> clazz;
		@JsonFormat(pattern = FormatConstants.DATETIME_NORMAL)
		private Date createDate;
		
		private Status status;
	}
	
	@Setter
	@Getter
	public static class IdGeneratorSnowflakeValid extends IdGeneratorValid{
		private Integer deltaSecondBits = null;
		private Integer workerIdBits = null;
		private Integer sequenceBits = null;
		private String startDateStr = null;
		
		private String workerIdType; //IdConstants.WorkerIdType.RANDOM
		private long workerId;
	}
	
	@Setter
	@Getter
	public static class IdGeneratorSnowflakeForRandomValid extends IdGeneratorSnowflakeValid{
		
	}
	
	@Setter
	@Getter
	public static class IdGeneratorSnowflakeForCustomizeValid extends IdGeneratorSnowflakeValid{
	}
	
	@Setter
	@Getter
	public static class IdGeneratorSnowflakeForDBValid extends IdGeneratorSnowflakeValid{
		private String dataSourceType = null; //workerIdType = IdConstants.WorkerIdType.DB时使用，DataSourceConstants.DataSourceType.DRUID;
		private String dataSourceName = null; //workerIdType = IdConstants.WorkerIdType.DB时使用
	}
	
	@Setter
	@Getter
	public static class IdGeneratorPoolSnowflakeValid extends IdGeneratorSnowflakeValid{
		private boolean usingSchedule;
		private long scheduleInterval;
	}
	
	@Setter
	@Getter
	public static class IdGeneratorPoolSnowflakeForRandomValid extends IdGeneratorPoolSnowflakeValid{
		
	}
	
	@Setter
	@Getter
	public static class IdGeneratorPoolSnowflakeForCustomizeValid extends IdGeneratorPoolSnowflakeValid{
	}
	
	@Setter
	@Getter
	public static class IdGeneratorPoolSnowflakeForDBValid extends IdGeneratorPoolSnowflakeValid{
		private String dataSourceType = null; //workerIdType = IdConstants.WorkerIdType.DB时使用，DataSourceConstants.DataSourceType.DRUID;
		private String dataSourceName = null; //workerIdType = IdConstants.WorkerIdType.DB时使用
	}
}
