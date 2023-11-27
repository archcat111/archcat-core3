package org.cat.support.exception3.generator.id;

import org.cat.core.exception3.constants.ExceptionConstants;
import org.cat.core.exception3.exception.StandardRuntimeException;
import org.cat.support.id3.generator.IIdGenerator;

public class ExceptionIdGeneratorForNoUse extends AbsExceptionIdGenerator {

	@Override
	public String getExceptionId() {
		return ExceptionConstants.ParamValue.ID_NOT_WRITE;
	}

	@Override
	public boolean isTrueExceptionId(String exceptionId) {
		if(ExceptionConstants.ParamValue.ID_NOT_WRITE.equals(exceptionId)) {
			return true;
		}
		return false;
	}
	
	public void setIdGenerator(IIdGenerator idGenerator) {
		throw new StandardRuntimeException("ExceptionIdGeneratorForNoUse不支持设置IdGenerator");
	}

}
