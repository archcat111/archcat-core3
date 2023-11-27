package org.cat.support.springboot3.starter.reactive;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.reactive")
@Getter
@Setter
public class ReactiveMvcProperties {
	private boolean enabled;
	private ReactiveMvcRespProperties resp = new ReactiveMvcRespProperties();
}
