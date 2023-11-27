package org.cat.support.springboot3.starter.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebMvcTestPingProperties {
	private boolean enabled;
	private String path = "/v3/controller/ping";
}
