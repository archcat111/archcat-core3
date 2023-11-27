package org.cat.support.springboot3.starter.exception;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.exception.StandardRuntimeException;
import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.core.web3.info.ArchInfoGenerator;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.support.exception3.generator.SupportException3ConditionalFlag;
import org.cat.support.exception3.generator.code.ExceptionCodeGeneratorForDefault;
import org.cat.support.exception3.generator.code.ExceptionCodeGeneratorForNoUse;
import org.cat.support.exception3.generator.constants.ExceptionSupportConstants;
import org.cat.support.exception3.generator.id.AbsExceptionIdGenerator;
import org.cat.support.exception3.generator.id.ExceptionIdGeneratorForNoUse;
import org.cat.support.exception3.generator.id.ExceptionIdGeneratorImpl;
import org.cat.support.id3.generator.IIdGenerator;
import org.cat.support.id3.generator.IdGeneratorHolder;
import org.cat.support.springboot3.starter.id.IdAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author 王云龙
 * @date 2021年8月30日 下午4:37:48
 * @version 1.0
 * @description 异常相关配置类
 * 		异常相关开始默认为开启
 * 		如果cat.support.exception.enabled=true（默认）：开启自动化配置
 * 		如果cat.support.exception.enabled=false：不进行自动化配置
 * 		如果cat.support.exception.enabled没有值：不进行匹配，不进行自动化配置
 *
 */
@Configuration
@AutoConfigureAfter(IdAutoConfiguration.class)
@ConditionalOnClass(SupportException3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.exception", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(ExceptionProperties.class)
public class ExceptionAutoConfiguration {
	
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	protected final transient String logPrefix = "Exception初始化：";

	@Autowired
	private ExceptionProperties exceptionProperties;
	
	@Autowired(required = false)
	private IdGeneratorHolder idGeneratorHolder;
	
	@Autowired
	private ArchInfoGenerator archInfoGenerator;
	
	@ConditionalOnMissingBean(IExceptionIdGenerator.class)
	@Bean
	public IExceptionIdGenerator exceptionIdGenerator() {
		String idGeneratorName = exceptionProperties.getIdGeneratorName();
		
		AbsExceptionIdGenerator exceptionIdGenerator = null;
		if(ExceptionSupportConstants.IdGenerator.NO_USE.toLowerCase().equals(idGeneratorName.toLowerCase())) {
			exceptionIdGenerator = new ExceptionIdGeneratorForNoUse();
		}else {
			exceptionIdGenerator = new ExceptionIdGeneratorImpl();
			IIdGenerator idGenerator = this.idGeneratorHolder.get(idGeneratorName);
			if(idGenerator==null) {
				throw new StandardRuntimeException(this.logPrefix + "没有找到IdGenerator["+idGeneratorName+"]");
			}
			exceptionIdGenerator.setIdGenerator(idGenerator);
		}
		
		return exceptionIdGenerator;
	}
	
	@ConditionalOnMissingBean(IExceptionCodeGenerator.class)
	@Bean
	public IExceptionCodeGenerator exceptionCodeGenerator() {
		String codeGeneratorType = exceptionProperties.getCodeGeneratorType();
		switch (codeGeneratorType) {
		case ExceptionSupportConstants.CodeGenerator.DEFAULT:
			String exceptionProjectCode = archInfoGenerator.getAppCode();
			if(StringUtils.isBlank(exceptionProjectCode)) {
				throw new StandardRuntimeException(this.logPrefix + "使用默认的ExceptionCodeGenerator必须设置合理的projectCode");
			}
			coreLogger.info("开始初始化ExceptionCodeGeneratorForDefault");
			IExceptionCodeGenerator exceptionCodeGenerator = new ExceptionCodeGeneratorForDefault();
			exceptionCodeGenerator.setProjectCode(exceptionProjectCode);
			return exceptionCodeGenerator;
		case ExceptionSupportConstants.CodeGenerator.NO_USE:
			coreLogger.info("开始初始化ExceptionCodeGeneratorForNoUse");
			return new ExceptionCodeGeneratorForNoUse();
		default:
			throw new StandardRuntimeException(this.logPrefix + "您设置的codeGenerator为["+codeGeneratorType+"]，当前框架未找到您需要的codeGenerator");
		}
	}
	
}
