package org.cat.support.springboot3.starter.web.security;

import java.util.Map;

import org.cat.support.security3.generator.password.constants.PasswordConstants;
import org.cat.support.security3.generator.spring.constants.SecurityLoginConstants;
import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityExceptionProperties {
	private boolean enabled = true;
	
	private String way = SecurityLoginConstants.Way.USERNAME_AND_PASSWORD;
	private String requestWay = SecurityLoginConstants.RequestWay.FORM;
	private String url = "/login";
	private String httpMethod = HttpMethod.POST.name(); 
	
	//USERNAME_AND_PASSWORD start
	private String usernameParameter = "userName";
	private String passwordParameter = "password";
	private String passwordEncryption = PasswordConstants.Encryption.BCRYPT;
	//USERNAME_AND_PASSWORD end
	
	private String otherParameters = null; //<parameterName1>、<parameterName2>、...
	private boolean hideUserNotFoundExceptions = true; //是否隐藏登录失败抛出的异常细节，安全问题
	
	private Success success;
	private Fail fail;
	
	@Getter
	@Setter
	private class Success {
		private String defaultTargetUrl = "/"; //登录成功后的跳转url
		private boolean alwaysUseDefaultTargetUrl = false; //登录成功后是否总是跳转到defaultTargetUrl
		private String targetUrlParameter = "targetUrl"; //如果需要通过参数来控制跳转的页面，则会从该参数获取登录成功后的跳转url
		private boolean useReferer = false; //如果alwaysUseDefaultTargetUrl=false && <targetUrlParameter>对应的value没有值，则尝试从referer中获取要跳转的页面
		private Map<String, String> roleTargetUrl;//key为roleName，value为跳转的url
	}
	
	@Getter
	@Setter
	private class Fail {
		private String defaultTargetUrl = "/"; //登录失败后的跳转url
	}

}
