package org.cat.core.exception3.id;

/**
 * 
 * @author 王云龙
 * @date 2021年7月13日 下午6:42:08
 * @version 1.0
 * @description 定义异常ID生成标准接口
 *
 */
public interface IExceptionIdGenerator {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午6:42:54
	 * @version 1.0
	 * @description 获取一个符合标准的全局唯一的ExceptionId
	 * @return
	 */
	public String getExceptionId();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午6:46:01
	 * @version 1.0
	 * @description 判断一个exceptionId是否是正确的 
	 * @param exceptionId
	 * @return
	 */
	public boolean isTrueExceptionId(String exceptionId);
}
