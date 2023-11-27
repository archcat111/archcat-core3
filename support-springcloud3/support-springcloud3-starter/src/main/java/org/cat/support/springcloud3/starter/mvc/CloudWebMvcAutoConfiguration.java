package org.cat.support.springcloud3.starter.mvc;

import org.cat.support.springcloud3.web.feign.SpringCloudWebMvcRegistrations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SpringCloudWebMvcRegistrations.class)
@ConditionalOnProperty(prefix = "cat.support3.web.cloud", name = "enabled", havingValue = "true", matchIfMissing = true)
//@EnableConfigurationProperties({ArchGatewaySessionProperties.class})
public class CloudWebMvcAutoConfiguration {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年8月15日 下午7:49:17
	 * @version 1.0
	 * @description 定义Spring注册Controller的注册条件
	 * 		默认：定义了@Controller 或者 @RequestMapping，都会被Spring认为是Controller
	 * 		这里：只有定义了@Controller的类才会被认为是Controller 
	 * @return
	 */
	public WebMvcRegistrations webMvcRegistrations() {
		WebMvcRegistrations webMvcRegistrations = new SpringCloudWebMvcRegistrations();
		return webMvcRegistrations;
	}
}
