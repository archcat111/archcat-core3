package org.cat.support.springboot3.starter.reactive;

import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.core.web3.CoreWeb3ConditionalFlag;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.core.web3.resp.generator.IResultRespGenerator;
import org.cat.support.web3.generator.resp.ResultRespGeneratorImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(CoreWeb3ConditionalFlag.class)
@ConditionalOnWebApplication(type = Type.REACTIVE)
@ConditionalOnProperty(prefix = "cat.support3.reactive", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ReactiveMvcProperties.class)
public class ReactiveMvcAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "ReactiveMvc初始化：";
	
//	@Autowired
//	private ReactiveMvcProperties webMvcProperties;
//	private ReactiveMvcRespProperties webMvcRespProperties;
	
	@Autowired
	private IExceptionIdGenerator exceptionIdGenerator;
	@Autowired
	private IExceptionCodeGenerator exceptionCodeGenerator;
	
//	@PostConstruct
//	public void init() {
//		this.webMvcRespProperties = this.webMvcProperties.getResp();
//	}
	
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月14日 下午4:35:40
	 * @version 1.0
	 * @description resultResp相关组件 
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.reactive.resp", name = "openResultRespGenerator", havingValue = "true", matchIfMissing = true)
	@Bean
	public IResultRespGenerator resultRespGenerator() {
		ResultRespGeneratorImpl resultRespGenerator=new ResultRespGeneratorImpl();
		resultRespGenerator.setExceptionIdGenerator(this.exceptionIdGenerator);
		resultRespGenerator.setExceptionCodeGenerator(this.exceptionCodeGenerator);
		return resultRespGenerator;
	}
	
}
