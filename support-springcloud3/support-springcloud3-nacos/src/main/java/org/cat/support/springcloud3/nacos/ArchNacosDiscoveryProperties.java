package org.cat.support.springcloud3.nacos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchNacosDiscoveryProperties {
	
	//List<ServiceInstance> getInstances(String serviceId)是否限制在自己的group内
	//List<String> getServices()是否限制在自己的group内
	private boolean restrictedToGroup = true;
	
	//可以管理哪些group，例如：arch-base,arch-midd
	private String incloudGroups;
}
