package org.cat.support.springcloud3.starter.nacos;

import org.cat.core.util3.bean.ArchBeanUtil;
import org.cat.support.springcloud3.nacos.ArchNacosDiscoveryProperties;
import org.cat.support.springcloud3.nacos.ArchNacosServiceDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;

@Configuration
@AutoConfigureBefore(NacosDiscoveryAutoConfiguration.class)
@ConditionalOnClass(ArchNacosServiceDiscovery.class)
@ConditionalOnProperty(prefix = "cat.support3.nacos", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({NacosPlusProperties.class})
public class NacosDiscoveryPlusAutoConfiguration {
	
	@Autowired
	private NacosPlusProperties nacosPlusProperties;
	
	@Bean
	public NacosServiceDiscovery nacosServiceDiscovery(NacosDiscoveryProperties discoveryProperties,
			NacosServiceManager nacosServiceManager) {
		ArchNacosDiscoveryProperties nacosDiscoveryPlusProperties = 
				ArchBeanUtil.toBean(this.nacosPlusProperties, ArchNacosDiscoveryProperties.class);
		
		NacosServiceDiscovery NacosServiceDiscovery = 
				new ArchNacosServiceDiscovery(discoveryProperties, nacosServiceManager, nacosDiscoveryPlusProperties);
		
		return NacosServiceDiscovery;
	}

}
