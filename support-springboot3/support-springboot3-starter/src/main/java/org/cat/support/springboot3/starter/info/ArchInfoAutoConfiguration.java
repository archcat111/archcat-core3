package org.cat.support.springboot3.starter.info;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.cat.core.exception3.exception.StandardRuntimeException;
import org.cat.core.util3.maven.ArchMavenUtil;
import org.cat.core.util3.maven.ArchMavenUtilException;
import org.cat.core.util3.system.ArchLocalNetWorkArchUtil;
import org.cat.core.web3.constants.ArchConstants;
import org.cat.core.web3.info.ArchInfoGenerator;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.util.ResourceUtils;

import cn.hutool.core.date.LocalDateTimeUtil;

@Configuration
@ConditionalOnClass(ArchInfoGenerator.class)
@ConditionalOnProperty(prefix = "cat.support3.info", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ArchInfoAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "ArchInfo初始化：";
	
	@Autowired
	private ConfigurableApplicationContext configurableApplicationContext;
	
	@Autowired(required = false)
	private GitProperties gitProperties;
	
	@Autowired(required = false)
	private BuildProperties buildProperties;
	
	@Value("${server.port:-1}")
	private String port;
	
	@Value("${spring.application.company:未知}")
	private String company; 
	@Value("${spring.application.platform:未知}")
	private String platform; 
	@Value("${spring.application.code:未知}")
	private String appCode; 
	@Value("${spring.application.name:未知}")
	private String appName; //配置文件中配置 "@project.name@"，即：从pom文件获取
	
	@Bean
	public ArchInfoGenerator archInfoGenerator() {
		
		//DefaultStartupStep
		Model model = null;
		try {
			model = getPomModel();
		} catch (IOException e) {
			throw new ArchMavenUtilException("获取PomModel异常", e);
		}
		
		ArchInfoGenerator archInfoGenerator = new ArchInfoGenerator();
		
		archInfoGenerator.setCompany(this.company); //home-arch
		archInfoGenerator.setPlatform(this.platform); //home-arch-base
		archInfoGenerator.setFramework(ArchConstants.Framework.JAVA);
		archInfoGenerator.setFrameworkSub(this.getFrameworkSub(model));
		archInfoGenerator.setPrincipal(this.getPrincipal(model));
		archInfoGenerator.setPrincipalEmail(this.getPrincipalEmail(model));
		
		archInfoGenerator.setHostName(ArchLocalNetWorkArchUtil.getHostName());
		archInfoGenerator.setHostIp(ArchLocalNetWorkArchUtil.getHostIpV2());
		archInfoGenerator.setPort(this.port);
		archInfoGenerator.setAppCode(appCode);
		archInfoGenerator.setAppName(this.appName);
		archInfoGenerator.setAppVersion(this.getAppVersion(model));
		archInfoGenerator.setAppDescription(this.getAppDescription(model));
		archInfoGenerator.setBuildTime(LocalDateTimeUtil.of(this.buildProperties.getTime()));
		
		if(this.gitProperties==null) {
			coreLogger.warn(this.logPrefix+"您的应用没有引入git-commit-id-plugin或者不是Git应用，如果确认没有问题请忽略...");
		}else {
			archInfoGenerator.setGitBranch(this.gitProperties.getBranch());
			archInfoGenerator.setGitCommitId(this.gitProperties.getCommitId());
			archInfoGenerator.setGitCommitTime(LocalDateTimeUtil.of(this.gitProperties.getCommitTime()));
		}
		archInfoGenerator.setAppStartTime(getAppStartTime());
		
		return archInfoGenerator;
	}
	
	private Model getPomModel() throws IOException {
		if(new File("pom.xml").exists()) {
			return ArchMavenUtil.getPomModel("pom.xml");
		}
		String path = ResourceUtils.CLASSPATH_URL_PREFIX+"META-INF/maven/**/pom.xml";
		Resource[] pomResource = new PathMatchingResourcePatternResolver().getResources(path);
		if(pomResource.length>0) {
			if(pomResource.length>1) {
				this.coreLogger.error(this.logPrefix+"META-INF/maven/**/pom.xml下获取到了多个pom.xml，请检查");
			}
			InputStream inputStream = pomResource[0].getInputStream();
			return ArchMavenUtil.getPomModel(inputStream);
		}else {
			throw new StandardRuntimeException(this.logPrefix + "未找到pom.xml，路径为["+path+"]");
		}

//		String path = ResourceUtils.CLASSPATH_URL_PREFIX+"META-INF/maven/"+this.buildProperties.getGroup()+"/"+this.buildProperties.getArtifact()+"/pom.xml";
//		Resource pomResource = new PathMatchingResourcePatternResolver().getResource(path);
	}
	
	private String getAppVersion(Model model) {
		String version = model.getVersion();
		if(StringUtils.isBlank(version)) {
			version = model.getParent().getVersion();
		}
		return version;
	}
	
	private String getAppDescription(Model model) {
		String description = model.getDescription();
		return description;
	}
	
	private String getFrameworkSub(Model model) {
		String springCloudAlibabaDependency = "spring-cloud-starter-alibaba-nacos-discovery";
		String springCloudDependency = "support-springcloud3-starter";
		String springBootDependency = "support-springboot3-starter";
		String springMvcDependency = "core-web3";
		boolean springCloudAlibaba = false;
		boolean spirngCloud = false;
		boolean springBoot = false;
		boolean springMvc = false;
		
		for (Dependency dependency : model.getDependencies()) {
			if(dependency.getArtifactId().equals(springCloudAlibabaDependency)) {
				springCloudAlibaba = true;
			}else if(dependency.getArtifactId().equals(springCloudDependency)) {
				spirngCloud = true;
			}else if(dependency.getArtifactId().equals(springBootDependency)) {
				springBoot = true;
			}else if(dependency.getArtifactId().equals(springMvcDependency)) {
				springMvc = true;
			}
		}
		
		if(springCloudAlibaba) {
			return ArchConstants.FrameworkSub.SPRING_CLOUD_ALIBABA;
		}else if(spirngCloud) {
			return ArchConstants.FrameworkSub.SPRING_CLOUD;
		}else if(springBoot) {
			return ArchConstants.FrameworkSub.SPRING_BOOT;
		}else if(springMvc) {
			return ArchConstants.FrameworkSub.SPRING_MVC;
		}else {
			return ArchConstants.FrameworkSub.UNKNOWN;
		}
		
	}
	
	private String getPrincipal(Model model) {
		List<Developer> developerList = model.getDevelopers();
		if(developerList==null || developerList.size()==0) {
			throw new StandardRuntimeException(this.logPrefix + "请在pom中添加developers信息");
		}
		Developer developer = developerList.get(0);
		String developerName = developer.getName();
		if(StringUtils.isBlank(developerName)) {
			throw new StandardRuntimeException(this.logPrefix + "请在pom中添加developer的name信息");
		}
		return developerName;
	}
	
	private String getPrincipalEmail(Model model) {
		List<Developer> developerList = model.getDevelopers();
		if(developerList==null || developerList.size()==0) {
			throw new StandardRuntimeException(this.logPrefix + "请在pom中添加developers信息");
		}
		Developer developer = developerList.get(0);
		String developerEmail = developer.getEmail();
		if(StringUtils.isBlank(developerEmail)) {
			throw new StandardRuntimeException(this.logPrefix + "请在pom中添加developer的email信息");
		}
		return developerEmail;
	}
	
	private LocalDateTime getAppStartTime() {
		ApplicationStartup applicationStartup = configurableApplicationContext.getApplicationStartup();
		if(applicationStartup instanceof BufferingApplicationStartup) {
			BufferingApplicationStartup bufferingApplicationStartup = (BufferingApplicationStartup) applicationStartup;
			Instant instant = bufferingApplicationStartup.getBufferedTimeline().getStartTime();
			LocalDateTime localDateTime = LocalDateTimeUtil.of(instant);
			return localDateTime;
		}
		
		this.coreLogger.warn(this.logPrefix+"如需获取准确的系统启动时间，请采用BufferingApplicationStartup");
		return LocalDateTime.now();
	}
	
	@EventListener
	public void record(ApplicationReadyEvent applicationReadyEvent) {
		ArchInfoGenerator archInfoGenerator = archInfoGenerator();
		archInfoGenerator.setAppRunningTime(LocalDateTime.now());
	}
}
