package org.cat.support.springboot3.starter.id;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.id")
@Getter
@Setter
public class IdProperties {
	private boolean enabled;
	
	/**
	 * key为IdGenerator的名称
	 */
	private Map<String, IdGeneratorProperties> generators;
}
