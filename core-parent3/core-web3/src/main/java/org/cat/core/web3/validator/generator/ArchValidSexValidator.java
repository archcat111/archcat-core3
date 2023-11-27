package org.cat.core.web3.validator.generator;

/**
 * 
 * @author 王云龙
 * @date 2022年5月5日 下午2:10:14
 * @version 1.0
 * @description 对性别的校验器
 *
 */
public class ArchValidSexValidator extends AbsArchValidRegexValidator<ArchValidSex, Object> {

	@Override
	protected String getCustomizeRegex(ArchValidSex constraintAnnotation) {
		return constraintAnnotation.regex();
	}

	@Override
	protected String getStrictRegex() {
		return getRelaxedRegex();
	}

	@Override
	protected String getRelaxedRegex() {
		String relaxedRegex = "(0)|(1)|(2)|(3)";
		return relaxedRegex;
	}

	@Override
	protected boolean getStrict(ArchValidSex constraintAnnotation) {
		return false;
	}

	@Override
	protected String getCustomizeMessage(ArchValidSex constraintAnnotation) {
		return constraintAnnotation.message();
	}

}
