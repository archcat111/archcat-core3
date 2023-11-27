package org.cat.support.springboot3.starter.web.validation;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.cat.support.springboot3.starter.exception.ExceptionAutoConfiguration;
import org.cat.support.web3.generator.SupportWeb3ConditionalFlag;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(ExceptionAutoConfiguration.class)
@ConditionalOnClass(SupportWeb3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.web.validator", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebValidatorProperties.class)
public class WebValidatorAutoConfiguration {
	
	@Autowired
	private WebValidatorProperties webValidationProperties;
	
	@Bean
	public Validator validator() {
		HibernateValidatorConfiguration hibernateValidatorConfiguration = Validation.byProvider(HibernateValidator.class).configure();
		if(webValidationProperties.isFailFast()) {
			hibernateValidatorConfiguration .failFast(true); //快速失败返回模式 .addProperty("hibernate.validator.fail_fast", "true")
		}
		ValidatorFactory validatorFactory = hibernateValidatorConfiguration.buildValidatorFactory();
        
		Validator validator = validatorFactory.getValidator();
		
		return validator;
	}
//	
//	/**
//     * 开启快速返回
//     * 如果参数校验有异常，直接抛异常，不会进入到 controller，使用全局异常拦截进行拦截
//     */
//    @Bean
//    public MethodValidationPostProcessor methodValidationPostProcessor() {
//        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
//        /**设置validator模式为快速失败返回*/
//        postProcessor.setValidator(validator());
//        return postProcessor;
//    }

}
