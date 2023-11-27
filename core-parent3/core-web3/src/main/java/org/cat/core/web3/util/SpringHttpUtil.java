package org.cat.core.web3.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.cat.core.util3.map.MapUtil;
import org.cat.core.util3.string.ArchRegexUtil;

/**
 * 
 * @author 王云龙
 * @date 2021年8月18日 下午5:37:02
 * @version 1.0
 * @description 基于Spring框架的处理用户请求Http的工具类
 *
 */
public class SpringHttpUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午5:41:46
	 * @version 1.0
	 * @description 获取用户请求的URI 
	 * @return 用户请求的URI 
	 */
	public static String getClientHttpUri() {
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		if(httpServletRequest==null) {
			return null;
		}
		String clientUri = httpServletRequest.getRequestURI();
		return clientUri;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午5:44:43
	 * @version 1.0
	 * @description 获取用户请求的HTTP method 
	 * @return HTTP method，如：get、post等
	 */
	public static String getClientHttpMethod() {
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		if(httpServletRequest==null) {
			return null;
		}
		String clientHttpMethod = httpServletRequest.getMethod();
		return clientHttpMethod;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午6:04:08
	 * @version 1.0
	 * @description 从Http Uri中获取用户请求的API的版本 
	 * @return 用户请求的API的版本 
	 */
	public static String getClientApiVersion() {
		String REGEX_API_VERSION="v[0-9]{0,1}";
		String clientHttpUri = getClientHttpUri();
		if(clientHttpUri==null) {
			return null;
		}
		String clientApiVersion = ArchRegexUtil.getRegexFirstPartStr(clientHttpUri, REGEX_API_VERSION);
		return clientApiVersion;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月19日 下午3:00:09
	 * @version 1.0
	 * @description 获取HttpServletRequest中的Parameters，并删除其中指定的Parameters，将其余的Parameters进行返回
	 * 		注意：这里并不会删除HttpServletRequest中的Parameters 
	 * @param removeParams 需要删除的Parameters
	 * @return 返回一个不包含指定需要删除Parameters的Map<String, String[]>
	 */
	public static Map<String, String[]> getClientHttpParamsAndRemove(String...removeParams){
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		if(httpServletRequest==null) {
			return null;
		}
		
		Map<String, String[]> params = httpServletRequest.getParameterMap();
		Map<String, String[]> newParams =null;
		if (params != null && params.size() > 0) {
			newParams = MapUtil.removeEntities(params, removeParams);
		}
		return newParams;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月19日 下午3:01:57
	 * @version 1.0
	 * @description 获取HttpServletRequest中的Parameters，并加密其中指定的Parameters，将所有的Parameters进行返回
	 * 		注意：这里并不会加密HttpServletRequest中的Parameters 
	 * @param encryptParams 需要加密的HttpServletRequest中的Parameters
	 * @param encryptReplaceStr 需要替换的属性值，比如将：password=abc替换成password=***
	 * @return 返回一个加密后的Parameters的Map<String, String[]>
	 */
	public static Map<String, String[]> getClientHttpParamsAndEncrypt(String[] encryptParams, String[] encryptReplaceStr){
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		if(httpServletRequest==null) {
			return null;
		}
		
		Map<String, String[]> params = httpServletRequest.getParameterMap();
		Map<String, String[]> newParams =null;
		if (params != null && params.size() > 0) {
			newParams = MapUtil.encryptEntities(params, encryptParams, encryptReplaceStr);
		}
		return newParams;
	}
	

}
