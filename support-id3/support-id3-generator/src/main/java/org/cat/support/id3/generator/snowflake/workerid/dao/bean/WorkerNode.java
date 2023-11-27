package org.cat.support.id3.generator.snowflake.workerid.dao.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2022年4月22日 下午4:46:55
 * @version 1.0
 * @description 用于执行SQL时的传参
 *
 */
@Getter
@Setter
public class WorkerNode{
	
	private String platform;
	private String appName;
	private String appModule;
	private String hostIp;
	private String port;
	private int type; //CONTAINER or ACTUAL
	private Date lastStartTimestamp;
	private Date createTimestamp;
	private Date lastUpdateTimestamp;
	
}
