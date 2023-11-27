package org.cat.core.web3.validator.generator;
/**
 * 
 * @author 王云龙
 * @date 2022年8月10日 下午7:38:55
 * @version 1.0
 * @description 基于正则表达式进行校验的校验器
 *
 */
public class ArchValidRegexValidator extends AbsArchValidRegexValidator<ArchValidRegex, Object> {
	
	@Override
	protected String getCustomizeRegex(ArchValidRegex constraintAnnotation) {
		return constraintAnnotation.regex();
	}

	@Override
	protected String getStrictRegex() {
		return null;
	}

	@Override
	protected String getRelaxedRegex() {
		return null;
	}

	@Override
	protected boolean getStrict(ArchValidRegex constraintAnnotation) {
		return constraintAnnotation.strict();
	}

	@Override
	protected String getCustomizeMessage(ArchValidRegex constraintAnnotation) {
		return constraintAnnotation.message();
	}

}
