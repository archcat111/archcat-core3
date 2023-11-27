package org.cat.core.web3.validator.generator;

/**
 * 
 * @author 王云龙
 * @date 2022年6月8日 下午4:16:11
 * @version 1.0
 * @description 对月份进行校验的校验器
 *
 */
public class ArchValidMonthValidator extends AbsArchValidRegexValidator<ArchValidMonth, Object> {
	
	private String strictRegex = "[0-1]{0,1}[0-9]{1}";
	private String relaxedRegex = strictRegex;

	@Override
	protected String getCustomizeRegex(ArchValidMonth constraintAnnotation) {
		return constraintAnnotation.regex();
	}

	@Override
	protected String getStrictRegex() {
		return this.strictRegex;
	}

	@Override
	protected String getRelaxedRegex() {
		return this.relaxedRegex;
	}

	@Override
	protected boolean getStrict(ArchValidMonth constraintAnnotation) {
		return constraintAnnotation.strict();
	}

	@Override
	protected String getCustomizeMessage(ArchValidMonth constraintAnnotation) {
		return constraintAnnotation.message();
	}

}
