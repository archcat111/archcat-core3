package org.cat.support.springboot3.starter.study;

public class SpringConfig {
	
	public void 配置加载顺序及扩展点() {
		//加载本地配置
		/**
		 * Spring会先将本地配置加载到Environment中，总共7个左右的PropertySource，有顺序
		 * 实际读取配置也是按照Environment对象中的dPropertySource的顺序进行读取，先匹配到哪个是哪个
		 * 扩展点：
		 * public class ActuatorEnvironmentPostProcessor implements EnvironmentPostProcessor
		 * 		可以通过Order或者Order注解来控制顺序
		 * 		并且，需要在spring.factories中使用#org.springframework.boot.env.EnvironmentPostProcessor=
		 * 注意：调用这个类的时机为：加载完本地配置，但没有加载远程配置
		 */
		//加载远程配置
		/**
		 * SpringApplication.run()
		 * --->prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
		 * --->applyInitializers(context)
		 * 该方法中循环调用List<ApplicationContextInitializer<?>> initializers
		 * 		即所有实现ApplicationContextInitializer接口的Bean，总共有十几个实现吧
		 * 其中的PropertySourceBootstrapConfiguration就是SpringCloud处理配置的类
		 * 		顺序为：Ordered.HIGHEST_PRECEDENCE + 10
		 * 	PropertySourceBootstrapConfiguration会循环调用List<PropertySourceLocator> propertySourceLocators处理配置
		 * 		一般情况下只有一个NacosPropertySourceLocator，用来从远程来获取配置
		 * 		注意：PropertySourceBootstrapConfiguration只有在处理完所有的PropertySourceLocator，
		 * 			才会汇总配置统一set到Environment
		 * 扩展点：public class ActuatorCloudPropertySourceLocator implements PropertySourceLocator
		 * 		注意：实现PropertySourceLocator接口的类只有使用AutoConfig的@Bean初始化为Bean才有用
		 * 			并且：spring.factories中需要使用org.springframework.cloud.bootstrap.BootstrapConfiguration=
		 * 			而不是SpringBoot的org.springframework.boot.autoconfigure.EnableAutoConfiguration=
		 */
		/**
		 * SpringApplication.propertySourceLocators：
		 * 	要启动两次：
		 * 		第一次：
		 * 		SharedMetadataReaderFactoryContextInitializer：0
		 * 		DelegatingApplicationContextInitializer：0
		 * 		ContextIdApplicationContextInitializer：Ordered.LOWEST_PRECEDENCE - 10
		 * 		ConditionEvaluationReportLoggingListener
		 * 		ConfigurationWarningsApplicationContextInitializer
		 * 		RSocketPortInfoApplicationContextInitializer
		 * 		ServerPortInfoApplicationContextInitializer
		 * 		第二次：
		 * 		BootstrapApplicationListener$AncestorInitializer：Ordered.HIGHEST_PRECEDENCE + 5
		 * 		？？DelegatingEnvironmentDecryptApplicationInitializer：Ordered.HIGHEST_PRECEDENCE + 9
		 * 		重！PropertySourceBootstrapConfiguration：Ordered.HIGHEST_PRECEDENCE + 10
		 * 		EnvironmentDecryptApplicationInitializer：Ordered.HIGHEST_PRECEDENCE + 15
		 * 		？？DelegatingEnvironmentDecryptApplicationInitializer：Ordered.HIGHEST_PRECEDENCE + 9
		 * 		SharedMetadataReaderFactoryContextInitializer：0
		 * 		DelegatingApplicationContextInitializer：0
		 * 		ContextIdApplicationContextInitializer：Ordered.LOWEST_PRECEDENCE - 10
		 * 		ConditionEvaluationReportLoggingListener
		 * 		ConfigurationWarningsApplicationContextInitializer
		 * 		RSocketPortInfoApplicationContextInitializer
		 * 		ServerPortInfoApplicationContextInitializer
		 */
		/**
		 * ApplicationContextInitializer知识点：
		 * 有如下几种方式初始化该类：
		 * 		1：META-INF/spring.factories
		 * 			org.springframework.context.ApplicationContextInitializer=
		 * 			这个加载过程是在SpringApplication中的getSpringFactoriesInstances()方法中直接加载并实例后执行对应的initialize方法
		 * 		2：META-INF/spring.factories
		 * 			org.springframework.cloud.bootstrap.BootstrapConfiguration
		 * 			前提是该实现类需要使用@Bean或者@Configuration(proxyBeanMethods = false)初始化为一个Bean
		 * 		3：application.properties
		 * 			context.initializer.classes=
		 * 			通过DelegatingApplicationContextInitializer这个初始化类中的initialize方法获取到该配置并执行对应的initialize方法
		 * 		4：springApplication.addInitializers(new Demo01ApplicationContextInitializer())
		 */
	}
}
