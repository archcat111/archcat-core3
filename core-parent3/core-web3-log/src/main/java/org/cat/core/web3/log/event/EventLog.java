package org.cat.core.web3.log.event;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.cat.core.web3.log.constants.EventLogConstants;

/**
 * 
 * @author 王云龙
 * @date 2017年3月8日 下午5:59:40
 * @version 1.0
 * @description 标记了该注解的类或者方法会被记录EventLog
 * 		注意：如果在类上标记@EventLog，在方法上标记@IgnoreEventLog，那么该类中没有标记@IgnoreEventLog的方法会记录EventLog
 * 		注意：如果在类上或者方法上同时标记了@EventLog和@IgnoreEventLog，那么该类或者该方法不会记录EventLog
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface EventLog {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午3:14:43
	 * @version 1.0
	 * @description eventLog的类型 {@linkplain EventLogConstants.Type} 
	 * @return
	 */
	String type() default EventLogConstants.Type.UNKNOWN;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午3:45:38
	 * @version 1.0
	 * @description api的名称 {@linkplain EventLogConstants.ApiName} 
	 * @return
	 */
	String apiName() default EventLogConstants.ApiName.UNKONWN;
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年3月8日 下午6:31:28
	 * @version 1.0
	 * @description 描述
	 *
	 * @return
	 */
	String description() default "";
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 上午10:44:18
	 * @version 1.0
	 * @description 分组，不同分组的Eventlog，不会共享透传 
	 * @return
	 */
	String group() default "default";
	
	
	
}
