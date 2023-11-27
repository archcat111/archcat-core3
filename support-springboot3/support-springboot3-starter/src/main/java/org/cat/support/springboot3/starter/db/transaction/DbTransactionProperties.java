package org.cat.support.springboot3.starter.db.transaction;

import java.util.Map;

import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.db.transaction")
@Getter
@Setter
public class DbTransactionProperties {
	private boolean enabled = false;
	
	private Map<String, DbTransactionDataSourceProperties> roles = Maps.newHashMap();
}
