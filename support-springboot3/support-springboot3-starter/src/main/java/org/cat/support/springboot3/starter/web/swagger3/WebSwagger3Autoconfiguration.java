package org.cat.support.springboot3.starter.web.swagger3;

import org.cat.core.web3.info.ArchInfoGenerator;
import org.cat.support.springboot3.starter.web.WebMvcAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.annotations.Api;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 
 * @author 王云龙
 * @date 2022年5月12日 下午9:55:02
 * @version 1.0
 * @description 坑爹的Swagger3，如果再不改进，可以尝试使用springdoc
 * 		springboot 2.6.0开始就会出现不兼容的问题
 * 		Swagger3的注解很不好用，改到3的注解后发现各种问题
 *
 */
@Configuration
@ConditionalOnClass(EnableOpenApi.class)
@AutoConfigureAfter({WebMvcAutoConfiguration.class})
@ConditionalOnProperty(value = "springfox.documentation.enabled", havingValue = "true", matchIfMissing = true)
public class WebSwagger3Autoconfiguration {
	
	@Autowired
	private ArchInfoGenerator archInfoGenerator;
	
	@Bean
	public Docket createRestApi() {
		Docket docket = new Docket(DocumentationType.OAS_30)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.withClassAnnotation(Api.class)
//						.or(RequestHandlerSelectors.withClassAnnotation(Tag.class))
//						.or(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
//						.or(RequestHandlerSelectors.withMethodAnnotation(Operation.class))
				 )
				.paths(PathSelectors.any())
				.build();
		return docket;
	}
	
	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfoBuilder()
				.title(this.archInfoGenerator.getAppName()+"的Swagger接口文档")
				.description(this.archInfoGenerator.getAppDescription())
				.contact(new Contact(this.archInfoGenerator.getPrincipal(), "www.arch.com", this.archInfoGenerator.getPrincipalEmail()))
				.version(this.archInfoGenerator.getAppVersion())
				.build();
		return apiInfo;
	}
}
