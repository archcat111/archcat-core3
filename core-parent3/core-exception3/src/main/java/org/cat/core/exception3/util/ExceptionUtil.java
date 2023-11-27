package org.cat.core.exception3.util;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.exception3.bean.ExceptionBean;
import org.cat.core.exception3.constants.ExceptionConstants;
import org.cat.core.util3.json.ArchJsonUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月13日 下午4:31:15
 * @version 1.0
 * @description 异常工具类
 *
 */
public class ExceptionUtil{
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午2:06:43
	 * @version 1.0
	 * @description 将异常字符串转换成ExceptionBean 
	 * @param exceptionBeanJson {@linkplain ExceptionBean}的json字符串
	 * @return {@linkplain ExceptionBean}
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static ExceptionBean toExceptionBean(String exceptionBeanJson) throws JsonMappingException, JsonProcessingException{
		ExceptionBean exceptionBean=ArchJsonUtil.toObject(exceptionBeanJson, ExceptionBean.class);
		return exceptionBean;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月13日 下午4:31:54
	 * @version 1.0
	 * @description 判断一个json字符串是否是系统标准异常字符串
	 * @param exceptionBeanJson json字符串
	 * @return
	 */
	public static boolean isStandardException(String exceptionBeanJson){
		if(StringUtils.isNoneBlank(exceptionBeanJson) 
				&& exceptionBeanJson.contains(ExceptionConstants.ParamName.EXCETPION_ID)
				&& exceptionBeanJson.contains(ExceptionConstants.ParamName.EXCEPTION_PROJECT_CODE)
				&& exceptionBeanJson.contains(ExceptionConstants.ParamName.EXCEPTION_CODE)
				&& exceptionBeanJson.contains(ExceptionConstants.ParamName.EXCEPTION_MSG)){
			return true;
		}else{
			return false;
		}
	}
	
}
