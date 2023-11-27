package org.cat.support.springboot3.starter.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebMvcTestProperties {
	private WebMvcTestPingProperties ping = new WebMvcTestPingProperties();
}
