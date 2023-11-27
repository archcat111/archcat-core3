package org.cat.core.web3.log.id;

import org.cat.core.web3.log.constants.AppenderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchLoggerFactory {
	
	public static Logger getLogger(String appenderName) {
		return LoggerFactory.getLogger(appenderName);
	}
	
	public static Logger getCoreLogger() {
		return getLogger(AppenderConstants.AppenderName.CORE);
	}
	
	public static Logger getEventLogger() {
		return getLogger(AppenderConstants.AppenderName.EVENT);
	}
	
	public static Logger getAuditLogger() {
		return getLogger(AppenderConstants.AppenderName.AUDIT);
	}
	
	public static Logger getTrackLogger() {
		return getLogger(AppenderConstants.AppenderName.TRACK);
	}
	
	public static Logger getInfoLogger() {
		return getLogger(AppenderConstants.AppenderName.INFO);
	}
	
	public static Logger getExceptionLogger() {
		return getLogger(AppenderConstants.AppenderName.EXCEPTION);
	}
	
	public static Logger getFeignLogger() {
		return getLogger(AppenderConstants.AppenderName.OTHER_FEIGN);
	}
	
}
