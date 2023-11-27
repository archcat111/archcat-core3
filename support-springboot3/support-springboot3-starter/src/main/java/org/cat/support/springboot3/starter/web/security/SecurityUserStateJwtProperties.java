package org.cat.support.springboot3.starter.web.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUserStateJwtProperties {
	private String issuer = "default"; //jwt签发者
	private String subject = "default-platform"; //jwt所面向的用户，可以标注是面向什么平台
//	private String audience = "default"; //接收jwt的一方，一般放userCode之类的值
	private long durationTimeSeconds = 60; //过期时间1小时
	private long refreshWindowSeconds = 5; //刷新窗口5分钟
	private String headerParamterName = "Authorization";
	
	private String jwtVerifyRedirectUrl = "/";
	private String jwtExpiredRedirectUrl = "/";
}
