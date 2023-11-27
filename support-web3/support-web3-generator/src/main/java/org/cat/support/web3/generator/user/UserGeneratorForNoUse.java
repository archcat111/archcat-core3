package org.cat.support.web3.generator.user;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.core.web3.user.IUserGenerator;
import org.slf4j.Logger;

public class UserGeneratorForNoUse implements IUserGenerator {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "UserGeneratorForNoUse：";
	
	@Override
	public String getUserCode() {
		coreLogger.warn(logPrefix + "userCode默认为null");
		return null;
	}
	
	@Override
	public Long getUserCodeForLong() {
		coreLogger.warn(logPrefix + "userCode(Long类型)默认为null");
		return null;
	}

	@Override
	public String getUserName() {
		coreLogger.warn(logPrefix + "userName默认为null");
		return null;
	}
	
	@Override
	public String getNickName() {
		coreLogger.warn(logPrefix + "nickName默认为null");
		return null;
	}

	@Override
	public String getUserParam(String paramName) {
		coreLogger.warn(logPrefix + "userParams默认为null");
		return null;
	}

	@Override
	public String getSessionId() {
		coreLogger.warn(logPrefix + "sessionId默认为null");
		return null;
	}

	@Override
	public String getDevice() {
		coreLogger.warn(logPrefix + "device默认为null");
		return null;
	}

}
