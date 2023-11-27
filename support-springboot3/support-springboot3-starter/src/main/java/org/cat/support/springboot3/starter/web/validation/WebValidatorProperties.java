package org.cat.support.springboot3.starter.web.validation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.web.validator")
@Getter
@Setter
public class WebValidatorProperties {
	
	private boolean enabled;
	
	private boolean failFast = true;

}
