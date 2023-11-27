package org.cat.support.id3.generator.snowflake.workerid;


public interface IWorkerIdRandomGenerator extends IWorkerIdGenerator {
	
	public boolean isRandom();

	public void setRandom(int workerIdBits);
}
