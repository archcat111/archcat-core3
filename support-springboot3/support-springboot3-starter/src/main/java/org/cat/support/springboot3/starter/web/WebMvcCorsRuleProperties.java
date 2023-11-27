package org.cat.support.springboot3.starter.web;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月30日 下午4:17:17
 * @version 1.0
 * @description 一个CORS请求的规则类，系统允许配置多个CORS请求规则
 * 
 * 简单请求：
 * 在CORS出现前，发送HTTP请求时在头信息中不能包含任何自定义字段，且 HTTP 头信息不超过以下几个字段：
 * Accept
 * Accept-Language
 * Content-Language
 * Last-Event-ID
 * Content-Type 只限于 [application/x-www-form-urlencoded 、multipart/form-data、text/plain ] 类型
 * 假设一个简单请求：
 * GET /test HTTP/1.1
 * Accept: *\*（斜杠需要反过来）
 * Accept-Encoding: gzip, deflate, sdch, br
 * Origin: http://www.examples.com
 * Host: www.examples.com
 * 对于简单请求，CORS的策略是请求时在请求头中增加一个Origin字段，服务器收到请求后，根据该字段判断是否允许该请求访问
 * 1：如果允许，则在 HTTP 头信息中添加 Access-Control-Allow-Origin 字段，并返回正确的结果
 * 2：如果不允许，则不在 HTTP 头信息中添加 Access-Control-Allow-Origin 字段
 * 除了上面提到的 Access-Control-Allow-Origin ，还有几个字段用于描述 CORS 返回结果 ：
 * 1：Access-Control-Allow-Credentials： 可选，用户是否可以发送、处理 cookie
 * 2：Access-Control-Expose-Headers：可选，可以让用户访问的字段，例如自定义一个token字段，如果不设置，则客户端无法访问该字段
 * 	有几个字段无论设置与否都可以拿到的
 * 			包括：Cache-Control、Content-Language、Content-Type、Expires、Last-Modified、Pragma
 * 
 * 
 * 非简单请求：
 * 对于非简单请求的跨源请求，浏览器会在真实请求发出前，增加一次OPTION请求，称为预检请求(preflight request)
 * 预检请求将真实请求的信息，包括请求方法、自定义头字段、源信息添加到 HTTP 头信息字段中，询问服务器是否允许这样的操作
 * 例如一个DELETE请求：
 * OPTIONS /test HTTP/1.1
 * Origin: http://www.examples.com
 * Access-Control-Request-Method: DELETE
 * Access-Control-Request-Headers: X-Custom-Header
 * Host: www.examples.com
 * 与 CORS 相关的字段有：
 * 1：请求使用的 HTTP 方法 Access-Control-Request-Method
 * 2：请求中包含的自定义头字段 Access-Control-Request-Headers
 * 服务器收到请求时，需要分别对 Origin、Access-Control-Request-Method、Access-Control-Request-Headers 进行验证
 * 验证通过后，会在返回 HTTP头信息中添加 ：
 * Access-Control-Allow-Origin: http://www.examples.com
 * Access-Control-Allow-Methods: GET, POST, PUT, DELETE	//真实请求允许的方法
 * Access-Control-Allow-Headers: X-Custom-Header		//服务器允许使用的header字段
 * Access-Control-Allow-Credentials: true	//是否允许用户发送、处理 cookie
 * Access-Control-Max-Age: 1728000	//预检请求的有效期，单位为秒。有效期内，不会重复发送预检请求
 * 
 * 
 * demo：
 * private String mapping="/**";
 * private String allowedOrigins="*";
 * private boolean allowCredentials=false;
 * private List<String> allowHeaders=Lists.newArrayList("custom-user");
 * private List<String> allowedMethods=Lists.newArrayList("GET","POST","PUT","DELETE","HEAD","PATCH","OPTIONS","TRACE");
 * private long maxAge=3600;
 */
@Getter
@Setter
public class WebMvcCorsRuleProperties {
	private boolean enabled=true;
	private String name;	//该次cors请求的名称
	private String mapping="/**"; //允许cors请求的路径，如：/**、/v1/test/*、/v1/users/**
	 /**
	  * 对于简单请求，CORS的策略是请求时在请求头中增加一个Origin字段，服务器收到请求后，根据该字段判断是否允许该请求访问
	  * 1：如果允许，则在 HTTP 头信息中添加 Access-Control-Allow-Origin 字段，并返回正确的结果
	  * 2：如果不允许，则不在 HTTP 头信息中添加 Access-Control-Allow-Origin 字段
	  */
	private List<String> allowedOrigins;	//配置允许的域名源请求的白名单
	/**
	 * 除了上面提到的 Access-Control-Allow-Origin ，还有几个字段用于描述 CORS 返回结果 ：
	 * 1：Access-Control-Allow-Credentials： 可选，用户是否可以发送、处理 cookie
	 * 配置该字段为true后，allowedOrigins不能配置*，或者使用OriginPatterns
	 */
	private Boolean allowCredentials;
	/**
	 * 除了上面提到的 Access-Control-Allow-Origin ，还有几个字段用于描述 CORS 返回结果 ：
	 * 2：Access-Control-Expose-Headers：可选，可以让用户拿到的字段
	 * 有几个字段无论设置与否都可以拿到的：
	 * 包括：Cache-Control、Content-Language、Content-Type、Expires、Last-Modified、Pragma
	 */
	private List<String> allowedHeaders;
	/**
	 * 允许的方法，如："GET","POST","PUT","DELETE","HEAD","PATCH","OPTIONS","TRACE"
	 */
	private List<String> allowedMethods; 
	/**
	 * 预检请求的有效期，单位为秒。有效期内，不会重复发送预检请求
	 * 默认值：By default this is set to 1800 seconds (30 minutes).
	 */
	private Long maxAge;
	
}
