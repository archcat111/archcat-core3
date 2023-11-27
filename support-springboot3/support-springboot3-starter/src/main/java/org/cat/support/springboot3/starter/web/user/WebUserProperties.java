package org.cat.support.springboot3.starter.web.user;

import java.util.Map;

import org.cat.core.web3.constants.SessionConstants;
import org.cat.support.web3.generator.contants.WebSupportConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.web.user")
@Getter
@Setter
public class WebUserProperties {
	private boolean enabled;
	
	private String userGeneratorName = WebSupportConstants.UserGeneratorTransferProtocol.NO_USE;
	
	private String sessionAttrName = SessionConstants.ATTR_NAME_DEFAULT;
	
	//key：userCode
	//value：header(code,userCode,user-code),cookie(code,userCode),session(userCode),requestParam(code,userCode)
	private Map<String, String> http = Maps.newHashMap();
//	private Map<String, WebUserExpressionProperties> http = Maps.newHashMap();
	
}
