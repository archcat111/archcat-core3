package org.cat.support.web3.generator.user.http;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
 * @author 王云龙
 * @date 2022年4月21日 上午11:43:27
 * @version 1.0
 * @description 基于Http Header的参数获取器
 *
 */
public class ParamHttpHeaderParser implements ParamHttpParser {

	//paramNameList的值也许会是例如：[code,userCode,user-code]
	private List<String> paramNameList;
	
	protected ParamHttpHeaderParser(List<String> paramNameList) {
		this.paramNameList = paramNameList;
	}

	@Override
	public String get() {
		ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes());
		if(servletRequestAttributes==null) {
			return null;
		}
		HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
		for (String paramName : paramNameList) {
			String paramValue = httpServletRequest.getHeader(paramName);
			if(StringUtils.isNotBlank(paramValue)) {
				return paramValue;
			}
		}
		return null;
	}

}
