package org.cat.support.log3.generator.id;

/**
 * 
 * @author 王云龙
 * @date 2021年8月27日 下午3:05:26
 * @version 1.0
 * @description LogId生成器，需要set进来一个IdGenerator才会起作用
 * 		其根据set进来的IdGenerator的不同，会生成不同规则的Id
 *
 */
public class LogIdGeneratorImpl extends AbsLogIdGenerator {

	@Override
	public String getLogId() {
		String logId = this.getIdGenerator().getStrId();
		return logId;
	}
	
}
