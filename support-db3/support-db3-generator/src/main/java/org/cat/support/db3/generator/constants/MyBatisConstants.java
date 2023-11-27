package org.cat.support.db3.generator.constants;

/**
 * 
 * @author 王云龙
 * @date 2021年10月9日 下午2:28:56
 * @version 1.0
 * @description MyBatis相关常量
 *
 */
public class MyBatisConstants {
	
	public static final class BeanName {
		public static final String SQL_SESSION_FACTORY_NAME_SUFFIX = "-SqlSessionFactory";
		public static final String SQL_SESSION_TEMPLATE_NAME_SUFFIX = "-SqlSessionTemplate";
		public static final String MAPPER_SCANNER_CONFIGURER_NAME_SUFFIX = "-MapperScannerConfigurer";
	}
	
	//MyBatisSqlSessionFactoryHolder中存放Mybatis相关属性的时候使用的key的名称
	public static final class PropName {
		public static final String MYBATIS_DATA_SOURCE_NAME="myBatisDataSourceName";
		public static final String DATA_SOURCE_TYPE="dataSourceType";
		public static final String DATA_SOURCE_NAME="dataSourceName";
		public static final String MAPPER_BEAN_PACKAGES="mapperBeanPackages";
		public static final String MAPPER_SQL_XMLS="mapperSqlXmls";
		public static final String MAPPER_INTERFACE_PACKAGES = "mapperInterfacePackages";
		public static final String EXECUTOR_TYPE = "executorType";
		public static final String LOCAL_CACHE_SCOPE = "localCacheScope";
		public static final String LEVEL_2_CACHE="level2Cache";
		
		
	}
}
