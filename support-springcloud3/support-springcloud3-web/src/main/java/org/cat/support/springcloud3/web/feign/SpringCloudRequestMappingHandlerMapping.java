package org.cat.support.springcloud3.web.feign;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class SpringCloudRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

	/**
	 * 默认Spring判断一个类的逻辑如下：
	 * 		(AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
	 * 		AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class));
	 * 		即：如果被标注了@Controller或者@RequestMapping，都会被认为是Controller类实例
	 * 因此，会有一个问题：
	 * 		当接口同时有如下注解，则会报错：@RequestMapping annotation not allowed on @FeignClient interfaces
	 * 		假设：
	 * 		@FeignClient(name = "app-user3-impl", url="${app-user3-impl.feign.url:}")
	 * 		@RequestMapping(path = "/api/v1/users")
	 * 		public interface UserFeignClient {}
	 * 		并且这个实例被其他服务引入，那么就会报错：@RequestMapping annotation not allowed on @FeignClient interfaces
	 * 	这里我们要做的事情是：
	 * 		如果一个接口上有@FeignClient，不要让Spring当作Controller来处理
	 * 		重写WebMvcRegistrations的实现来调用该类的isHandler(Class<?> beanType)方法
	 */
	@Override
	protected boolean isHandler(Class<?> beanType) {
		return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class);
	}

	
}
