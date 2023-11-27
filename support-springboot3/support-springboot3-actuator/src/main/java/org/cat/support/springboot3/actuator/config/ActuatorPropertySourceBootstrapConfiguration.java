package org.cat.support.springboot3.actuator.config;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.cat.support.springboot3.actuator.ActuatorConstants;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import com.google.common.collect.Maps;

@Order(Ordered.HIGHEST_PRECEDENCE + 14)
public class ActuatorPropertySourceBootstrapConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private final String SERVER_PORT = "server.port";
	private final String ACTUATOR_SERVER_PORT = "management.server.port";
	private final String ACTUATOR_PORT_GENERATE = "cat.support3.actuator.management.portGenerate";
	
	private final String ACTUATOR_PROPERTIES_OVERWRITE = "actuator_properties_overwrite";
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		ConfigurableEnvironment configurableEnvironment = applicationContext.getEnvironment();
		
		Integer serverPort = configurableEnvironment.getProperty(SERVER_PORT, Integer.class);
		Integer actuatorServerPort = configurableEnvironment.getProperty(ACTUATOR_SERVER_PORT, Integer.class);
		String actuatorPortGenerate = configurableEnvironment.getProperty(ACTUATOR_PORT_GENERATE, String.class);
		if(StringUtils.isBlank(actuatorPortGenerate)) {
			return;
		}
		if(actuatorServerPort!=null) {
			return;
		}
		if(actuatorPortGenerate.equals(ActuatorConstants.Management.PORT_INCR_1)) {
			int newActuatorPort = serverPort+1;
			MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources();
			Map<String, Object> actuatorPropertiesOverwrite = Maps.newHashMap();
			actuatorPropertiesOverwrite.put(ACTUATOR_SERVER_PORT, newActuatorPort);
			PropertySource<?> propertySource = new MapPropertySource(ACTUATOR_PROPERTIES_OVERWRITE, actuatorPropertiesOverwrite);
			mutablePropertySources.addFirst(propertySource);
		}
	}

}
