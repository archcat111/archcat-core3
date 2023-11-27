package org.cat.support.springboot3.starter.web.security;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.slf4j.Logger;

//@Configuration
//@AutoConfigureAfter(WebMvcAutoConfiguration.class)
//@ConditionalOnWebApplication(type = Type.REACTIVE)
//@ConditionalOnClass(SecurityConstants.class)
//@ConditionalOnProperty(prefix = "cat.support3.web.security", name = "enabled",  havingValue = "true", matchIfMissing = false)
//@EnableConfigurationProperties(value = {SecurityProperties.class})
public class SecurityAutoWebfluxConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	
//	@Autowired
//	private SecurityProperties securityProperties;
	
//	@ConditionalOnProperty(prefix = "cat.support3.web.security", name = "implFrame",  havingValue = "spring", matchIfMissing = false)
//	@Bean
//	public WebSecurityConfigurerAdapterExt webSecurityConfigurerAdapterExt() {
//		WebSecurityConfigurerAdapterExt webSecurityConfigurerAdapterExt = new WebSecurityConfigurerAdapterExt();
//		return webSecurityConfigurerAdapterExt;
//	}
	
	//RedisSession相关的可以参考RedisReactiveSessionConfiguration
	
}
