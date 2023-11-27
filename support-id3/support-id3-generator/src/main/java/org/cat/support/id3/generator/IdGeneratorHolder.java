package org.cat.support.id3.generator;

import java.util.Date;
import java.util.Map;

import org.cat.core.util3.map.SourceHolder;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年10月21日 下午5:19:54
 * @version 1.0
 * @description IdGenerator的Holder
 * 		持有IdGenerator类型的IdGenerator，保存在父类的sourceMapper
 * 		本类中的idGeneratorPropsMapper持有的是每一个IdGenerator的一些附加属性，如类型，真实Class信息等，方便后续查找和管理
 *
 */
public class IdGeneratorHolder extends SourceHolder<IIdGenerator> {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月23日 上午10:40:28
	 * @version 1.0
	 * @description 返回value为本类顶层泛型Class的Map 
	 * @return
	 */
	public Map<String, IIdGenerator> getIdGeneratorMapper(){
		Map<String, IIdGenerator> idGeneratorMap = Maps.newHashMap();
		this.getSelfSourceMapper().forEach((name, idGenerator) -> {
			idGeneratorMap.put(name, idGenerator);
		});
		return idGeneratorMap;
	}
	
	//key为idGenerator的一些附加属性
	private Map<String, IdGeneratorProps> idGeneratorPropsMapper = Maps.newHashMap();
	
	public IdGeneratorProps getIdGeneratorProps(String idGeneratorName) {
		IdGeneratorProps idGeneratorProps = this.idGeneratorPropsMapper.get(idGeneratorName);
		return idGeneratorProps;
	}
	
	public void setIdGeneratorProps(String idGeneratorName, IdGeneratorProps idGeneratorProps) {
		this.idGeneratorPropsMapper.put(idGeneratorName, idGeneratorProps);
	}
	
	@Getter
	@Setter
	public static class IdGeneratorProps {
		private String type; ////IdConstants.Type.UUID
		private Class<?> clazz;
		private Date createDate;
		
		private Integer deltaSecondBits = null;
		private Integer workerIdBits = null;
		private Integer sequenceBits = null;
		private String startDateStr = null;
		
		private String workerIdType; //IdConstants.WorkerIdType.RANDOM
		private long workerId;
		
		private String dataSourceType = null; //workerIdType = IdConstants.WorkerIdType.DB时使用，DataSourceConstants.DataSourceType.DRUID;
		private String dataSourceName = null; //workerIdType = IdConstants.WorkerIdType.DB时使用
		
		private boolean usingSchedule;
		private long scheduleInterval;
	}
	
}
