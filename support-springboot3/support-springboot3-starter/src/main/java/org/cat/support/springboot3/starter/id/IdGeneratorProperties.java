package org.cat.support.springboot3.starter.id;

import org.cat.support.id3.generator.IdConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdGeneratorProperties {
	private boolean enabled = true;
	
	private String type; //IdConstants.Type
	
	/**snowflake**/
	private Integer deltaSecondBits = 28;
	private Integer workerIdBits = 22;
	private Integer sequenceBits = 13;
	private String startDateStr = "2021-10-20";
	
	private String workerIdType = IdConstants.WorkerIdType.RANDOM;
	private Long customWorkerId = 0L; //workerIdType = IdConstants.WorkerIdType.CUSTOMIZE时使用
	private String dataSourceType = null; //workerIdType = IdConstants.WorkerIdType.DB时使用，DataSourceConstants.DataSourceType.DRUID;
	private String dataSourceName = null; //workerIdType = IdConstants.WorkerIdType.DB时使用
	
	
	
	
	/**pool**/
	//sequenceBits = 13时，1秒内最多有8191个id，poolSizeBoostPower=3则缓存长度为65528，为2时长度为32764，为1时长度为16382
	private Integer poolSizeBoostPower = 3;
	private Integer poolPaddingThresholdPercentage = 50;
	/**pool schedule**/
	private Boolean usingSchedule = true;
	private Long scheduleInterval = 5 * 60L;
}
