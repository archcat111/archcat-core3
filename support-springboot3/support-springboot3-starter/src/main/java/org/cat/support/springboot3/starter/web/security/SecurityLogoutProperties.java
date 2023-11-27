package org.cat.support.springboot3.starter.web.security;

import java.util.Map;

import org.cat.support.security3.generator.spring.constants.SecurityLogoutConstants;
import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityLogoutProperties {
	private boolean enabled = true;
	
	private String requestWay = SecurityLogoutConstants.RequestWay.FORM;
	private String url = "/logout";
	private String httpMethod = HttpMethod.POST.name(); 
	
	private String otherParameters = null; //<parameterName1>、<parameterName2>、...
	
	private Success success;
	
	@Getter
	@Setter
	class Success {
		private String defaultTargetUrl = "/"; //登出成功后的跳转url
		private boolean alwaysUseDefaultTargetUrl = false; //登出成功后是否总是跳转到defaultTargetUrl
		private String targetUrlParameter = "targetUrl"; //如果需要通过参数来控制跳转的页面，则会从该参数获取登出成功后的跳转url
		private boolean useReferer = false; //如果alwaysUseDefaultTargetUrl=false && <targetUrlParameter>对应的value没有值，则尝试从referer中获取要跳转的页面
		private Map<String, String> roleTargetUrl;//key为roleName，value为跳转的url
	}
	
}
