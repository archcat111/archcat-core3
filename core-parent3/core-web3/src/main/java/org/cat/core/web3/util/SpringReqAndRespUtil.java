package org.cat.core.web3.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
 * @author 王云龙
 * @date 2021年8月18日 下午5:45:33
 * @version 1.0
 * @description 基于Spring框架处理HttpServletRequest对象的工具类
 *
 */
public class SpringReqAndRespUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午5:46:51
	 * @version 1.0
	 * @description 获取请求中的RequestAttributes对象 
	 * @return RequestAttributes对象 
	 */
	public static RequestAttributes getRequestAttributes() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		return requestAttributes;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午5:48:52
	 * @version 1.0
	 * @description 获取请求中的ServletRequestAttributes对象 
	 * @return ServletRequestAttributes对象 
	 */
	public static ServletRequestAttributes getServletRequestAttributes() {
		RequestAttributes requestAttributes = getRequestAttributes();
		if(requestAttributes==null) {
			return null;
		}
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
		return servletRequestAttributes;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午5:50:05
	 * @version 1.0
	 * @description 获取请求中的HttpServletRequest对象
	 * @return HttpServletRequest对象
	 */
	public static HttpServletRequest getHttpServletRequest() {
		ServletRequestAttributes servletRequestAttributes = getServletRequestAttributes();
		if(servletRequestAttributes==null) {
			return null;
		}
		HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
		return httpServletRequest;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 上午11:00:44
	 * @version 1.0
	 * @description 获取同一个请求中的HttpServletResponse对象 
	 * @return HttpServletResponse对象
	 */
	public static HttpServletResponse getHttpServletResponse() {
		ServletRequestAttributes servletRequestAttributes = getServletRequestAttributes();
		HttpServletResponse httpServletResponse = servletRequestAttributes.getResponse();
		return httpServletResponse;
	}
}
