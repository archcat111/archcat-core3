package org.cat.support.exception3.generator.id;

import java.util.UUID;

import org.cat.core.exception3.exception.StandardRuntimeException;

/**
 * 
 * @author 王云龙
 * @date 2021年8月27日 下午3:05:26
 * @version 1.0
 * @description 默认的ExceptionId生成器
 *
 */
public class ExceptionIdGeneratorImpl extends AbsExceptionIdGenerator {

	@Override
	public String getExceptionId() {
		//6b7b417c-0f3b-4fcd-91d0-a5868b7d05c6
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}

	@Override
	public boolean isTrueExceptionId(String exceptionId) {
		throw new StandardRuntimeException("ExceptionIdGeneratorImpl不支持设置isTrueExceptionId");
	}
	
}
