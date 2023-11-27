package org.cat.core.util3.map;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月17日 下午11:01:07
 * @version 1.0
 * @description 处理Map的工具类
 *
 */
public class MapUtil {
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月17日 下午11:02:02
	 * @version 1.0
	 * @param <T>
	 * @description 删除Map中对应key的项 
	 * 注意：返回的map和作为参数传入的map不是一个对象
	 * @param map 需要操作的map
	 * @param removeKeyArray 需要删除的key的数组
	 * @return 一个新的删除可指定key的HashMap
	 */
	public static <T> Map<String, T> removeEntities(Map<String, T> map, String[] removeKeyArray) {
		Map<String, T> newMap = Maps.newHashMap();
		if (removeKeyArray != null && removeKeyArray.length > 0) {
			for (Map.Entry<String, T> entry : map.entrySet()) {
				boolean isRecord=true;
				for (int i = 0; i < removeKeyArray.length; i++) {
					String removeKey = removeKeyArray[i];
					if (entry.getKey().equals(removeKey)) {
						isRecord=false;
						break;
					}
				}
				if(isRecord){
					newMap.put(entry.getKey(), entry.getValue());
				}
			}
		} else {
			newMap.putAll(map);
		}

		return newMap;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月19日 下午2:45:50
	 * @version 1.0
	 * @description 将Map中需要加密的value替换成执行的加密对象 
	 * @param <T> Map中Value的类型
	 * @param params 需要操作的map
	 * @param encryptKeyArray 需要加密的key的数组
	 * @param encryptReplaceStr 需要置换的加密后的对象
	 * @return 一个将指定value进行加密后的新Map
	 */
	public static <T> Map<String, T> encryptEntities(Map<String, T> params, String[] encryptKeyArray, T encryptReplaceObj) {
		Map<String, T> newMap = Maps.newHashMap();
		if (encryptKeyArray != null && encryptKeyArray.length > 0) {
			for (Map.Entry<String, T> entry : params.entrySet()) {
				boolean isReplace=false;
				for (int i = 0; i < encryptKeyArray.length; i++) {
					String encryptKey = encryptKeyArray[i];
					if (entry.getKey().equals(encryptKey)) {
						newMap.put(entry.getKey(), encryptReplaceObj);
						isReplace=true;
						break;
					}
				}
				if(!isReplace){
					newMap.put(entry.getKey(), entry.getValue());
				}
			}
		} else {
			newMap.putAll(params);
		}

		return newMap;
	}
}
