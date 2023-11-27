package org.cat.support.springboot3.starter.actuator.props;

import org.cat.support.springboot3.actuator.ActuatorConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.actuator.management")
@Getter
@Setter
public class ActuatorManagementProperties {
	private String portGenerate = ActuatorConstants.Management.PORT_INCR_1;
}
