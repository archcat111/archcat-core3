package org.cat.support.springboot3.starter.actuator.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.actuator")
@Getter
@Setter
public class ActuatorProperties {
	
	private boolean enabled;
	
	private ActuatorManagementProperties management = new ActuatorManagementProperties();
	private ActuatorInfo2Properties info2 = new ActuatorInfo2Properties();
	private ActuatorHealthIndicatorProperties health = new ActuatorHealthIndicatorProperties();
	private ActuatorEndpointProperties endpoint = new ActuatorEndpointProperties();

}
