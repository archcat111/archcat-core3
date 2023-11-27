package org.cat.support.db3.generator.druid;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月13日 下午3:04:14
 * @version 1.0
 * @description 一个通用的数据源配置
 *
 */
@Getter
@Setter
public class DruidDataSourceProperties extends DruidCommonProperties{
	
	private boolean enabled = true;
	
//	private String driverClassName = JdbcConstants.MYSQL_DRIVER;
//	private String dbTypeName; //默认值为null
	
//	private String name = "default-druidDataSource-"+new Random().nextInt(100)+"-"+System.currentTimeMillis();
	
	private String url = null; 
	private String userName = null;
	private String password = null;
}
