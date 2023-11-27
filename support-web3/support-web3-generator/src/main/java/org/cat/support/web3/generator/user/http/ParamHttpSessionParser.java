package org.cat.support.web3.generator.user.http;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
public class ParamHttpSessionParser implements ParamHttpParser {

	private List<String> paramNameList;
	
	protected ParamHttpSessionParser(List<String> paramNameList) {
		this.paramNameList = paramNameList;
	}

	@Override
	public String get() {
		ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes());
		if(servletRequestAttributes==null) {
			return null;
		}
		HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
		HttpSession httpSession = httpServletRequest.getSession();
		for (String paramName : paramNameList) {
			Object paramValueObject = httpSession.getAttribute(paramName);
			if(paramValueObject != null) {
				return paramValueObject.toString();
			}
		}
		return null;
	}

}
