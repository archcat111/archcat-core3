package org.cat.support.springboot3.starter.web.log;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.log3.generator.SupportLog3ConditionalFlag;
import org.cat.support.springboot3.starter.id.IdAutoConfiguration;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(IdAutoConfiguration.class)
@ConditionalOnClass(SupportLog3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.web.log", name = "enabled",  havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(value = {WebLogProperties.class})
public class WebLogAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	
//	@Autowired
//	private WebLogProperties webLogProperties;
	
	
}
