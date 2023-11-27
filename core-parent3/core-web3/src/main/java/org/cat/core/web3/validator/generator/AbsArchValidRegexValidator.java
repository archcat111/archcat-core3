package org.cat.core.web3.validator.generator;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.util3.string.ArchRegexUtil;

/**
 * 
 * @author 王云龙
 * @date 2022年5月31日 上午10:21:49
 * @version 1.0
 * @description 一个基于正则的验证其的超类
 *
 * @param <A>
 * @param <T>
 */
public abstract class AbsArchValidRegexValidator<A extends Annotation, T> implements ConstraintValidator<A, T>{

	private String customizeRegex = null;
	private boolean isStrict = false;
	private String customizeMessage;

	@Override
	public void initialize(A constraintAnnotation) {
		String regex = getCustomizeRegex(constraintAnnotation);
		if(StringUtils.isNotBlank(regex)) {
			this.customizeRegex = regex;
		}
		
		this.isStrict =getStrict(constraintAnnotation);
		
		String message = getCustomizeMessage(constraintAnnotation);
		if(StringUtils.isNotBlank(message)) {
			this.customizeMessage = message;
		}
	}
	
	@Override
	public boolean isValid(T value, ConstraintValidatorContext context) {
		//禁用默认的message的值
		context.disableDefaultConstraintViolation();
		
		if(value == null) {
			return true;
		}
		
		if(StringUtils.isNotBlank(this.customizeRegex)) {
			if(ArchRegexUtil.regex(this.customizeRegex, value.toString())) {
				return true;
			}
			context.buildConstraintViolationWithTemplate(this.customizeMessage).addConstraintViolation();
			return false;
		}
		
		if(isStrict) {
			if(ArchRegexUtil.regex(this.getStrictRegex(), value.toString())) {
				return true;
			}
			context.buildConstraintViolationWithTemplate(this.customizeMessage).addConstraintViolation();
			return false;
		}else {
			if(ArchRegexUtil.regex(this.getRelaxedRegex(), value.toString())) {
				return true;
			}
			context.buildConstraintViolationWithTemplate(this.customizeMessage).addConstraintViolation();
			return false;
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月31日 上午10:22:24
	 * @version 1.0
	 * @description 获取自定义的正则表达式 
	 * 		如果指定了自定义的正则表达式，则无论strict是否为true，都只进行自定义的正则表达式验证
	 * @param constraintAnnotation
	 * @return
	 */
	protected abstract String getCustomizeRegex(A constraintAnnotation);
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月31日 下午1:49:36
	 * @version 1.0
	 * @description 获取strict为true时执行验证的正则表达式 
	 * @return
	 */
	protected abstract String getStrictRegex();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月31日 下午1:49:56
	 * @version 1.0
	 * @description 获取strict为false时执行验证的正则表达式 
	 * @return
	 */
	protected abstract String getRelaxedRegex();
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月31日 上午10:22:44
	 * @version 1.0
	 * @description 是否是执行严格的正则表达式验证 
	 * @param constraintAnnotation
	 * @return
	 */
	protected abstract boolean getStrict(A constraintAnnotation);
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月31日 下午1:48:06
	 * @version 1.0
	 * @description 获取自定义的异常提示 
	 * @param constraintAnnotation
	 * @return
	 */
	protected abstract String getCustomizeMessage(A constraintAnnotation);

}
