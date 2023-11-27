package org.cat.support.log3.generator.id;

import org.cat.core.web3.log.constants.BaseLogConstants;
import org.cat.support.id3.generator.IIdGenerator;
import org.cat.support.log3.generator.exception.LogException;

public class LogIdGeneratorForNoUse extends AbsLogIdGenerator {

	@Override
	public String getLogId() {
		return BaseLogConstants.Id.NOT_WRITE;
	}

	@Override
	public void setIdGenerator(IIdGenerator idGenerator) {
		throw new LogException("LogIdGeneratorForNoUse不支持设置IdGenerator");
	}
	
	

}
