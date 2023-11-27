package org.cat.support.id3.generator.pool.handler;

/**
 * 
 * @author 王云龙
 * @date 2021年10月13日 下午3:56:46
 * @version 1.0
 * @param <T>
 * @description id缓冲池拒绝给新id时的处理函数
 *
 */
@FunctionalInterface
public interface LPoolRejectedTakeHandler<T> {
	
	void rejectPut(T t);
}
