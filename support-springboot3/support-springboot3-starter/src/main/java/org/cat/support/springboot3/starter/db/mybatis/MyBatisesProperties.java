package org.cat.support.springboot3.starter.db.mybatis;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.db.mybatis")
@Getter
@Setter
public class MyBatisesProperties {
	private boolean enabled = false;
	
	/**
	 * key为MyBatis的数据源名称
	 */
	Map<String, MyBatisProperties> myBatisDataSources;
}
