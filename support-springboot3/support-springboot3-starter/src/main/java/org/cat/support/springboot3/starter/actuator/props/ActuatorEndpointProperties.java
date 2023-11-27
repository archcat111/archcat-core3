package org.cat.support.springboot3.starter.actuator.props;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActuatorEndpointProperties {
	private Ping2EndpointProperties ping2 = new Ping2EndpointProperties();
	
	@Getter
	@Setter
	public class Ping2EndpointProperties {
		private boolean enabled = true;
		private String result = "pong2(Endpoint)";
	}
}
