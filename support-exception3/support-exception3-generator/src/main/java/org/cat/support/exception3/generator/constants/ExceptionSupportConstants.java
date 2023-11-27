package org.cat.support.exception3.generator.constants;

/**
 * 
 * @author 王云龙
 * @date 2021年8月26日 下午5:47:17
 * @version 1.0
 * @description Exception相关Starter用到的常量的常量类
 *
 */
public class ExceptionSupportConstants {
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月26日 下午5:47:14
	 * @version 1.0
	 * @description 异常Id生成器的名称常量类
	 *
	 */
	public static final class IdGenerator {
		/**
		 * 当cat.support3.web.exception..IdGeneratorName为noUse的时候会初始化ExceptionIdGeneratorForNoUse
		 * 否则初始化ExceptionIdGeneratorImpl
		 */
		public static final String NO_USE="noUse";
	}
	
	public static final class CodeGenerator {
		public static final String DEFAULT="default";
		public static final String NO_USE="noUse";
	}
}
