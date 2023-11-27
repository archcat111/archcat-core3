package org.cat.core.web3.constants;

/**
 * 
 * @author 王云龙
 * @date 2022年5月11日 下午5:03:57
 * @version 1.0
 * @description 基础架构常量
 *
 */
public class ArchConstants {
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月17日 下午2:41:30
	 * @version 1.0
	 * @description 在日志中记录的应用或者服务使用的大框架，如：Java、Python
	 *
	 */
	public static final class Framework {
		public static final String JAVA="Java";
		public static final String PYTHON="Python";
		public static final String UNKNOWN="Unknown";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月17日 下午2:39:51
	 * @version 1.0
	 * @description Framework下的子架构，如：Java下面的SpringCloud
	 *
	 */
	public static final class FrameworkSub {
		public static final String SPRING_MVC="SpringMVC";
		public static final String SPRING_BOOT="SpringBoot";
		public static final String SPRING_CLOUD="SpringCloud";
		public static final String SPRING_CLOUD_ALIBABA="SpringCloudAlibaba";
		public static final String DUBBO="Dubbo";
		public static final String UNKNOWN="Unknown";
	}
}
