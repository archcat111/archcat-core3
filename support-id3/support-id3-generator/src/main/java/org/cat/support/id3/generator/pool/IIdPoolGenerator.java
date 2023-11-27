package org.cat.support.id3.generator.pool;

import org.cat.support.id3.generator.IIdGenerator;

/**
 * 
 * @author 王云龙
 * @date 2022年4月21日 下午4:20:47
 * @version 1.0
 * @description 基于缓存池的Id生成器的顶层接口
 *
 */
public interface IIdPoolGenerator extends IIdGenerator {
	
	public void setIdPool(IIdPool idPool);
	
	public void setIdPoolPutExecutor(IIdPoolPutExecutor idPoolPutExecutor);
	
	public void destroy();
	
}
