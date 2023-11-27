package org.cat.core.exception3.constants;

public final class ExceptionConstants {
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月16日 上午11:24:50
	 * @version 1.0
	 * @description 标准异常Bean本身的现实Prop Name相关常量
	 *
	 */
	public static final class ParamName {
		public static final String EXCETPION_ID="exception_id";
		public static final String EXCEPTION_PROJECT_CODE="exception_project_code";
		public static final String EXCEPTION_CODE="exception_code";
		public static final String EXCEPTION_MSG="exception_msg";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月16日 上午11:28:22
	 * @version 1.0
	 * @description 标准ExceptionBean Prop Value相关常量
	 *
	 */
	public static final class ParamValue {
		public static final String ID_NOT_WRITE="-1"; //exceptionId
		
		public static final String PROJECT_CODE_NOT_WRITE="-1";
		
		public static final String CODE_NOT_WRITE="-1"; //exceptionCode
		public static final String CODE_SYSTEM_EXCEPTION="-2"; //表示系统发生的异常，并非自定义异常
		public static final String CODE_BUSSINESS_VALID_EXCEPTION="-3"; //表示业务发生的异常，参数验证异常
		
		public static final String EXCEPTION_MSG_NOT_WRITE="未填写内容"; //exceptionMsg
	}
	
}
