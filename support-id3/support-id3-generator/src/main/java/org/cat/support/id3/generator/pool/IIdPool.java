package org.cat.support.id3.generator.pool;

import org.cat.support.id3.generator.pool.handler.LPoolArrivePaddingThresholdHandler;
import org.cat.support.id3.generator.pool.handler.LPoolRejectedPutHandler;
import org.cat.support.id3.generator.pool.handler.LPoolRejectedTakeHandler;

/**
 * 
 * @author 王云龙
 * @date 2022年4月21日 下午4:19:13
 * @version 1.0
 * @description Id 缓存池的顶层接口
 *
 */
public interface IIdPool{
	
	public long take();
	
	public boolean put(long id);
	
	public void setPoolRejectedPutHandler(LPoolRejectedPutHandler<IIdPool> poolRejectedPutHandler);

	public void setPoolRejectedTakeHandler(LPoolRejectedTakeHandler<IIdPool> poolRejectedTakeHandler);

	public void setPoolArrivePaddingThresholdHandler(
			LPoolArrivePaddingThresholdHandler<IIdPool> poolArrivePaddingThresholdHandler);
}
