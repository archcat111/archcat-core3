package org.cat.support.springboot3.starter.actuator.props;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorHealthIndicatorProperties {
	private Ping2HealthIndicatorProperties ping2 = new Ping2HealthIndicatorProperties();
	
	@Getter
	@Setter
	public class Ping2HealthIndicatorProperties {
		private boolean enabled = true;
		private String result = "pong2(HealthIndicator)";
	}
}
