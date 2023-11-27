package org.cat.support.id3.generator.snowflake.workerid;

import java.sql.SQLException;

import javax.sql.DataSource;

public interface IWorkerIdDbGenerator extends IWorkerIdGenerator {
	
	public boolean isDB();

	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月22日 下午6:02:48
	 * @version 1.0
	 * @description TODO 
	 * @param workerIdBits
	 * @param dataSource
	 * @param platform
	 * @param appName
	 * @param appModule
	 * @param hostIp
	 * @param port
	 * @param type CONTAINER or ACTUAL
	 * @throws SQLException
	 */
	void setDB(int workerIdBits, DataSource dataSource, String platform, String appName, String appModule, String hostIp, String port, int type)
			throws SQLException;
}
