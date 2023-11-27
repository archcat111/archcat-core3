package org.cat.core.web3.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 
 * @author 王云龙
 * @date 2021年8月18日 下午5:29:27
 * @version 1.0
 * @description 基于Spring框架进行Session处理的工具类
 *
 */
public class SpringSessionUtil {
	
	public static HttpSession getSpringSession(boolean create) {
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		HttpSession httpSession = httpServletRequest.getSession(create);
		return httpSession;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午5:25:32
	 * @version 1.0
	 * @description 获取请求中的SessionId 
	 * 		调用：{@linkplain RequestContextHolder#getRequestAttributes()}
	 * @return
	 */
	public static String getSpringSessionId() {
		RequestAttributes requestAttributes = SpringReqAndRespUtil.getRequestAttributes();
		String sessionId = requestAttributes.getSessionId();
		return sessionId;
	}
	
	public static void setSpringSessionAttr(String name, Object object) {
		setSpringSessionAttr(name, object, true);
	}
	
	public static void setSpringSessionAttr(String name, Object object, boolean autoCreateSession) {
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		HttpSession httpSession = httpServletRequest.getSession(autoCreateSession);
		httpSession.setAttribute(name, object);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getSpringSessionAttr(String name , Class<T> clazz) {
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		HttpSession httpSession = httpServletRequest.getSession(true);
		Object object = httpSession.getAttribute(name);
		if(object==null) {
			return null;
		}
		return (T)object;
	}
	
	public static void removeSpringSessionAttr(String name) {
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		HttpSession httpSession = httpServletRequest.getSession(true);
		httpSession.removeAttribute(name);
	}
	
	public static void removeSpringSession() {
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		HttpSession httpSession = httpServletRequest.getSession(true);
		httpSession.invalidate();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 下午5:29:54
	 * @version 1.0
	 * @description 获取Session中某个属性对应的值，这个值可能是任何对象 
	 * @param <T> 值的对象类型
	 * @param httpSession HttpSession对象
	 * @param attrName session中需要获取属性值的属性名
	 * @param clazz session中需要获取的属性值的Class类型
	 * @return 属性值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSessionAttrValue(HttpSession httpSession,String attrName, Class<T> clazz) {
		Object object = httpSession.getAttribute(attrName);
		if(object!=null) {
			return (T)object;
		}else {
			return null;
		}
	}
}
