package org.cat.core.web3.validator.generator;

/**
 * 
 * @author 王云龙
 * @date 2022年6月1日 下午3:06:05
 * @version 1.0
 * @description 对年份的校验器
 *
 */
public class ArchValidYearValidator extends AbsArchValidRegexValidator<ArchValidYear, Object> {
	
	private String strictRegex = "[1-2]{1}[0-9]{3}";
	private String relaxedRegex = strictRegex;

	@Override
	protected String getCustomizeRegex(ArchValidYear constraintAnnotation) {
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
	protected boolean getStrict(ArchValidYear constraintAnnotation) {
		return constraintAnnotation.strict();
	}

	@Override
	protected String getCustomizeMessage(ArchValidYear constraintAnnotation) {
		return constraintAnnotation.message();
	}

}
