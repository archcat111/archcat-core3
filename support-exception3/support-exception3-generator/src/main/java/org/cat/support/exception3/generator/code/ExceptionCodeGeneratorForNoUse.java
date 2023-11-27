package org.cat.support.exception3.generator.code;

import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.constants.ExceptionConstants;

public class ExceptionCodeGeneratorForNoUse implements IExceptionCodeGenerator {

	@Override
	public String getExceptionProjectCode() {
		return ExceptionConstants.ParamValue.PROJECT_CODE_NOT_WRITE;
	}

	@Override
	public boolean isTrueExceptionProjectCode(String exceptionProjectCode) {
		if(ExceptionConstants.ParamValue.PROJECT_CODE_NOT_WRITE.equals(exceptionProjectCode)) {
			return true;
		}
		return false;
	}

	@Override
	public String getExceptionBusinessCode() {
		return ExceptionConstants.ParamValue.CODE_NOT_WRITE;
	}

	@Override
	public boolean isTrueExceptionBusinessCode(String exceptionCode) {
		if(ExceptionConstants.ParamValue.CODE_NOT_WRITE.equals(exceptionCode)) {
			return true;
		}
		return false;
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

	@Override
	public void setProjectCode(String projectCode) {
		throw new UnsupportedOperationException("ExceptionCodeGeneratorForNoUse不支持setProjectCode(String projectCode)");
	}

}
