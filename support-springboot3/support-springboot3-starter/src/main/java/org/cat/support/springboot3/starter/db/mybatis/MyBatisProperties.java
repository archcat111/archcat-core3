package org.cat.support.springboot3.starter.db.mybatis;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.cat.support.db3.generator.constants.DataSourceConstants;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MyBatisProperties {
	private boolean enabled = true;
	
	private String dataSourceType = DataSourceConstants.Type.DRUID;
	private String dataSourceName;
	
	/**
	 * 扫描xml文件的列表
	 * 例如：
	 * cat.support3.db.mybatis:
	 * 		<mybatisDataSource1>:
	 * 			mapperSqlXmls:
	 * 				- classpath:com/juran/exampleuser/app/dao/xml/*.xml
	 * 				- classpath:com/juran/exampleauth/app/dao/xml/*.xml
	 */
	private String[] mapperSqlXmls;

	/**
	 * 扫描Bean文件的列表
	 * 例如：
	 * cat.support3.db.mybatis:
	 * 		<mybatisDataSource1>:
	 * 			mapperBeanPackages:
	 * 				- com.juran.exampleuser.app.dao.bean
	 * 				- com.juran.exampleauth.app.dao.bean
	 */
	private String[] mapperBeanPackages;
	
	/**
	 * Mapper接口文件的列表
	 * 例如：
	 * cat.support3.db.mybatis:
	 * 		<mybatisDataSource1>:
	 * 			mapperInterfacePackages:
	 * 				- com.juran.exampleuser.app.dao.mapper
	 * 				- com.juran.exampleauth.app.dao.mapper
	 */
	private String[] mapperInterfacePackages;
	/**
	 * SIMPLE(默认)、REUSE、BATCH
	 * 其实这三种模式分别对应着三种执行器：
	 * SimpleExecutor、ReuseExecutor、BatchExecutor
	 * 
	 * SimpleExecutor：是一种常规执行器，每次执行都会创建一个statement，用完后关闭
	 * 		每次都会关闭statement，意味着下一次使用需要重新开启statement
	 * ReuseExecutor：是可重用执行器，将statement存入map中，操作map中的statement而不会重复创建statement
	 * 		不会关闭statement，而是把statement放到缓存中。缓存的key为sql语句，value即为对应的statement
	 * 		也就是说不会每一次调用都去创建一个 Statement 对象，而是会重复利用以前创建好的（如果SQL相同的话）
	 * 		这也就是在很多数据连接池库中常见的 PSCache 概念
	 * BatchExecutor：是批处理型执行器，doUpdate预处理存储过程或批处理操作，doQuery提交并执行过程
	 * 		在BatchExecutor中的doupdate并不会像前面两者那样执行返回行数
	 * 		而是每次执行将statement预存到有序集合
	 * 		官方说明这个executor是用于执行存储过程的和批量操作的，因此这个方法是循环或者多次执行构建一个存储过程或批处理过程
	 */
	private ExecutorType executorType = ExecutorType.SIMPLE;
	
	private LocalCacheScope localCacheScope = LocalCacheScope.SESSION; //1级缓存
	private boolean cacheEnabled = false; //2级缓存
	
	private String logicDeleteField = "del";
	private String logicDeleteValue = "1";
	private String logicNotDeleteValue = "0";
	
}
