package org.cat.support.web3.generator.user;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.util3.string.ArchCharsets2;
import org.cat.core.web3.constants.SessionConstants;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.core.web3.util.SpringReqAndRespUtil;
import org.cat.support.web3.generator.contants.UserConstants;
import org.cat.support.web3.generator.user.exception.ArchUserGeneratorException;
import org.cat.support.web3.generator.user.http.AbsUserGeneratorForHttp;
import org.cat.support.web3.generator.user.http.ExpressionHttpConverter;
import org.cat.support.web3.generator.user.http.ParamHttpParser;
import org.slf4j.Logger;

import cn.hutool.core.net.URLDecoder;

/**
 * 
 * @author 苏禾
 * @date 2022年4月20日 下午4:09:48
 * @version 1.0
 * @description 基于Http协议获取用户信息
 * 		假设我们在cat.support3.user.http下配置了一个map
 * ....http:
 * 		userCode: header(userCode,user-code),cookie(userCode),requestParam(user-code)
 * 		userName: header(userName,user-name),cookie(userName,user-name),requestParam(userName,user-name)
 * 		...
 * 		这时，本实例中的holder为：
 * 			userCode：List<ParamHttpParser>
 * 			userName：List<ParamHttpParser>
 * 		这里每个key都会有一个List<ParamHttpParser>负责解析
 * 		例如：userCode就会有一个List<ParamHttpParser>，实现分别是：
 * 			ParamHttpHeaderParser：本实现中的paramNameList为：[userCode,user-code]
 * 			ParamHttpCookieParser：本实现中的paramNameList为：[userCode]
 * 			ParamHttpRequestParamParser：本实现中的paramNameList为：[user-code]
 * 		并且用户在调用userGenerator获取参数值的时候，也会根据该属性进行获取，只要找到匹配的属性值则不再会往后遍历
 * 	
 *
 */
public class UserGeneratorForHttp extends AbsUserGeneratorForHttp {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "UserGeneratorForNoHttp：";
	
	private boolean hasSpringSession = false;
	private String sessionAttrName = SessionConstants.ATTR_NAME_DEFAULT;
	
	//解析配置文件的正则规则
	private final String regex = "(header|cookie|session|requestParam)\\([a-zA-Z\\-\\,]+\\)[\\,]*";
	
	public void setHasSpringSession(boolean hasSpringSession) {
		this.hasSpringSession = hasSpringSession;
	}

	public void setSessionAttrName(String sessionAttrName) {
		this.sessionAttrName = sessionAttrName;
	}

	public void setParamExpression(Map<String, String> paramExpressionMap) {
		ExpressionHttpConverter expressionHttpConverter = new ExpressionHttpConverter();
		//expression：每一个参数的参数名称以及解析规则
		//例如：userCode的expression为：
		//		header(userCode,user-code),cookie(userCode,user-code),requestParam(userCode,user-code)
		paramExpressionMap.forEach((paramName, expression) -> {
			List<ParamHttpParser> paramHttpParserList = expressionHttpConverter.convertHttpToParser(expression, this.regex);
			super.holder.put(paramName, paramHttpParserList);
		});
	}
	
	@Override
	public String getUserCode() {
		String userCode = get(UserConstants.ParamName.USER_CODE);
		return userCode;
	}
	
	@Override
	public Long getUserCodeForLong() {
		String userCode = this.getUserCode();
		if(userCode==null) {
			return null;
		}
		if(StringUtils.isNumeric(userCode)) {
			return Long.parseLong(userCode);
		}
		throw new ArchUserGeneratorException("通过UserGenerator获取Long类型的userCode，userCode为"+userCode+"无法转换为Long类型");
	}

	@Override
	public String getUserName() {
		String userName = get(UserConstants.ParamName.USER_NAME);
		if(StringUtils.isNotBlank(userName)) {
			userName = URLDecoder.decode(userName, ArchCharsets2.UTF_8);
		}
		return userName;
	}
	
	@Override
	public String getNickName() {
		String nickName = get(UserConstants.ParamName.NICK_NAME);
		if(StringUtils.isNotBlank(nickName)) {
			nickName = URLDecoder.decode(nickName, ArchCharsets2.UTF_8);
		}
		return nickName;
	}
	
	@Override
	public String getDevice() {
		String device = get(UserConstants.ParamName.DEVICE);
		if(StringUtils.isNotBlank(device)) {
			device = URLDecoder.decode(device, ArchCharsets2.UTF_8);
		}
		return device;
	}
	
	@Override
	public String getUserParam(String paramName) {
		String paramValue = get(paramName);
		if(StringUtils.isNotBlank(paramValue)) {
			paramValue = URLDecoder.decode(paramValue, ArchCharsets2.UTF_8);
		}
		return paramValue;
	}

	@Override
	public String getSessionId() {
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		if(httpServletRequest==null) {
			return null;
		}
		if(hasSpringSession) {//如果引入了SpringSession则可以直接从request中获取session
			HttpSession httpSession = httpServletRequest.getSession(false);
			if(httpSession==null) {
				return null;
			}
			String sessionId = httpSession.getId();
			return sessionId;
		}else { //否则，从header中获取session
			String sessionId = httpServletRequest.getHeader(sessionAttrName);
			return sessionId;
		}
	}

}
