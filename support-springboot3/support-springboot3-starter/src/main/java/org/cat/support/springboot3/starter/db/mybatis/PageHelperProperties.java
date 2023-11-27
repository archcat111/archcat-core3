package org.cat.support.springboot3.starter.db.mybatis;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2022年4月12日 下午4:08:25
 * @version 1.0
 * @description org.cat3框架定义的基于springboot的mybatis.pagehelper配置
 *
 */
@ConfigurationProperties(prefix = "cat.support3.db.mybatis.pagehelper")
@Getter
@Setter
public class PageHelperProperties {
	
	private boolean enabled;
	
	//默认为false，为 true 时，会将 RowBounds 中的 offset 参数当成 pageNum 使用
	private String  offsetAsPageNum = "true";
	//默认为false，为true时，使用 RowBounds 分页会进行 count 查询
	private String rowBoundsWithCount = "false";
	//默认为false，为 true 时，如果 pageSize=0 或者 RowBounds.limit = 0 就会查询出全部的结果
	private String pageSizeZero = "false";
	//默认为false，为 true 时，pageNum<=0 时会查询第一页， pageNum>pages（超过总数时），会查询最后一页
	private String reasonable = "false";
	//默认值false，支持通过 Mapper 接口参数来传递分页参数，自动根据上面 params 配置的字段中取值
	private String supportMethodsArguments = "false";
	//默认值为：com.github.pagehelper.PageHelper。自定义分页逻辑需要设置这个值为分页的类
	private String dialect = "";
	//配置分页对应的数据库
	private String helperDialect = "mysql";
	//默认值为 false。设置为 true 时，允许在运行时根据多数据源自动识别对应方言的分页
	private String autoRuntimeDialect = "false";
	//默认值为 false。设置为 true 时，允许在运行时根据数据源自动识别对应方言的分页
	private String autoDialect = "true";
	//默认值为 true。当使用运行时动态数据源或没有设置 helperDialect 属性自动获取数据库类型时，
	//会自动获取一个数据库连接， 通过该属性来设置是否关闭获取的这个连接
	private String closeConn = "true";
	//为了支持startPage(Object params)方法，增加了该参数来配置参数映射
	private String params = "count=countSql";
	
	private Properties properties = new Properties();
	
	public Properties getProperties() {
		this.properties.setProperty("offsetAsPageNum", this.offsetAsPageNum);
		this.properties.setProperty("rowBoundsWithCount", this.rowBoundsWithCount);
		this.properties.setProperty("pageSizeZero", this.pageSizeZero);
		this.properties.setProperty("reasonable", this.reasonable);
		this.properties.setProperty("supportMethodsArguments", this.supportMethodsArguments);
		this.properties.setProperty("dialect", this.dialect);
		this.properties.setProperty("helperDialect", this.helperDialect);
		this.properties.setProperty("autoRuntimeDialect", this.autoRuntimeDialect);
		this.properties.setProperty("autoDialect", this.autoDialect);
		this.properties.setProperty("closeConn", this.closeConn);
		this.properties.setProperty("params", this.params);
		return this.properties;
	}
}
