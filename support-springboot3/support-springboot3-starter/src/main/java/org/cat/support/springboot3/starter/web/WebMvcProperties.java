package org.cat.support.springboot3.starter.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.web")
@Getter
@Setter
public class WebMvcProperties {
	private boolean enabled;
	private WebMvcFileProperties file = new WebMvcFileProperties();
	private WebMvcCorsProperties cors = new WebMvcCorsProperties();
	private WebMvcRespProperties resp = new WebMvcRespProperties();
	private WebMvcTestProperties test = new WebMvcTestProperties();
}
