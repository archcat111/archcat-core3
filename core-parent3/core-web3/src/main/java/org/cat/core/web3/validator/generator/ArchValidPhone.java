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
 * @date 2022年4月25日 下午3:20:01
 * @version 1.0
 * @description 对手机号的校验注解
 *
 */
@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= {ArchValidPhoneValidator.class})
public @interface ArchValidPhone {
	String message() default "手机号码格式不正确";
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	String regex() default "";
	boolean strict() default false; //是否使用严格的正则对密码进行验证，默认为宽松
	
}
