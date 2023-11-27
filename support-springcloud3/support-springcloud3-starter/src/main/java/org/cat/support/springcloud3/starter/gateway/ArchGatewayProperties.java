package org.cat.support.springcloud3.starter.gateway;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.gateway")
@Getter
@Setter
public class ArchGatewayProperties {
	
	private List<ArchGatewayCorsProperties> corsConfigs;
}
