package org.cat.core.util3.http;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.util3.json.ArchJsonUtil;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年7月21日 上午11:00:27
 * @version 1.0
 * @description 用于httpclient请求的response封装类
 *
 */
@Getter
@Setter
public class HttpResponse implements Serializable{

	private static final long serialVersionUID = 8140069301565768594L;
	
	
	private int status = HttpConstants.Status.NOT_WRITE;
	
	private String content = HttpConstants.Content.NOT_WRITE;
	
	private Class<?> exceptionClass = HttpConstants.ExceptionClass.NOT_WRITE;
	
	private String exceptionMsg = HttpConstants.ExceptionMsg.NOT_WRITE;
	
	private HttpResponse() {
		super();
	}
	
	private HttpResponse(Class<?> exceptionClass, String exceptionMsg) {
		super();
		this.exceptionClass = exceptionClass;
		this.exceptionMsg = exceptionMsg;
	}
	
	public static HttpResponse createNotWriteHttpResponse() {
		HttpResponse httpResponse = new HttpResponse();
		return httpResponse;
	}
	
	public static HttpResponse createExceptionResp(Class<?> exceptionClass, String exceptionMsg) {
		HttpResponse httpResponse = new HttpResponse(exceptionClass, exceptionMsg);
		return httpResponse;
	}
	
	private String toJson() {
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.put("status", Integer.toString(this.status));
		resultMap.put("content", this.content);
		if(exceptionClass!=null) {
			resultMap.put("exceptionClass", this.exceptionClass.getName());
		}
		if(StringUtils.isNotBlank(this.exceptionMsg)) {
			resultMap.put("exceptionMsg", this.exceptionMsg);
		}
		
		String result = ArchJsonUtil.toJson(resultMap);
		return result;
	}

	/**
	 * 返回json字符串，如果存在异常则会额外返回exceptionClass和exceptionMsg，否则只返回status和content
	 */
	@Override
	public String toString() {
		return this.toJson();
	}

}
