package org.cat.support.id3.generator.snowflake.workerid;


public interface IWorkerIdCustomGenerator extends IWorkerIdGenerator {
	
	public boolean isCustomize();

	public void setCustomWorkId(long workerId);
}
