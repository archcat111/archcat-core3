package org.cat.core.web3.validator.generator;

/**
 * 
 * @author 王云龙
 * @date 2022年6月8日 下午4:20:26
 * @version 1.0
 * @description 对每月进行校验的校验器
 *
 */
public class ArchValidDayValidator extends AbsArchValidRegexValidator<ArchValidDay, Object> {
	
	private String strictRegex = "[0-3]{0,1}[0-9]{1}";
	private String relaxedRegex = strictRegex;

	@Override
	protected String getCustomizeRegex(ArchValidDay constraintAnnotation) {
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
	protected boolean getStrict(ArchValidDay constraintAnnotation) {
		return constraintAnnotation.strict();
	}

	@Override
	protected String getCustomizeMessage(ArchValidDay constraintAnnotation) {
		return constraintAnnotation.message();
	}

}
