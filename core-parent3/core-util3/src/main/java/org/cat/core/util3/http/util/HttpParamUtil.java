package org.cat.core.util3.http.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author 王云龙
 * @date 2021年7月14日 下午2:48:40
 * @version 1.0
 * @description HTTP URL 参数处理工具类
 *
 */
public class HttpParamUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午2:43:44
	 * @version 1.0
	 * @description 将List中的键值对转化为URL上的参数对字符串 
	 * @param list 待转化的参数键值对列表
	 * @return
	 */
	public static String transformToUrlParam(List<Entry<String, ?>> list) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			Entry<String, ?> entry = list.get(i);
			if(i==0) {
				stringBuffer.append(entry.getKey()+"="+entry.getValue());
			}else {
				stringBuffer.append("&"+entry.getKey()+"="+entry.getValue());
			}
		}
		return stringBuffer.toString();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午2:44:31
	 * @version 1.0
	 * @description 将Map中的键值对转化为URL上的参数对字符串  
	 * @param map 待转化的参数键值对
	 * @return
	 */
	public static String transformToUrlParam(Map<String, ?> map) {
		List<Entry<String, ?>> list =new ArrayList<Entry<String, ?>>(map.entrySet());
		return transformToUrlParam(list);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月14日 下午2:45:42
	 * @version 1.0
	 * @description 将Map中的参数键值对添加到URL后面，返回一个完整的URL
	 * @param url
	 * @param map
	 * @return
	 */
	public static String urlAddParam(String url,Map<String, ?> map) {
		List<Entry<String, ?>> list = new ArrayList<Entry<String, ?>>(map.entrySet());
		String param = transformToUrlParam(list);
		if(url.contains("?") || url.contains("&")) {
			url+="&";
		}else if(!url.contains("?")&&StringUtils.isNotBlank(param)) {
			url+="?";
		}
		url+=param;
		return url;
	}
}
