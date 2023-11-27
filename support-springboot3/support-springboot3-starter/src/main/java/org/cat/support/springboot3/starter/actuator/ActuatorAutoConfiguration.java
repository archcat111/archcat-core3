package org.cat.support.springboot3.starter.actuator;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.info.ArchInfoGenerator;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.springboot3.actuator.ActuatorConstants;
import org.cat.support.springboot3.actuator.SupportActuator3ConditionalFlag;
import org.cat.support.springboot3.actuator.endpoint.Ping2Endpoint;
import org.cat.support.springboot3.actuator.health.Ping2HealthIndicator;
import org.cat.support.springboot3.actuator.info.Info2Endpoint;
import org.cat.support.springboot3.starter.actuator.props.ActuatorEndpointProperties;
import org.cat.support.springboot3.starter.actuator.props.ActuatorHealthIndicatorProperties;
import org.cat.support.springboot3.starter.actuator.props.ActuatorInfo2Properties;
import org.cat.support.springboot3.starter.actuator.props.ActuatorProperties;
import org.cat.support.springboot3.starter.actuator.props.ActuatorEndpointProperties.Ping2EndpointProperties;
import org.cat.support.springboot3.starter.actuator.props.ActuatorHealthIndicatorProperties.Ping2HealthIndicatorProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnClass(SupportActuator3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.actuator", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE-10)
@EnableConfigurationProperties(ActuatorProperties.class)
public class ActuatorAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Actuator初始化：";
	
	@Autowired
	private ArchInfoGenerator archInfoProperties;
	
	@Autowired
	private ActuatorProperties actuatorProperties;
	
	
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "cat.support3.actuator.info2", name = "enabled", havingValue = "true", matchIfMissing = true)
	@Bean
	public Info2Endpoint info2Endpoint(List<InfoContributor> infoContributors) {
		ActuatorInfo2Properties actuatorInfo2Properties = this.actuatorProperties.getInfo2();
		
		Info2Endpoint info2Endpoint = null;
		if(ActuatorConstants.Info.ARCH.equals(actuatorInfo2Properties.getActivation())) {
			info2Endpoint = new Info2Endpoint(this.archInfoProperties);
		}else if(ActuatorConstants.Info.DEFAULT.equals(actuatorInfo2Properties.getActivation())) {
			info2Endpoint = new Info2Endpoint(infoContributors);
		}else {
			throw new IllegalArgumentException(this.logPrefix+"初始化Info2Endpoint错误，Activation不正确");
		}
		this.coreLogger.info(this.logPrefix + "初始化Info2Endpoint完成");
		return info2Endpoint;
	}
	
	/**
	 * @author wangyunlong
	 * @date 2021年9月7日 上午10:57:02
	 * @version 1.0
	 * @description a 
	 * 		ConditionalOnEnabledHealthIndicator表示：
	 * 			{@code management.health.<name>.enabled} 值为 {@code true}，则匹配
	 * 			或者，{@code management.health.defaults.enabled} 值为 {@code true} or 未配置，则匹配
	 * @return DemoHealthIndicator
	 */
	@ConditionalOnProperty(prefix = "cat.support3.actuator.health.ping2", name = "enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnEnabledHealthIndicator("ping2")
	@Bean
	public Ping2HealthIndicator ping2HealthIndicator() {
		ActuatorHealthIndicatorProperties actuatorHealthIndicatorProperties = this.actuatorProperties.getHealth();
		Ping2HealthIndicatorProperties ping2HealthIndicatorProperties = actuatorHealthIndicatorProperties.getPing2();
		String ping2HealthResult = ping2HealthIndicatorProperties.getResult();
		
		Ping2HealthIndicator ping2HealthIndicator = null;
		if(StringUtils.isBlank(ping2HealthResult)) {
			ping2HealthIndicator = new Ping2HealthIndicator();
		}else {
			ping2HealthIndicator = new Ping2HealthIndicator(ping2HealthResult);
		}
		
		this.coreLogger.info(this.logPrefix + "初始化ping2HealthIndicator完成");
		return ping2HealthIndicator;
	}
	
	
	@ConditionalOnProperty(prefix = "cat.support3.actuator.endpoint.ping2", name = "enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public Ping2Endpoint ping2Endpoint() {
		ActuatorEndpointProperties actuatorEndpointProperties = this.actuatorProperties.getEndpoint();
		Ping2EndpointProperties ping2EndpointProperties = actuatorEndpointProperties.getPing2();
		String ping2EndpointResult = ping2EndpointProperties.getResult();
		
		Ping2Endpoint ping2Endpoint = null;
		if(StringUtils.isBlank(ping2EndpointResult)) {
			ping2Endpoint = new Ping2Endpoint();
		}else {
			ping2Endpoint = new Ping2Endpoint(ping2EndpointResult);
		}
		
		this.coreLogger.info(this.logPrefix + "初始化ping2Endpoint完成");
		return ping2Endpoint;
	}
}
