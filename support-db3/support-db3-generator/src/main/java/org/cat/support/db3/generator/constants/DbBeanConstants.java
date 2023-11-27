package org.cat.support.db3.generator.constants;

/**
 * 
 * @author 王云龙
 * @date 2022年4月11日 下午4:13:02
 * @version 1.0
 * @description 插入数据库的Bean的字段常量
 *
 */
public class DbBeanConstants {
	public static final class Status {
		public static final Integer VALID = 1;
		public static final Integer INVALID = 0;
		public static final Integer ALL = -1;
	}
	
	public static final class Del {
		public static final Integer DELETED = 1;
		public static final Integer NOT_DELETED = 0;
		public static final Integer ALL = -1;
	}
	
	//用于设置tree型数据时，一般如果需要新增的该数据为顶层数据，则其parentId则为0
	public static final class TreeParentId {
		public static final Long NOT_PARENT = 0L;
	}
	//用于设置tree型数据时，一般如果需要新增的该数据为顶层数据，则其level则为0
	public static final class TreeLevel {
		public static final Integer LEVEL_0 = 0;
		public static final Integer LEVEL_1 = 1;
	}
}
