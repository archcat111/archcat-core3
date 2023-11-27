package org.cat.core.web3.validator.generator;

/**
 * 
 * @author 王云龙
 * @date 2022年5月5日 下午1:48:41
 * @version 1.0
 * @description 对密码的校验器
 *
 */
public class ArchValidPasswordValidator extends AbsArchValidRegexValidator<ArchValidPassword, Object> {

	@Override
	protected String getCustomizeRegex(ArchValidPassword constraintAnnotation) {
		return constraintAnnotation.regex();
	}

	@Override
	protected String getStrictRegex() {
		String strictRegex = "(![A-Za-z0-9]+)(![a-z0-9\\W]+)(![A-Za-z\\W]+)(![A-Z0-9\\W]+)[a-zA-Z0-9\\W]{8,16}"; 
		return strictRegex;
	}

	@Override
	protected String getRelaxedRegex() {
		String relaxedRegex = "[[a-z0-9\\W]]{8,16}";
		return relaxedRegex;
	}

	@Override
	protected boolean getStrict(ArchValidPassword constraintAnnotation) {
		return constraintAnnotation.strict();
	}

	@Override
	protected String getCustomizeMessage(ArchValidPassword constraintAnnotation) {
		return constraintAnnotation.message();
	}
	

}
