package org.cat.support.springcloud3.gateway.exception;

import org.cat.core.exception3.ParentRuntimeException;
import org.cat.core.web3.constants.ArchVersion;

/**
 * 
 * @author 王云龙
 * @date 2017年12月25日 下午1:26:52
 * @version 1.0
 * @description gateway自定义服务异常
 *
 */
public class GatewayException extends ParentRuntimeException{

	private static final long serialVersionUID = ArchVersion.V3;
	
	
	public GatewayException(GatewayExceptionEnum gatewayExceptionEnum) {
		super(gatewayExceptionEnum.getErrorCode(), gatewayExceptionEnum.getErrorMsg());
	}


}
