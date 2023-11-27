package org.cat.support.springcloud3.starter.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.nacos")
@Getter
@Setter
public class NacosPlusProperties {
	private boolean enabled;
	
	//是否将gateway自动代理的服务限制为gateway自己所在的group内
	private boolean restrictedToGroup = true;
	
	//可以管理哪些group，例如：arch-base,arch-midd
	private String incloudGroups;
}
