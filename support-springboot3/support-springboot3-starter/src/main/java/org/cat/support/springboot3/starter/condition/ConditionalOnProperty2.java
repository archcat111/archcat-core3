package org.cat.support.springboot3.starter.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;

/**
 * 
 * @author 王云龙
 * @date 2021年9月9日 下午2:24:08
 * @version 1.0
 * @description {@linkplain ConditionalOnProperty}的扩展
 * 		通过prefix获取配置前缀，通过name匹配属性名称，并且获得该name对应的value，然后和havingValue进行对比
 * 		{@linkplain ConditionalOnProperty}只能对比String类型的value和havingValue的值是否完全匹配（忽略大小写）
 * 		
 * 		{@linkplain ConditionalOnProperty}的实现是{@linkplain OnPropertyCondition}
 * 		但是OnPropertyCondition并不是public的而是default，因此不能继承重写，而且主要的对比是在
 * 		OnPropertyCondition的私有静态内部类Spec中的collectProperties()-->isMatch()
 * 		其实只要重写这一块就好了，不过没办法重写，因此全部重写本注解的实现类（其实90%的代码都相同）
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnPropertyCondition2.class)
public @interface ConditionalOnProperty2 {
	/**
	 * Alias for {@link #name()}.
	 * @return the names
	 */
	String[] value() default {};

	/**
	 * A prefix that should be applied to each property. The prefix automatically ends
	 * with a dot if not specified. A valid prefix is defined by one or more words
	 * separated with dots (e.g. {@code "acme.system.feature"}).
	 * @return the prefix
	 */
	String prefix() default "";

	/**
	 * The name of the properties to test. If a prefix has been defined, it is applied to
	 * compute the full key of each property. For instance if the prefix is
	 * {@code app.config} and one value is {@code my-value}, the full key would be
	 * {@code app.config.my-value}
	 * <p>
	 * Use the dashed notation to specify each property, that is all lower case with a "-"
	 * to separate words (e.g. {@code my-long-property}).
	 * @return the names
	 */
	String[] name() default {};

	/**
	 * The string representation of the expected value for the properties. If not
	 * specified, the property must <strong>not</strong> be equal to {@code false}.
	 * 对于String类型的value，会进行全匹配
	 * 对于List类型的value，只要其中一项符合即可
	 * @return the expected value
	 */
	String havingValue() default "";

	/**
	 * Specify if the condition should match if the property is not set. Defaults to
	 * {@code false}.
	 * @return if should match if the property is missing
	 */
	boolean matchIfMissing() default false;
}
