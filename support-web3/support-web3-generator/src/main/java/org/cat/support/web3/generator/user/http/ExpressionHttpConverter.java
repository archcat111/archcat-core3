package org.cat.support.web3.generator.user.http;

import java.util.Arrays;
import java.util.List;

import org.cat.core.util3.string.ArchRegexUtil;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.support.web3.generator.contants.UserConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * 
 * @author 王云龙
 * @date 2022年4月21日 上午10:31:46
 * @version 1.0
 * @description User相关配置的转换器
 *
 */
public class ExpressionHttpConverter {
	
	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月21日 上午11:18:58
	 * @version 1.0
	 * @description 解析用户配置文件中的表达式，最终解析为多个子表达式，不符合正则表达式的规则将被抛弃
	 * @param expression header(code,userCode,user-code),cookie(code,userCode),session(userCode),requestParam(code,userCode) 
	 * @param regex (header|cookie|session|requestParam)\\([a-zA-Z\\-\\,]+\\)[\\,]*
	 */
	public List<String> convertHttpExpression(String expression, String regex) {
		//本方法解析并返回的结果为一个List，也许list会有如下4项：
		//header(code,userCode,user-code)
		//cookie(code,userCode)
		//session(userCode)
		//requestParam(code,userCode)
		List<String> regexStrTempList = ArchRegexUtil.getRegexParts(expression, regex);
		List<String> regexStrList = Lists.newArrayList(); 
		regexStrTempList.forEach(partStr -> {
			partStr = partStr.endsWith(",")?partStr.substring(0, partStr.length()-1):partStr;
			regexStrList.add(partStr);
		});
		return regexStrList;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月21日 上午11:46:17
	 * @version 1.0
	 * @description 解析用户配置文件中的每个部分的表达式为List<ParamHttpParser>
	 * 		具体获取属性值的顺序按照用户的配置进行
	 * @param expressionList header(code,userCode,user-code)，cookie(code,userCode)，...
	 * @return List<ParamHttpParser>
	 */
	public List<ParamHttpParser> convertHttpToParser(List<String> expressionList) {
		List<ParamHttpParser> result = Lists.newArrayList();
		
		//header(code,userCode,user-code)
		//cookie(code,userCode)
		//session(userCode)
		//requestParam(code,userCode)
		expressionList.forEach(expression -> {
			//header
			String HttpPart = expression.substring(0, expression.indexOf("("));
			//code,userCode,user-code
			String paramNameArr = expression.substring(expression.indexOf("(")+1, expression.lastIndexOf(")"));
			//paramNameList为：[code,userCode,user-code]
			List<String> paramNameList = Arrays.asList(paramNameArr.split(","));
			
			if(UserConstants.HttpParserName.HEADER.equalsIgnoreCase(HttpPart)) {
				ParamHttpHeaderParser paramHttpHeaderParser = new ParamHttpHeaderParser(paramNameList);
				result.add(paramHttpHeaderParser);
			}else if(UserConstants.HttpParserName.COOKIE.equalsIgnoreCase(HttpPart)) {
				ParamHttpCookieParser paramHttpCookieParser = new ParamHttpCookieParser(paramNameList);
				result.add(paramHttpCookieParser);
			}else if(UserConstants.HttpParserName.SESSION.equalsIgnoreCase(HttpPart)) {
				ParamHttpSessionParser paramHttpSessionParser = new ParamHttpSessionParser(paramNameList);
				result.add(paramHttpSessionParser);
			}else if(UserConstants.HttpParserName.REQUEST_PARAM.equalsIgnoreCase(HttpPart)) {
				ParamHttpRequestParamParser paramHttpRequestParamParser = new ParamHttpRequestParamParser(paramNameList);
				result.add(paramHttpRequestParamParser);
			}else {
				coreLogger.warn("ExpressionHttpConverter.convertHttpToParser：发现无法识别的HttpParserName["+HttpPart+"]");
			}
		});
		
		return result;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年8月1日 下午6:07:37
	 * @version 1.0
	 * @description 返回一个 List<ParamHttpParser>
	 * 		假设我们在cat.support3.user.http下配置了一个map
	 * ....http:
	 * 		userCode: header(userCode,user-code),cookie(userCode),requestParam(user-code)
	 * 		userName: header(userName,user-name),cookie(userName,user-name),requestParam(userName,user-name)
	 * 		...
	 * 		那么这里每个key都会有一个List<ParamHttpParser>负责解析
	 * 		例如：userCode就会有一个List<ParamHttpParser>，实现分别是：
	 * 			ParamHttpHeaderParser：本实现中的paramNameList为：[userCode,user-code]
	 * 			ParamHttpCookieParser：本实现中的paramNameList为：[userCode]
	 * 			ParamHttpRequestParamParser：本实现中的paramNameList为：[user-code]
	 * 		并且用户在调用userGenerator获取参数值的时候，也会根据该属性进行获取，只要找到匹配的属性值则不再会往后遍历
	 * 			
	 * @param expression
	 * @param regex
	 * @return
	 */
	public List<ParamHttpParser> convertHttpToParser(String expression, String regex) {
		//传入的expression例如：
		//header(userCode,user-code),cookie(userCode,user-code),requestParam(userCode,user-code)
		List<String> expressionList = convertHttpExpression(expression, regex);
		//expressionList为：[
		//		header(userCode,user-code)
		//		cookie(userCode,user-code)
		//		requestParam(userCode,user-code)
		//]
		List<ParamHttpParser> paramHttpParserList = convertHttpToParser(expressionList);
		return paramHttpParserList;
	}
}
