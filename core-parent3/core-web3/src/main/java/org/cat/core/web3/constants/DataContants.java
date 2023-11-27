package org.cat.core.web3.constants;

/**
 * 
 * @author 王云龙
 * @date 2018年1月12日 上午11:50:41
 * @version 1.0
 * @description 数据相关的常量
 *
 */
public class DataContants {
	/**
	 * 
	 * @author 王云龙
	 * @date 2018年1月12日 上午11:50:53
	 * @version 1.0
	 * @description 数据状态常量
	 *
	 */
	public static class Status{
		public static final int ENABLED=1;//生效
		public static final int DISABLED=0;//失效
	}
	
	public static class StatusV2{
		public static final int ING=0;//进行中
		public static final int YES=1;//生效
		public static final int NO=2;//生效
	}
	
	public static class Delete{
		public static final int YES=1;//生效
		public static final int NO=0;//失效
	}
	
	public static final class Env{
		public static final int UNKNOWN=0;
		public static final int PROD=1;
		public static final int UAT=2;
		public static final int ALPHA=3;
	}
}
