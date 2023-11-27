package org.cat.core.web3.user;

/**
 * 
 * @author 王云龙
 * @date 2021年8月25日 下午2:54:03
 * @version 1.0
 * @description 用户信息相关标准接口
 *
 */
public interface IUserGenerator {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午2:57:21
	 * @version 1.0
	 * @description 获取UserCode 
	 * @return UserCode
	 */
	public String getUserCode();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年6月1日 下午1:54:29
	 * @version 1.0
	 * @description 获取UserCode（Long类型） 
	 * @return
	 */
	public Long getUserCodeForLong();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午2:57:31
	 * @version 1.0
	 * @description 获取UserName 
	 * @return UserName
	 */
	public String getUserName();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月20日 下午4:12:39
	 * @version 1.0
	 * @description 获取用户昵称 
	 * @return
	 */
	public String getNickName();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月20日 下午4:46:59
	 * @version 1.0
	 * @description 获取用户属性 
	 * @param paramName
	 * @return
	 */
	public String getUserParam(String paramName);
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午2:57:51
	 * @version 1.0
	 * @description 获取SessionId 
	 * @return 获取SessionId
	 */
	public String getSessionId();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年8月1日 下午5:44:12
	 * @version 1.0
	 * @description 获取用户登录设备 
	 * @return
	 */
	public String getDevice();
}
