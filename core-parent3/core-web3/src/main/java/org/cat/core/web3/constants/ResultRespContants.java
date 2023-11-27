package org.cat.core.web3.constants;

import org.cat.core.web3.resp.ArchResultResp;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午2:08:52
 * @version 1.0
 * @description {@linkplain ArchResultResp}相关常量
 *
 */
public final class ResultRespContants {
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月16日 下午2:09:11
	 * @version 1.0
	 * @description {@linkplain ArchResultResp#setStatus(String)}相关常量
	 *
	 */
	public final class Status {
		public static final String NORMAL="normal"; //正常
		public static final String SYSTEM_EXCEPTION="system-exception"; //系统异常
		public static final String BUSINESS_EXCEPTION="business-exception"; //业务异常
		public static final String UNKNOWN="unknown"; //未知
	}
	
}
