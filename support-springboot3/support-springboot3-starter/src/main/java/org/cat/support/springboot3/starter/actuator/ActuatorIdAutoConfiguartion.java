package org.cat.support.springboot3.starter.actuator;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.id3.generator.IdGeneratorHolder;
import org.cat.support.id3.generator.SupportId3ConditionalFlag;
import org.cat.support.springboot3.actuator.SupportActuator3ConditionalFlag;
import org.cat.support.springboot3.actuator.health.Id2HealthIndicator;
import org.cat.support.springboot3.starter.actuator.props.ActuatorProperties;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnClass({SupportActuator3ConditionalFlag.class, SupportId3ConditionalFlag.class})
@ConditionalOnProperty(prefix = "cat.support3.actuator", name = "enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE-10)
@EnableConfigurationProperties(ActuatorProperties.class)
public class ActuatorIdAutoConfiguartion {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Actuator初始化：";
	
	@Autowired(required = false)
	private IdGeneratorHolder idGeneratorHolder;
	
	@ConditionalOnBean(IdGeneratorHolder.class)
	@ConditionalOnProperty(prefix = "cat.support3.actuator.health.id2", name = "enabled", havingValue = "true", matchIfMissing = true)
	@ConditionalOnEnabledHealthIndicator("id2")
	@Bean
	public Id2HealthIndicator id2HealthIndicator() {
		Id2HealthIndicator id2HealthIndicator = new Id2HealthIndicator(this.idGeneratorHolder);
		return id2HealthIndicator;
	}
	
}
