package org.cat.core.web3.util;

import java.lang.reflect.Method;

import org.cat.core.web3.exception.ControllerSpringException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 
 * @author 王云龙
 * @date 2021年8月31日 下午5:58:56
 * @version 1.0
 * @description 处理Spring Controller的工具类
 *
 */
public class SpringControllerUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月31日 下午6:47:48
	 * @version 1.0
	 * @description 将多个url paths和其对应的Controller Method处理关系动态注册到Spring容器中
	 * 		注意：一个Controller的方法可以同时处理多个url path和多种requestMethod
	 * @param requestMappingHandlerMapping RequestMappingHandlerMapping，可以通过@Autowired获取
	 * @param urlPaths 用户请求的url paths
	 * @param requestMethods GET、POST等
	 * @param controllerInstance 需要处理这些请求的Controller实例，需要先注册到Spring容器中
	 * @param controllerMethod 需要处理该url paths的Controller方法
	 * @param controllerMethodParamTypes 需要处理该url paths的Controller方法的参数类型
	 */
	public static void registerControllerRequestMapping(
			RequestMappingHandlerMapping requestMappingHandlerMapping, 
			String[] urlPaths, 
			RequestMethod[] requestMethods,
			Object controllerInstance,
			String controllerMethod,
			Class<?>... controllerMethodParamTypes) {
		//标识@RequestMapping注解的类，在被扫描到之后，会根据注解里的参数生成RequestMapingInfo对象
		RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(urlPaths).methods(requestMethods).build();
		//就是目标请求处理器的实例
		Method method = null;
		try {
			method = controllerInstance.getClass().getDeclaredMethod(controllerMethod, controllerMethodParamTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ControllerSpringException("在使用ControllerSpringUtil动态注册一个requestMapping时，反射Controller方法时发生错误", e);
		}
		//TODO 这里注册的Controller只能进行页面跳转，不知道如何设置注入@ResponseBody的设定
		requestMappingHandlerMapping.registerMapping(requestMappingInfo, controllerInstance, method);
		
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月31日 下午6:55:21
	 * @version 1.0
	 * @description 将多个url paths和其对应的Controller Method处理关系从Spring容器中动态删除
	 * 		注意：一个Controller的方法可以同时处理多个url path和多种requestMethod
	 * @param requestMappingHandlerMapping RequestMappingHandlerMapping，可以通过@Autowired获取
	 * @param urlPaths 用户请求的url paths
	 * @param requestMethods GET、POST等
	 * @param controllerMethodParamTypes
	 */
	public static void unregisterControllerRequestMapping(
			RequestMappingHandlerMapping requestMappingHandlerMapping, 
			String[] urlPaths, 
			RequestMethod[] requestMethods,
			Class<?>... controllerMethodParamTypes) {
		//标识@RequestMapping注解的类，在被扫描到之后，会根据注解里的参数生成RequestMapingInfo对象
		RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(urlPaths).methods(requestMethods).build();
		requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
		
	}
}
