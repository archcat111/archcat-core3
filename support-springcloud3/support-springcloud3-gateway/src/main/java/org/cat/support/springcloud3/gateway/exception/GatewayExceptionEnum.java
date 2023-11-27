package org.cat.support.springcloud3.gateway.exception;

public enum GatewayExceptionEnum {

	UNAUTHORIZED("01001","您未登录，请先进行登录");
	
	
	private final String prefix="1000-";
	private final String errorCode;
	private final String errorMsg;
	
	
	private GatewayExceptionEnum(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	
	public String getErrorCode() {
		return prefix+errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

//	public static GatewayExceptionEnum valueOf(int errorCode) {  
//        for (GatewayExceptionEnum userExceptionEnum : GatewayExceptionEnum.values()) {  
//            if (userExceptionEnum.errorCode.equals(errorCode)) {  
//                return userExceptionEnum;
//            }  
//        }  
//        throw new IllegalArgumentException("No matching constant for [" + errorCode + "]");
//    }  	
	
	@Override
	public String toString() {
		return this.errorCode;
	}
}
