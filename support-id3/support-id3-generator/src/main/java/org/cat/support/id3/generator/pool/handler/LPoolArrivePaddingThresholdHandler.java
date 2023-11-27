package org.cat.support.id3.generator.pool.handler;

/**
 * 
 * @author 王云龙
 * @date 2021年10月14日 下午1:55:35
 * @version 1.0
 * @description 当Id Pool中的id数量到达了填充的阈值的处理器
 *
 * @param <T> Id Pool
 */
@FunctionalInterface
public interface LPoolArrivePaddingThresholdHandler<T> {
	void asyncPaddingIdToPool(T t);
}
