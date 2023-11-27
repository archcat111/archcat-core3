package org.cat.core.web3.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;

/**
 * 
 * @author 王云龙
 * @date 2021年8月18日 下午5:31:49
 * @version 1.0
 * @description 基于Spring框架处理IP的工具类
 *
 */
public class SpringIpUtil extends IpUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午5:23:17
	 * @version 1.0
	 * @description 通过HttpServletRequest对象获取客户端IP  
	 * 		注意：内部调用：{@linkplain IpUtil#getRequestClientIps(RequestAttributes)}
	 * @return 例如：222.140.93.122 , 55.34.156.92 , 192.168.10.2
	 */
	public static String getRequestClientIps() {
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		if(httpServletRequest==null) {
			return null;
		}
		String ips = getRequestClientIps(httpServletRequest);
		return ips;
	}
}
