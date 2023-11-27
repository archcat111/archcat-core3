package org.cat.support.springboot3.starter.web;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.core.util3.date.ArchDateTimeUtil;
import org.cat.core.web3.CoreWeb3ConditionalFlag;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.core.web3.resp.generator.IDownloadGenerator;
import org.cat.core.web3.resp.generator.IResultRespGenerator;
import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.core.web3.resp.generator.IWsRsRespGenerator;
import org.cat.core.web3.util.SpringControllerUtil;
import org.cat.support.springboot3.starter.web.controller.PingController;
import org.cat.support.web3.generator.resp.DownloadGeneratorImpl;
import org.cat.support.web3.generator.resp.ResultRespGeneratorImpl;
import org.cat.support.web3.generator.resp.SpringRespGeneratorImpl;
import org.cat.support.web3.generator.resp.WsRsRespGeneratorImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.google.common.collect.Lists;

@Configuration
//@AutoConfigureAfter({ExceptionAutoConfiguration.class, WebUserAutoConfiguration.class})
@ConditionalOnClass(CoreWeb3ConditionalFlag.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "cat.support3.web", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebMvcProperties.class)
public class WebMvcAutoConfiguration implements WebMvcConfigurer {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "WebMvc初始化：";
	
	@Autowired
	private WebMvcProperties webMvcProperties;
	private WebMvcFileProperties webMvcFileProperties;
	private WebMvcCorsProperties webMvcCorsProperties;
	private WebMvcRespProperties webMvcRespProperties;
	private WebMvcRespConverterProperties webMvcRespConverterProperties;
	private WebMvcTestProperties webMvcTestProperties;
	private WebMvcTestPingProperties webMvcTestPingProperties;
	
	
	@Autowired
	private IExceptionIdGenerator exceptionIdGenerator;
	@Autowired
	private IExceptionCodeGenerator exceptionCodeGenerator;
	
	@PostConstruct
	public void init() {
		this.webMvcFileProperties = this.webMvcProperties.getFile();
		this.webMvcCorsProperties = this.webMvcProperties.getCors();
		this.webMvcRespProperties = this.webMvcProperties.getResp();
		this.webMvcRespConverterProperties = this.webMvcRespProperties.getConverter();
		this.webMvcTestProperties = this.webMvcProperties.getTest();
		this.webMvcTestPingProperties = this.webMvcTestProperties.getPing();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月14日 下午4:34:39
	 * @version 1.0
	 * @description 初始化一个PingController的Bean 
	 * @param requestMappingHandlerMapping
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.web.test.ping", name = "enabled", havingValue = "true", matchIfMissing = false)
	@Bean
	public PingController pingController(RequestMappingHandlerMapping requestMappingHandlerMapping) {
		PingController pingController = new PingController();
		bindPingController(requestMappingHandlerMapping, pingController);
		return pingController;
	}
	private void bindPingController(RequestMappingHandlerMapping requestMappingHandlerMapping, PingController pingController) {
		String[] utlPaths = new String[] {this.webMvcTestPingProperties.getPath()};
		RequestMethod[] requestMethods = new RequestMethod[] {RequestMethod.GET};
		Object controllerInstance = pingController;
		String controllerMethod = "ping";
		Class<?>[] controllerMethodParamTypes = null;
		SpringControllerUtil.registerControllerRequestMapping(
				requestMappingHandlerMapping, 
				utlPaths, 
				requestMethods, 
				controllerInstance, 
				controllerMethod, 
				controllerMethodParamTypes);
	}
	
	
//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addStatusController(webMvcPingProperties.getPingPath(), HttpStatus.OK);
//	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月14日 下午4:35:03
	 * @version 1.0
	 * @description 文件上传相关的配置 
	 * @return
	 * @throws IOException
	 */
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年7月28日 下午4:08:23
	 * @version 1.0
	 * @description 文件上传相关的配置，会覆盖Spring Boot的MultipartAutoConfiguration的生成
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.web.file", name = "enabled", havingValue = "true", matchIfMissing = false)
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
		map.from(this.webMvcFileProperties.getMaxInMemorySize()).to(factory::setFileSizeThreshold);
		map.from(this.webMvcFileProperties.getUploadTempDir()).whenHasText().to(factory::setLocation);
		map.from(this.webMvcFileProperties.getMaxUploadSize()).to(factory::setMaxRequestSize);
		map.from(this.webMvcFileProperties.getMaxUploadSizePerFile()).to(factory::setMaxFileSize);
		
		MultipartConfigElement multipartConfigElement = factory.createMultipartConfig();
		return multipartConfigElement;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年7月28日 下午4:09:00
	 * @version 1.0
	 * @description 文件上传相关的配置，会覆盖Spring Boot的MultipartAutoConfiguration的生成
	 * 		这里定义这个主要目的是统一配置的地方 
	 * @return
	 */
	@Bean
	public StandardServletMultipartResolver multipartResolver() {
		StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
		multipartResolver.setResolveLazily(this.webMvcFileProperties.isResolveLazily());
		return multipartResolver;
	}
	
	/**
	 * 推荐使用StandardServletMultipartResolver
	 * 因为CommonsMultipartResolver是基于commons-upload实现的
	 * 而StandardServletMultipartResolver是Servlet 3.0实现的，不需要依赖其他
	 */
//	@Bean
//	public MultipartResolver getMultipartResolver() throws IOException{
//		coreLogger.info(this.logPrefix + "开始初始化CommonsMultipartResolver");
//		CommonsMultipartResolver commonsMultipartResolver=new CommonsMultipartResolver();
//		commonsMultipartResolver.setDefaultEncoding(this.webMvcFileProperties.getHeaderEncoding());
//		commonsMultipartResolver.setMaxInMemorySize(this.webMvcFileProperties.getMaxInMemorySize());
//		commonsMultipartResolver.setMaxUploadSize(this.webMvcFileProperties.getMaxUploadSize());
//		commonsMultipartResolver.setMaxUploadSizePerFile(this.webMvcFileProperties.getMaxUploadSizePerFile());
//		commonsMultipartResolver.setPreserveFilename(this.webMvcFileProperties.isPreserveFilename());
//		commonsMultipartResolver.setResolveLazily(this.webMvcFileProperties.isResolveLazily());
//		if(StringUtils.isNoneBlank(this.webMvcFileProperties.getUploadTempDir())) {
//			Resource uploadTempDirResorce = new FileSystemResource(this.webMvcFileProperties.getUploadTempDir());
//			commonsMultipartResolver.setUploadTempDir(uploadTempDirResorce);
//		}
//		return commonsMultipartResolver;
//	}

	/**
	 * 跨域相关
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		if(this.webMvcCorsProperties == null || this.webMvcCorsProperties.isEnabled() == false) {
			coreLogger.info(this.logPrefix + "放弃初始化Cors相关配置");
			return;
		}
		coreLogger.info(this.logPrefix + "开始初始化Cors相关配置");
		List<WebMvcCorsRuleProperties> webMvcCorsRuleProperties = webMvcCorsProperties.getCorsRules();
		if(webMvcCorsRuleProperties ==null || webMvcCorsRuleProperties.isEmpty()) {
			coreLogger.warn(this.logPrefix + "您开启了自动化配置cors开关，但是并没有设置对应的corsRule");
		}else {
			for (Iterator<WebMvcCorsRuleProperties> iterator = webMvcCorsRuleProperties.iterator(); iterator.hasNext();) {
				WebMvcCorsRuleProperties webMvcCorsRuleProperty = iterator.next();
				if(!webMvcCorsRuleProperty.isEnabled()) {
					continue;
				}
				String name = webMvcCorsRuleProperty.getName();
				String mapping = webMvcCorsRuleProperty.getMapping();
				List<String> allowedOrigins = webMvcCorsRuleProperty.getAllowedOrigins();
				Boolean allowCredentials = webMvcCorsRuleProperty.getAllowCredentials();
				List<String> allowedHeaders = webMvcCorsRuleProperty.getAllowedHeaders();
				List<String> allowedMethods = webMvcCorsRuleProperty.getAllowedMethods();
				Long maxAge = webMvcCorsRuleProperty.getMaxAge();
				
				if(StringUtils.isBlank(mapping) || !mapping.startsWith("/")) {
					coreLogger.error(this.logPrefix + "初始化Cors相关配置时，发现错误的跨域配置："+name+"的mapping配置不合法["+mapping+"]");
					continue;
				}
				CorsRegistration corsRegistration=registry.addMapping(mapping);
				
				if(allowedOrigins!=null && allowedOrigins.size()>0) {
					corsRegistration.allowedOrigins(allowedOrigins.stream().toArray(String[]::new));
				}
				
				if(allowCredentials!=null) {
					corsRegistration.allowCredentials(allowCredentials);
				}
				
				if(allowedHeaders!=null && allowedHeaders.size()>0) {
					corsRegistration.allowedHeaders(allowedHeaders.stream().toArray(String[]::new));
				}
				
				if(allowedMethods!=null && allowedMethods.size()>0) {
					corsRegistration.allowedMethods(allowedMethods.stream().toArray(String[]::new));
				}
				
				if(maxAge!=null) {
					corsRegistration.maxAge(maxAge);
				}
				
			}
		}
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月14日 下午4:35:27
	 * @version 1.0
	 * @description 下载组件 
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.web.resp", name = "openDownloadGenerator", havingValue = "true", matchIfMissing = false)
	@Bean
	public IDownloadGenerator downloadGenerator() {
		IDownloadGenerator downloadGenerator=new DownloadGeneratorImpl();
		return downloadGenerator;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月14日 下午4:35:40
	 * @version 1.0
	 * @description resultResp相关组件 
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.web.resp", name = "openResultRespGenerator", havingValue = "true", matchIfMissing = true)
	@Bean
	public IResultRespGenerator resultRespGenerator() {
		ResultRespGeneratorImpl resultRespGenerator=new ResultRespGeneratorImpl();
		resultRespGenerator.setExceptionIdGenerator(this.exceptionIdGenerator);
		resultRespGenerator.setExceptionCodeGenerator(this.exceptionCodeGenerator);
		return resultRespGenerator;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月14日 下午4:35:52
	 * @version 1.0
	 * @description ResponseEntity和httpServletResponse.PrintWriter.write相关组件 
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.web.resp", name = "openSpringRespGenerator", havingValue = "true", matchIfMissing = false)
	@Bean
	public ISpringRespGenerator springRespGenerator() {
		SpringRespGeneratorImpl springRespGenerator=new SpringRespGeneratorImpl();
		springRespGenerator.setExceptionIdGenerator(this.exceptionIdGenerator);
		springRespGenerator.setExceptionCodeGenerator(this.exceptionCodeGenerator);
		return springRespGenerator;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月14日 下午4:36:45
	 * @version 1.0
	 * @description WsRs相关组件 
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.web.resp", name = "openWsrsRespGenerator", havingValue = "true", matchIfMissing = false)
	@Bean
	public IWsRsRespGenerator wsRsRespGenerator() {
		WsRsRespGeneratorImpl wsRsRespGenerator=new WsRsRespGeneratorImpl();
		wsRsRespGenerator.setExceptionIdGenerator(this.exceptionIdGenerator);
		wsRsRespGenerator.setExceptionCodeGenerator(this.exceptionCodeGenerator);
		return wsRsRespGenerator;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月14日 下午4:36:56
	 * @version 1.0
	 * @description 响应的序列化相关组件 
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.web.resp.converter", name = "enabled", havingValue = "true", matchIfMissing = false)
	@Bean
	public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
		
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    if(this.webMvcRespConverterProperties.isConverterLongToString()) {
	    	SimpleModule simpleModule = new SimpleModule();
		    simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		    simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
		    objectMapper.registerModule(simpleModule);
	    }
	    if(this.webMvcRespConverterProperties.isConverterDateToString()) {
	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ArchDateTimeUtil.FormatConstants.DATETIME_NORMAL);
		    objectMapper.setDateFormat(simpleDateFormat);
	    }
	    if(this.webMvcRespConverterProperties.isConverterLocalDateTime()) {
	    	JavaTimeModule javaTimeModule = new JavaTimeModule();
			//日期序列化
	        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(
	        		DateTimeFormatter.ofPattern(ArchDateTimeUtil.FormatConstants.DATETIME_NORMAL)));
	        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(
	        		DateTimeFormatter.ofPattern(ArchDateTimeUtil.FormatConstants.DATE_NORMAL)));
	        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(
	        		DateTimeFormatter.ofPattern(ArchDateTimeUtil.FormatConstants.TIME_NORMAL)));

	        //日期反序列化
	        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
	        		DateTimeFormatter.ofPattern(ArchDateTimeUtil.FormatConstants.DATETIME_NORMAL)));
	        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(
	        		DateTimeFormatter.ofPattern(ArchDateTimeUtil.FormatConstants.DATE_NORMAL)));
	        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(
	        		DateTimeFormatter.ofPattern(ArchDateTimeUtil.FormatConstants.TIME_NORMAL)));
	        
	        objectMapper.registerModule(javaTimeModule);
	    }
	    
	    jackson2HttpMessageConverter.setObjectMapper(objectMapper);
	    
    	jackson2HttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON));
	    
	    return jackson2HttpMessageConverter;
	}
	
	
}
