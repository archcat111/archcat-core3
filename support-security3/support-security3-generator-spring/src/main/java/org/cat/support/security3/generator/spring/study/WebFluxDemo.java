package org.cat.support.security3.generator.spring.study;

public class WebFluxDemo {
	protected void SpringCloudGateway() {
		/**
		 * SpringCloudGateway使用WebFlux开发
		 * WebFlux和WebMvc不是一套东西，互相不支持
		 * 
		 * WebFlux：spring-webflux.jar
		 * WebMvc：spring-web.jar
		 * 
		 * 当SpringSecurity启动时：
		 * 		运行mvc（需要同时满足如下）：
		 * 			名叫springSecurityFilterChain的Bean在Spring容器中不存在
		 * 			ClassPath中存在类EnableWebSecurity
		 * 			@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
		 * 				classPath中有org.springframework.web.context.support.GenericWebApplicationContext（spring-web.jar）
		 * 		运维WebFlux：
		 * 			添加@EnableWebFluxSecurity注解即可
		 * 
		 * 	重！SpringCloudGateway和SpringSecurity集成时：
		 * 		需要只启动WebFlux，不启动WebMvc
		 * 		但是因为各种原因，classPath中又有spring-web.jar
		 * 		因此：可以通过修改ConditionalOnWebApplication.Type的方式
		 * 			既可以，让SpringSecurity启动webFlux的那一套，而不启动WebMvc的那一套
		 * 			又可以，让SpringCloudGateway启动时，不至于因为有Mvc而冲突
		 * @SpringBootApplication
		 * public class Application {
		 * 		public static void main(String[] args) {
		 * 			//SpringApplication.run(Application.class, args);
		 * 			SpringApplication application = new SpringApplication(Application.class);
		 * 			application.setWebApplicationType(WebApplicationType.REACTIVE);
		 * 			application.run(args);
		 * 		}
		 * }
		 * 
		 */
	}
}
