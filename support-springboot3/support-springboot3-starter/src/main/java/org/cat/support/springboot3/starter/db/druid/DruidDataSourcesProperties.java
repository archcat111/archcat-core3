package org.cat.support.springboot3.starter.db.druid;

import java.util.Map;

import org.cat.support.db3.generator.druid.DruidCommonProperties;
import org.cat.support.db3.generator.druid.DruidDataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.db.druid")
@Getter
@Setter
public class DruidDataSourcesProperties {
	private boolean enabled = true;
	private DruidCommonProperties commonProperties = new DruidCommonProperties();
	private  Map<String, DruidDataSourceProperties> druidDataSources = Maps.newHashMap();
}
