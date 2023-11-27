package org.cat.core.exception3.code;

/**
 * 
 * @author 王云龙
 * @date 2021年7月13日 下午6:39:01
 * @version 1.0
 * @description 自定义异常code生成标准接口
 *
 */
public interface IExceptionCodeGenerator {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午6:40:12
	 * @version 1.0
	 * @description 获取一个微服务的唯一Code 
	 * @return
	 */
	public String getExceptionProjectCode();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月27日 下午4:04:52
	 * @version 1.0
	 * @description 给该ExceptionCodeGenerator设置projectCode
	 * 		 这个方法会在系统初始化时根据配置中配置的cat.support.exception.projectCode填写的value自动进行设置
	 * @param projectCode 配置中的cat.support.exception.projectCode
	 * @return
	 */
	public void setProjectCode(String projectCode);
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午6:45:06
	 * @version 1.0
	 * @description 判断一个exceptionProjectCode是否是正确的 
	 * @param exceptionProjectCode
	 * @return
	 */
	public boolean isTrueExceptionProjectCode(String exceptionProjectCode);
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午6:39:54
	 * @version 1.0
	 * @description 获取一个符合标准的微服务内部唯一的业务场景ExceptionCode 
	 * @return businessExceptionCode
	 */
	public String getExceptionBusinessCode();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午6:45:26
	 * @version 1.0
	 * @description 判断一个业务exceptionCode是否是正确的
	 * @param exceptionCode
	 * @return true or false
	 */
	public boolean isTrueExceptionBusinessCode(String exceptionCode);

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月26日 下午4:10:15
	 * @version 1.0
	 * @description 当发生系统的异常而并非自定义异常时，程序员并没有给该异常设置code
	 * 		那么就需要ExceptionCodeGenerator给自定义异常设置code，这时就用到该方法
	 * 		用户方发现：例如：Exception中的code为-2，就知道是一个系统异常而非自定义异常了
	 * @return
	 */
	public String getExceptionSystemCode();

	public boolean isTrueExceptionSystemCode(String exceptionCode);
	
}
