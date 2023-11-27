package org.cat.support.springboot3.starter.es;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.search")
@Getter
@Setter
public class SearchProperties {
	private boolean enabled;
	
	/**
	 * key为elasticSearchm某个实例的名称
	 */
	private Map<String, ESGeneratorProperties> elasticSearch;
}
