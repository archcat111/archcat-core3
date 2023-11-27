package org.cat.support.springboot3.starter.web.user;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.user.IUserGenerator;
import org.cat.core.web3.util.SpringClassUtil;
import org.cat.support.springboot3.starter.exception.ExceptionAutoConfiguration;
import org.cat.support.web3.generator.SupportWeb3ConditionalFlag;
import org.cat.support.web3.generator.contants.WebSupportConstants;
import org.cat.support.web3.generator.user.UserGeneratorForHttp;
import org.cat.support.web3.generator.user.UserGeneratorForNoUse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;

@Configuration
@AutoConfigureAfter(ExceptionAutoConfiguration.class)
@ConditionalOnClass(SupportWeb3ConditionalFlag.class)
@ConditionalOnProperty(prefix = "cat.support3.web.user", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebUserProperties.class)
public class WebUserAutoConfiguration {
	
	@Autowired
	private WebUserProperties webUserProperties;
	
	@ConditionalOnMissingBean(IUserGenerator.class)
	@Bean
	public IUserGenerator userGenerator() {
		String userGeneratorName = this.webUserProperties.getUserGeneratorName();
		if(StringUtils.isBlank(userGeneratorName)) {
			userGeneratorName = WebSupportConstants.UserGeneratorTransferProtocol.NO_USE;
		}
		
		IUserGenerator userGenerator = null;
		
		switch (userGeneratorName.trim().toLowerCase()) {
		case WebSupportConstants.UserGeneratorTransferProtocol.NO_USE:
			userGenerator = new UserGeneratorForNoUse();
			break;
		case WebSupportConstants.UserGeneratorTransferProtocol.HTTP:
			userGenerator = createUserGeneratorForHttp();
			break;
		default:
			userGenerator = new UserGeneratorForNoUse();
			break;
		}
		
		return userGenerator;
	}
	
	private IUserGenerator createUserGeneratorForHttp() {
		UserGeneratorForHttp userGeneratorForHttp = new UserGeneratorForHttp();
		
		//hasSpringSession
		boolean hasSpringSession = SpringClassUtil.isPresent("org.springframework.session.web.http.HttpSessionIdResolver", null);
		userGeneratorForHttp.setHasSpringSession(hasSpringSession);
		//sessionAttrName
		String sessionAttrName = webUserProperties.getSessionAttrName();
		if(StringUtils.isNotBlank(sessionAttrName)) {
			userGeneratorForHttp.setSessionAttrName(sessionAttrName);
		}
		
		//key：userCode
		//value：header(code,userCode,user-code),cookie(code,userCode),session(userCode),requestParam(code,userCode)
		Map<String, String> paramUserMap = webUserProperties.getHttp();
		
		Map<String, String> paramExpressionMap = Maps.newHashMap();
		paramUserMap.forEach((paranName, webUserParamExpression)->{
			paramExpressionMap.put(paranName, webUserParamExpression);
		});
		
		userGeneratorForHttp.setParamExpression(paramExpressionMap);
		return userGeneratorForHttp;
	}

}
