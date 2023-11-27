package org.cat.core.web3.validator.generator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * 
 * @author 王云龙
 * @date 2022年6月8日 下午4:17:20
 * @version 1.0
 * @description 对月份的校验注解
 *
 */
@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= {ArchValidMonthValidator.class})
public @interface ArchValidMonth {
	String message() default "月份格式不正确";
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	String regex() default "";
	boolean strict() default false; //是否使用严格的正则对密码进行验证，默认为宽松
	
}
