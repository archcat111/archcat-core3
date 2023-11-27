package org.cat.support.security3.generator.spring.study;

/**
 * 
 * @author 王云龙
 * @date 2021年11月3日 下午2:11:56
 * @version 1.0
 * @description CORS 全称是 Cross-Origin Resource Sharing，直译过来就是跨域资源共享
 * 要理解这个概念就需要知道域、资源和同源策略这三个概念
 * 域：指的是一个站点，由 protocal、host 和 port 三部分组成，其中 host 可以是域名，也可以是 ip ；port 如果没有指明，则是使用 protocal 的默认端口
 * 资源：是指一个 URL 对应的内容，可以是一张图片、一种字体、一段 HTML 代码、一份 JSON 数据等等任何形式的任何内容
 * 同源策略：指的是为了防止 XSS，浏览器、客户端应该仅请求与当前页面来自同一个域的资源，请求其他域的资源需要通过验证
 *
 * 待看：HandlerMappingIntrospector
 *
 *	CorsConfigurer 
 *		extends AbstractHttpConfigurer 
 *			extends SecurityConfigurerAdapter 
 *			implements SecurityConfigurer
 *	CorsConfigurer
 *		@Override configure(H http) ---> getCorsFilter(context)
 *		重！如果类变量this.configurationSource!=null ---> return new CorsFilter(this.configurationSource)
 *		重！如果context.containsBeanDefinition("corsFilter")!=null ---> return这个corsFilter的Bean
 *		重！如果context.containsBeanDefinition("corsConfigurationSource")!=null ---> new CorsFilter(configurationSource)
 *		重！如果HandlerMappingIntrospector的Class存在 ---> return MvcCorsFilter.getMvcCorsFilter(context)
 *			如果context.containsBeanDefinition("mvcHandlerMappingIntrospector")!=null ---> new CorsFilter(mappingIntrospector)
 *	HandlerMappingIntrospector：
 *		implements CorsConfigurationSource, ApplicationContextAware, InitializingBean
 */
class CORS {
	
	protected void demoSpringMvc() {
		/**
		 * 默认情况下@CrossOrigin允许：
		 * 		1：所有的域
		 * 		2：所有的headers
		 * 		3：将控制器方法映射到的所有HTTP方法
		 * 		4：maxAge设置为30分钟
		 * 
		 * 类级别支持某一特定的域：
		 * @CrossOrigin(origins = "http://domain2.com", maxAge = 3600)
		 * @RestController
		 * @RequestMapping("/account")
		 * public class AccountController {
		 * 
		 * 		@GetMapping("/{id}")
		 * 		public Account retrieve(@PathVariable Long id) {
		 * 			// ...
		 * 		}
		 * 
		 * 		@DeleteMapping("/{id}")
		 * 		public void remove(@PathVariable Long id) {
		 * 			// ...	
		 * 		}
		 * }
		 * 
		 * 类和方法上都使用：
		 * @CrossOrigin(maxAge = 3600)
		 * @RestController
		 * @RequestMapping("/account")
		 * public class AccountController {
		 * 
		 * 		@CrossOrigin("http://domain2.com")
		 * 		@GetMapping("/{id}")
		 * 		public Account retrieve(@PathVariable Long id) {
		 * 			// ...
		 * 		}
		 * 
		 * 		@DeleteMapping("/{id}")
		 * 		public void remove(@PathVariable Long id) {
		 * 			// ...	
		 * 		}
		 * }
		 * 
		 * Spring JAVA Configuration中 全局配置：
		 * @Configuration
		 * @EnableWebMvc
		 * public class WebConfig implements WebMvcConfigurer {
		 * 		@Override
		 * 		public void addCorsMappings(CorsRegistry registry) {
		 * 			registry.addMapping("/api/**")
		 * 				.allowedOrigins("http://domain2.com")
		 * 				.allowedMethods("PUT", "DELETE")
		 * 				.allowedHeaders("header1", "header2", "header3")
		 * 				.exposedHeaders("header1", "header2")
		 * 				.allowCredentials(true)
		 * 				.maxAge(3600);
		 * 			// Add more mappings...
		 * 		}
		 * }
		 * 
		 */
	}
	
	protected void demoSpringBoot() {
		/**
		 * @Configuration
		 * public class CORSConfiguration {
		 * 		@Bean
		 * 		public WebMvcConfigurer corsConfigurer() {
		 * 			return new WebMvcConfigurer() {
		 * 				@Override
		 * 				public void addCorsMappings(CorsRegistry registry) {
		 * 					//addMapping 跨域所能访问的路径
		 * 					//allowedOrigins：那些域可以访问，默认为任何域都可以访问
		 * 					registry.addMapping("/api/**").allowedOrigins("*");
		 * 				}
		 * 			}
		 * 		}
		 * }
		 */
	}
	
	protected void demoSpringSecurity() {
		/**
		 * @EnableWebSecurity
		 * public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		 * 		@Override
		 * 		protected void configure(HttpSecurity http) throws Exception {
		 * 			http
		 * 			// by default uses a Bean by the name of corsConfigurationSource
		 * 			//默认配置一个Bean Name为corsConfigurationSource
		 * 			.cors().and()
		 * 			...
		 * 		}
		 * 
		 * 		//配置那些域可以访问的我的资源
		 * 		@Bean
		 * 		CorsConfigurationSource corsConfigurationSource() {
		 * 			CorsConfiguration configuration = new CorsConfiguration();
		 * 			configuration.setAllowedOrigins(Arrays.asList("https://example.com"));
		 * 			configuration.setAllowedMethods(Arrays.asList("GET","POST"));
		 * 			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		 * 			source.registerCorsConfiguration("/**", configuration);
		 * 			return source;
		 * 		}
		 * }
		 */
	}
}
