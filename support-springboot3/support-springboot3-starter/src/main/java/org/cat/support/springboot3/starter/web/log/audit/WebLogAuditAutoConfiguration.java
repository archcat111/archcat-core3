package org.cat.support.springboot3.starter.web.log.audit;

import org.cat.core.web3.info.ArchInfoGenerator;
import org.cat.core.web3.log.audit.AuditLogAspect;
import org.cat.core.web3.log.audit.AuditLogPulisher;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.core.web3.user.IUserGenerator;
import org.cat.support.id3.generator.IIdGenerator;
import org.cat.support.id3.generator.IdGeneratorHolder;
import org.cat.support.log3.generator.SupportLog3ConditionalFlag;
import org.cat.support.log3.generator.audit.listener.AuditLogLocalListener;
import org.cat.support.log3.generator.constants.LogSupportConstants;
import org.cat.support.log3.generator.exception.LogException;
import org.cat.support.log3.generator.id.AbsLogIdGenerator;
import org.cat.support.log3.generator.id.LogIdGeneratorForNoUse;
import org.cat.support.log3.generator.id.LogIdGeneratorImpl;
import org.cat.support.springboot3.starter.condition.ConditionalOnProperty2;
import org.cat.support.springboot3.starter.web.log.WebLogAutoConfiguration;
import org.cat.support.springboot3.starter.web.log.WebLogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(WebLogAutoConfiguration.class)
@ConditionalOnClass(SupportLog3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.web.log.audit", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(value = {WebLogProperties.class})
public class WebLogAuditAutoConfiguration {

	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	protected final transient String logPrefix = "Audit日志初始化：";

	@Autowired
	private ArchInfoGenerator archInfoGenerator;
	@Autowired
	private WebLogProperties webLogProperties;
	
	@Autowired
	private IdGeneratorHolder idGeneratorHolder;
	@Autowired
	private IUserGenerator userGenerator;
	
	@Bean
	public AuditLogAspect auditLogAspect(AuditLogPulisher auditLogPulisher) {
		
		AuditLogAspect auditLogAspect = new AuditLogAspect();
		auditLogAspect.setCompany(this.archInfoGenerator.getCompany());
		auditLogAspect.setPlatform(this.archInfoGenerator.getPlatform());
		auditLogAspect.setAppName(archInfoGenerator.getAppName());
		auditLogAspect.setAppVersion(archInfoGenerator.getAppVersion());
		auditLogAspect.setFramework(this.archInfoGenerator.getFramework());
		auditLogAspect.setFrameworkSub(this.archInfoGenerator.getFrameworkSub());
		auditLogAspect.setHostName(this.archInfoGenerator.getHostName());
		auditLogAspect.setHostIp(this.archInfoGenerator.getHostIp());
		
		WebLogAuditProperties webLogAuditProperties = webLogProperties.getAudit();
		
		//logIdGererator
		String logIdGeneratorName = webLogAuditProperties.getIdGeneratorName();
		AbsLogIdGenerator logIdGenerator = null;
		if(LogSupportConstants.IdGenerator.NO_USE.toLowerCase().equals(logIdGeneratorName.toLowerCase())) {
			logIdGenerator = new LogIdGeneratorForNoUse();
		}else {
			logIdGenerator = new LogIdGeneratorImpl();
			IIdGenerator idGenerator = this.idGeneratorHolder.get(logIdGeneratorName);
			if(idGenerator==null) {
				throw new LogException(this.logPrefix + "没有找到IdGenerator["+logIdGeneratorName+"]");
			}
			logIdGenerator.setIdGenerator(idGenerator);
		}
		
		auditLogAspect.setLogIdGenerator(logIdGenerator);
		auditLogAspect.setUserGenerator(userGenerator);
		
		return auditLogAspect;
	}
	
	@Bean
	public AuditLogPulisher auditLogPulisher() {
		AuditLogPulisher auditLogPulisher = new AuditLogPulisher();
		return auditLogPulisher;
	}
	
	@ConditionalOnProperty2(prefix = "cat.support3.web.log.audit",name = "out",  havingValue = "local", matchIfMissing = false)
	@Bean
	public AuditLogLocalListener auditLogLocalListener() {
		AuditLogLocalListener auditLogLocalListener = new AuditLogLocalListener();
		return auditLogLocalListener;
	}
}
