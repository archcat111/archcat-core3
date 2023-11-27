package org.cat.core.web3.validator.generator;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
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
 * @description 对密码的校验注解
 * 		@Documented		标记这些注解是否包含在用户文档中
 * 		@Retention(RetentionPolicy.RUNTIME)
 * 			注解作用域：SOURCE只在源码中存在、CLASS在源码和class文件中存在、RUNTIME在运行时也可一起作用
 * 		@Inherited	允许子类继承父类注释
 * 		@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
 * 			METHOD：方法
 * 			FIELD：字段、枚举的常量
 * 			ANNOTATION_TYPE：注解
 * 			CONSTRUCTOR：构造函数
 * 			PARAMETER：方法参数
 * 			LOCAL_VARIABLE：局部变量声明
 * 			PACKAGE：包声明
 * 			TYPE_PARAMETER：
 * 			TYPE_USE：
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Inherited
@Constraint(validatedBy= {ArchValidPasswordValidator.class})
public @interface ArchValidPassword {
	String message() default "密码格式不正确";
	
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	String regex() default "";
	boolean strict() default false; //是否使用严格的正则对密码进行验证，默认为宽松
}
