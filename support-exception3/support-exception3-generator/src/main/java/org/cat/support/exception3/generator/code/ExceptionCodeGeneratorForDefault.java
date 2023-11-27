package org.cat.support.exception3.generator.code;

import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.constants.ExceptionConstants;

public class ExceptionCodeGeneratorForDefault implements IExceptionCodeGenerator {

	private String projectCode;
	
	@Override
	public String getExceptionProjectCode() {
		return this.projectCode;
	}

	@Override
	public boolean isTrueExceptionProjectCode(String exceptionProjectCode) {
		if(this.projectCode.equals(exceptionProjectCode)) {
			return true;
		}
		return false;
	}
	
	@Override
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	@Override
	public String getExceptionBusinessCode() {
		throw new UnsupportedOperationException("当前方法不被支持，请在抛出自定义异常时自行设置exceptionCode");
	}

	@Override
	public boolean isTrueExceptionBusinessCode(String exceptionCode) {
		throw new UnsupportedOperationException("当前方法不被支持，请在抛出自定义异常时自行设置exceptionCode");
	}

	@Override
	public String getExceptionSystemCode() {
		return ExceptionConstants.ParamValue.CODE_SYSTEM_EXCEPTION;
	}

	@Override
	public boolean isTrueExceptionSystemCode(String exceptionCode) {
		if(ExceptionConstants.ParamValue.CODE_SYSTEM_EXCEPTION.equals(exceptionCode)) {
			return true;
		}
		return false;
	}

}
