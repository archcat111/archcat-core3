package org.cat.support.springcloud3.starter.gateway;

import java.util.List;

import org.cat.support.springcloud3.gateway.constants.ArchGatewayConstants.LoginConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.gateway.login-verify")
@Getter
@Setter
public class ArchGatewayLoginVerifyProperties {
	
	private boolean enabled;
	
	private String storage = LoginConstants.LOGIN_STATUS_STORAGE_SESSION;
	private String statusAttrName = "loginStatus";
	private int filterOrder = 5000;
	private List<String> excludeRouteIds = Lists.newArrayList();
	private List<String> excludeUris = Lists.newArrayList();
}
