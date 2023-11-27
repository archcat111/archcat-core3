package org.cat.core.web3.log.event;

import java.lang.annotation.*;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月17日 下午10:12:17
 * @version 1.0
 * @description 标记了该注解的类或者方法不会被记录EventLog，优先级高于{@code EventLog}
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface IgnoreEventLog {
}
