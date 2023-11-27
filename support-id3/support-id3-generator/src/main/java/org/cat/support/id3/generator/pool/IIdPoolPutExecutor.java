package org.cat.support.id3.generator.pool;

/**
 * 
 * @author 王云龙
 * @date 2021年10月14日 上午11:05:47
 * @version 1.0
 * @description id pool的填充id的执行器接口
 *
 */
public interface IIdPoolPutExecutor {
	
	public void setScheduleInterval(long scheduleInterval);
	
	public void startPoolPaddingSchedule();
	
	public void shutdown();
	
	public void asyncPaddingIdToPool();
	
	public void paddingIdToPool();
	
	public boolean isRunningPaddingIdPool();

}
