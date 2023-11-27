package org.cat.core.web3.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.WebRequest;

/**
 * 
 * @author wangyunlong
 * @date 2018年7月28日 上午10:55:28
 * @version 1.0
 * @description ajax的工具类
 *
 */
public class AjaxUtil {
	/**
	 * 
	 * @author wangyunlong
	 * @date 2018年7月28日 上午10:59:00
	 * @version 1.0
	 * @description 判断请求是否是Ajax 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header0 = request.getHeader("X-Requested-With");
		if("XMLHttpRequest".equals(header0)) {
			return true;
		}
		String header1 = request.getHeader("accept");
		if(header1!=null && header1.contains("application/json")) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isAjaxRequest(WebRequest request) {
		String header0 = request.getHeader("X-Requested-With");
		if("XMLHttpRequest".equals(header0)) {
			return true;
		}
		String header1 = request.getHeader("accept");
		if(header1!=null && header1.contains("application/json")) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isAjaxResponse(HttpServletResponse httpServletResponse) {
		String header0 = httpServletResponse.getHeader("content-type");
		if("application/json".equals(header0)) {
			return true;
		}
		return false;
	}
}
