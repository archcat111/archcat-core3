package org.cat.support.id3.generator.snowflake.workerid;

/**
 * 
 * @author 王云龙
 * @date 2021年10月12日 下午6:14:49
 * @version 1.0
 * @description Snowflake Id算法中生成workerId的生成器接口
 *
 */
public interface IWorkerIdGenerator {
	
	public long assignWorkerId();

}
