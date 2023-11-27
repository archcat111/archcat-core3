package org.cat.support.springboot3.starter.web.log;

import org.cat.support.springboot3.starter.web.log.audit.WebLogAuditProperties;
import org.cat.support.springboot3.starter.web.log.event.WebLogEventProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.web.log")
@Getter
@Setter
public class WebLogProperties {
	
	private boolean enabled;
	
	//选择系统中的多个实现之一，如果系统中存在用户的实现，则不会使用support的默认实现
//	private String idGenerator = LogConstants.IdGenerator.DEFAULT;
	
	private WebLogEventProperties event;
	private WebLogAuditProperties audit;
}
