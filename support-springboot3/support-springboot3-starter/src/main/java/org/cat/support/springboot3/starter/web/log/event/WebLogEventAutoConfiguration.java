package org.cat.support.springboot3.starter.web.log.event;

import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.core.web3.info.ArchInfoGenerator;
import org.cat.core.web3.log.event.EventLogAspect;
import org.cat.core.web3.log.event.EventLogPulisher;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.core.web3.user.IUserGenerator;
import org.cat.support.id3.generator.IIdGenerator;
import org.cat.support.id3.generator.IdGeneratorHolder;
import org.cat.support.log3.generator.SupportLog3ConditionalFlag;
import org.cat.support.log3.generator.constants.LogSupportConstants;
import org.cat.support.log3.generator.event.listener.EventLogLocalListener;
import org.cat.support.log3.generator.exception.LogException;
import org.cat.support.log3.generator.id.AbsLogIdGenerator;
import org.cat.support.log3.generator.id.LogIdGeneratorForNoUse;
import org.cat.support.log3.generator.id.LogIdGeneratorImpl;
import org.cat.support.springboot3.starter.condition.ConditionalOnProperty2;
import org.cat.support.springboot3.starter.web.log.WebLogAutoConfiguration;
import org.cat.support.springboot3.starter.web.log.WebLogProperties;
import org.slf4j.Logger;
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
@ConditionalOnProperty(prefix = "cat.support3.web.log.event", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(value = {WebLogProperties.class})
public class WebLogEventAutoConfiguration {

	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Event日志初始化：";

	@Autowired
	private ArchInfoGenerator archInfoGenerator;
	@Autowired
	private WebLogProperties webLogProperties;
	
	@Autowired
	private IdGeneratorHolder idGeneratorHolder;
	@Autowired
	private IUserGenerator userGenerator;
	@Autowired
	private IExceptionIdGenerator exceptionIdGenerator;
	@Autowired
	private IExceptionCodeGenerator exceptionCodeGenerator;
	
	@Bean
	public EventLogAspect eventLogAspect(EventLogPulisher eventLogPulisher) {
		
		EventLogAspect eventLogAspect = new EventLogAspect();
		eventLogAspect.setExceptionIdGenerator(this.exceptionIdGenerator);
		eventLogAspect.setExceptionCodeGenerator(this.exceptionCodeGenerator);
		eventLogAspect.setCompany(this.archInfoGenerator.getCompany());
		eventLogAspect.setPlatform(this.archInfoGenerator.getPlatform());
		eventLogAspect.setAppName(this.archInfoGenerator.getAppName());
		eventLogAspect.setAppVersion(this.archInfoGenerator.getAppVersion());
		eventLogAspect.setFramework(this.archInfoGenerator.getFramework());
		eventLogAspect.setFrameworkSub(this.archInfoGenerator.getFrameworkSub());
		eventLogAspect.setHostName(this.archInfoGenerator.getHostName());
		eventLogAspect.setHostIp(this.archInfoGenerator.getHostIp());
		eventLogAspect.setHostPort(this.archInfoGenerator.getPort());
		
		WebLogEventProperties webLogEventProperties = webLogProperties.getEvent();
		
		//logIdGererator
		String logIdGeneratorName = webLogEventProperties.getIdGeneratorName();
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
		
		eventLogAspect.setLogIdGenerator(logIdGenerator);
		eventLogAspect.setUserGenerator(userGenerator);
		
		return eventLogAspect;
	}
	
	@Bean
	public EventLogPulisher eventLogPulisher() {
		EventLogPulisher eventLogPulisher = new EventLogPulisher();
		return eventLogPulisher;
	}
	
	@ConditionalOnProperty2(prefix = "cat.support3.web.log.event",name = "out",  havingValue = "local", matchIfMissing = false)
	@Bean
	public EventLogLocalListener eventLogLocalListener() {
		EventLogLocalListener eventLogLocalListener = new EventLogLocalListener();
		return eventLogLocalListener;
	}
	
	
}
