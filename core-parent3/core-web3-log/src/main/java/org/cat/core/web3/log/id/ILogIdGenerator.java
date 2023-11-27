package org.cat.core.web3.log.id;

/**
 * 
 * @author 王云龙
 * @date 2021年8月18日 下午3:51:02
 * @version 1.0
 * @description 定义LogId生成标准接口
 *
 */
public interface ILogIdGenerator {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午3:51:37
	 * @version 1.0
	 * @description 获取一个符合标准的全局唯一的LogId  
	 * @return
	 */
	public String getLogId();
}
