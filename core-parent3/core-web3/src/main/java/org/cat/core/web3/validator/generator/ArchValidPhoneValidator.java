package org.cat.core.web3.validator.generator;

/**
 * 
 * @author 王云龙
 * @date 2022年5月5日 下午2:03:11
 * @version 1.0
 * @description 对手机号的校验器
 *
 */
public class ArchValidPhoneValidator extends AbsArchValidRegexValidator<ArchValidPhone, Object> {

	@Override
	protected String getCustomizeRegex(ArchValidPhone constraintAnnotation) {
		return constraintAnnotation.regex();
	}

	@Override
	protected String getStrictRegex() {
		String strictRegex = "[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}"; 
		return strictRegex;
	}

	@Override
	protected String getRelaxedRegex() {
		String relaxedRegex = "[1]([3-9])[0-9]{9}";
		return relaxedRegex;
	}

	@Override
	protected boolean getStrict(ArchValidPhone constraintAnnotation) {
		return constraintAnnotation.strict();
	}

	@Override
	protected String getCustomizeMessage(ArchValidPhone constraintAnnotation) {
		return constraintAnnotation.message();
	}

}
