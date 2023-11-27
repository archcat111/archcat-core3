package org.cat.support.springcloud3.starter.gateway;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchGatewayCorsProperties {
	
	private boolean enabled;
	private String path;
	private List<String> allowedOrigins = Lists.newArrayList("*");
	private List<String> allowedMethods = Lists.newArrayList("*");
	private List<String> allowedHeaders = Lists.newArrayList("*");
	private Boolean allowCredentials = true; //是否允许携带cookie
	
}
