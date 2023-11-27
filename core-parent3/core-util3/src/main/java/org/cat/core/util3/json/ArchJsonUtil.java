package org.cat.core.util3.json;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.cat.core.util3.date.ArchDateTimeUtil.FormatConstants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.google.common.collect.Maps;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 
 * @author 王云龙
 * @date 2021年7月13日 下午2:18:40
 * @version 1.0
 * @description Json处理工具类
 *
 */
public class ArchJsonUtil {
	
	public static ObjectMapper objectMapper = new ObjectMapper();
	static {
//		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		//日期序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(FormatConstants.DATETIME_NORMAL)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(FormatConstants.DATE_NORMAL)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(FormatConstants.TIME_NORMAL)));

        //日期反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(FormatConstants.DATETIME_NORMAL)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(FormatConstants.DATE_NORMAL)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(FormatConstants.TIME_NORMAL)));
        
        objectMapper.registerModule(javaTimeModule);
	}
	
	public static ObjectMapper longtoStringObjectMapper = new ObjectMapper();
	static{
		/**
	     * 序列换成json时,将所有的long变成string
	     * 因为js中得数字类型不能包含所有的java long值
	     */
	    SimpleModule simpleModule = new SimpleModule();
	    simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
	    simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
	    longtoStringObjectMapper.registerModule(simpleModule);
	    
	    JavaTimeModule javaTimeModule = new JavaTimeModule();
		//日期序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        //日期反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        longtoStringObjectMapper.registerModule(javaTimeModule);
	}
	
	public static ObjectMapper allowUnknownPropObjectMapper = new ObjectMapper();
	static{
		allowUnknownPropObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	
		 JavaTimeModule javaTimeModule = new JavaTimeModule();
		//日期序列化
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

        //日期反序列化
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        longtoStringObjectMapper.registerModule(javaTimeModule);
	}
	
	
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午2:43:29
	 * @version 1.0
	 * @description 将JavaBean转换为Json字符串  
	 * @param <T>
	 * @param mapper 一个已经配置好各种参数的ObjectMapper
	 * @param t 待转换的JavaBean的泛化类型
	 * @return Json字符串
	 */
	private static <T> String toJson(ObjectMapper mapper, T t) {
		if(t==null) {
			return null;
		}
		String json = null;
		try {
			json = mapper.writeValueAsString(t);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new ArchJsonUtilException("在将对象t转换为json字符串时发生JsonProcessingException错误", e);
		}
		return json;
	}
	
	private static <T> byte[] toJsonBytes(ObjectMapper mapper, T t) {
		if(t==null) {
			return null;
		}
		byte[] json = null;
		try {
			json = mapper.writeValueAsBytes(t);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new ArchJsonUtilException("在将对象t转换为json字符串时发生JsonProcessingException错误", e);
		}
		return json;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午2:44:38
	 * @version 1.0
	 * @description 将JavaBean转换为Json字符串 
	 * 		底层调用{@linkplain org.cat.core.util3.json.ArchJsonUtil#toJson(ObjectMapper, Object)}
	 * @param <T> 
	 * @param t 待转换的JavaBean的泛化类型
	 * @return Json字符串
	 */
	public static <T> String toJson(T t) {
		String json = toJson(objectMapper, t);
		return json;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2018年7月28日 上午8:24:31
	 * @version 1.0
	 * @description bean转json的同时，将Long类型转为String 
	 * @param t
	 * @return
	 * @throws JsonProcessingException 
	 */
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午2:48:04
	 * @version 1.0
	 * @description 将JavaBean转换成Json字符串的时候，将Long类型转换成String 
	 * 		底层调用{@linkplain org.cat.core.util3.json.ArchJsonUtil#toJson(ObjectMapper, Object)}
	 * @param <T>
	 * @param t 待转换的JavaBean的泛化类型
	 * @return Json字符串
	 * @throws JsonProcessingException
	 */
	public static <T> String toJsonAndLongToString(T t) throws JsonProcessingException {
		if(t==null) {
			throw new IllegalArgumentException("传入的对象不能为null");
		}
		String json = toJson(longtoStringObjectMapper, t);
		return json;
	}
	
	public static <T> byte[] toJsonBytesAndLongToString(T t) throws JsonProcessingException {
		if(t==null) {
			throw new IllegalArgumentException("传入的对象不能为null");
		}
		byte[] json = toJsonBytes(longtoStringObjectMapper, t);
		return json;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 上午11:55:29
	 * @version 1.0
	 * @description 将json字符串的全部或者一部分(根据path)转换为泛型对象    
	 * @param <T> 需要转化成输出结果bean的class类型
	 * @param mapper	一个已经配置好各种参数的ObjectMapper
	 * @param json 需要解析的json字符串
	 * @param path 需要解析json中的某个路径，例如：/user/type or null
	 * @param keyClazz 输出对象的class类型 
	 * @param valuesClazz 对象类型的泛型（当不需要泛型的时候可以不传）
	 * @return
	 * 			如果json为null or "" or "   "则默认返回null
	 * 			如果path不为null or "" or "   "，并且该Path对应的json子串不存在，则返回null
	 * 			如果path不为null or "" or "   "，并且该Path对应的json子串存在，则将字串转化字串为bean
	 * 			如果path为null or "" or "   "，则将整个json转化字串为bean
	 */
	private static <T> T toObjectT(ObjectMapper mapper, String json, String path, Class<T> keyClazz, Class<?>... valuesClazz) {
		if(StringUtils.isBlank(json)) {
			return null;
		}
		
		String parseJson=json;
		if(StringUtils.isNotBlank(path)) {
			JsonNode jsonRoot;
			try {
				jsonRoot = mapper.readTree(json);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new ArchJsonUtilException("在将一个json字符串的一部分转换成一个对象时mapper.readTree(json)发生JsonProcessingException错误", e);
			}
			JsonNode jsonNode = jsonRoot.at(path);
			if (jsonNode.isMissingNode()) {
				return null;
			}
			parseJson = jsonNode.toString();
		}
		
		JavaType javaType = mapper.getTypeFactory().constructParametricType(keyClazz, valuesClazz);
		T t = null;
		try {
			t = mapper.readValue(parseJson, javaType);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new ArchJsonUtilException("在将一个json字符串的一部分转换成一个对象时mapper.readValue(parseJson, javaType)发生JsonProcessingException错误", e);
		}
		return t;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 上午11:33:36
	 * @version 1.0
	 * @description 将json字符串的全部或者一部分(根据path)转换为泛型对象   
	 * 		底层调用{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectT(ObjectMapper, String, String, Class, Class...)}
	 * @param <T> 需要转化成输出结果bean的class类型
	 * @param json 需要解析的json字符串
	 * @param path 需要解析json中的某个路径，例如：/user/type or null
	 * @param keyClazz 输出对象的class类型 
	 * @param valuesClazz 对象类型的泛型（当不需要泛型的时候可以不传）
	 * @return
	 * 			如果json为null or "" or "   "则默认返回null
	 * 			如果path不为null or "" or "   "，并且该Path对应的json子串不存在，则返回null
	 * 			如果path不为null or "" or "   "，并且该Path对应的json子串存在，则将字串转化字串为bean
	 * 			如果path为null or "" or "   "，则将整个json转化字串为bean
	 */
	private static <T> T toObjectT(String json, String path, Class<T> keyClazz, Class<?>... valuesClazz) {
		T t = toObjectT(objectMapper, json, path, keyClazz, valuesClazz);
		return t;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 上午11:33:36
	 * @version 1.0
	 * @description 将json字符串的全部或者一部分(根据path)转换为泛型对象(允许未知的属性出现)   
	 * 		底层调用{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectT(ObjectMapper, String, String, Class, Class...)}
	 * @param <T> 需要转化成输出结果bean的class类型
	 * @param json 需要解析的json字符串
	 * @param path 需要解析json中的某个路径，例如：/user/type or null
	 * @param keyClazz 输出对象的class类型 
	 * @param valuesClazz 对象类型的泛型（当不需要泛型的时候可以不传）
	 * @return
	 * 			如果json为null or "" or "   "则默认返回null
	 * 			如果path不为null or "" or "   "，并且该Path对应的json子串不存在，则返回null
	 * 			如果path不为null or "" or "   "，并且该Path对应的json子串存在，则将字串转化字串为bean
	 * 			如果path为null or "" or "   "，则将整个json转化字串为bean
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static <T> T toObjectTAllowUnKonwnProps(String json, String path, Class<T> keyClazz, Class<?>... valuesClazz) throws JsonMappingException, JsonProcessingException {
		T t = toObjectT(allowUnknownPropObjectMapper, json, path, keyClazz, valuesClazz);
		return t;
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月29日 上午10:30:45
	 * @version 1.0
	 * @description 根据path将json中的某个部分转化为bean
	 * 		底层调用{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectT(String, String, Class, Class...)}
	 * @param json 需要解析的json字符串
	 * @param path 需要解析json中的某个路径，例如：/user/type or null
	 * @param clazz 需要转换的class type
	 * @return 
	 * 			如果json为null or "" or "   "则默认返回null
	 * 			如果path为null or "" or "   "则默认转换整个json字符串；如果对应的path不存在，则返回null
	 * 			如果path不为null or "" or "   "，并且该Path对应的json子串存在，则将字串转化字串为bean
	 */
	public static <T> T toObject(String json, String path, Class<T> clazz) {
		T t = toObjectT(json, path, clazz);
		return t;
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午1:48:11
	 * @version 1.0
	 * @description 将json转化为bean  
	 * 		底层调用{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObject(String, String, Class)}
	 * @param <T>
	 * @param json 需要解析的json字符串
	 * @param clazz 需要转换的class type
	 * @return
	 * 			如果json为null or "" or "   "则默认返回null
	 */
	public static <T> T toObject(String json, Class<T> clazz) {
		return toObject(json, null, clazz);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 上午10:38:51
	 * @version 1.0
	 * @description 根据path将json中的某个部分转化为List<Bean> 
	 * 		{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectT(String, String, Class, Class...)}
	 * @param json 需要解析的json字符串
	 * @param path 需要解析json中的某个路径，例如：/user/type，如果为null，则默认转换这个json字符串
	 * @param clazz List中存储的对象类型
	 * @return
	 * 			如果json为null or "" or "   "则默认返回null
	 * 			如果path为null or "" or "   "则默认转换整个json字符串；如果对应的path不存在，则返回null
	 * 			如果path不为null or "" or "   "，并且该Path对应的json子串存在，则将字串转化字串为bean
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> toObjectList(String json, String path, Class<T> clazz) throws JsonMappingException, JsonProcessingException {
		List<T> tList = toObjectT(json, path, List.class, clazz);
		return tList;
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 上午10:37:51
	 * @version 1.0
	 * @description 将json字符串转为对象List  
	 * 		{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectList(String, String, Class)}
	 * @param <T> 需要转化的Class的类型
	 * @param json 需要解析的json字符串
	 * @param clazz List中存储的对象类型
	 * @return
	 * 			如果json为null or "" or "   "则默认返回null
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static <T> List<T> toObjectList(String json, Class<T> clazz) {
		List<T> tList = null;
		try {
			tList = toObjectList(json, null, clazz);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ArchJsonUtilException("在将一个json字符串成一个对象List时发生JsonProcessingException错误", e);
		}
		return tList;
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 上午10:38:59
	 * @version 1.0
	 * @description 将json字符串转换为Map 
	 * 		{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectT(String, String, Class, Class...)}
	 * @param <T>
	 * @param <V>
	 * @param json 需要转换的JSON字符串
	 * @param path 需要解析json中的某个路径，例如：/user/type；如果为null，则默认转换这个json字符串，此时建议使用{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectMap(String, Class, Class)}
	 * @param keyClazz Map中key的类型
	 * @param valueClazz Map中value的类型
	 * @return
	 * 			如果json为null or "" or "   "则默认返回null
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	@SuppressWarnings("unchecked")
	public static <T, V> Map<T,V> toObjectMap(String json, String path, Class<T> keyClazz, Class<V> valueClazz) throws JsonMappingException, JsonProcessingException {
		Map<T,V> map = toObjectT(json, path, Map.class, keyClazz, valueClazz);
		return map;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 上午10:38:59
	 * @version 1.0
	 * @description 将json字符串转换为Map 
	 * 		{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectMap(String, String, Class, Class)}
	 * @param <T>
	 * @param <V>
	 * @param json 需要转换的JSON字符串
	 * @param keyClazz Map中key的类型
	 * @param valueClazz Map中value的类型
	 * @return
	 * 			如果json为null or "" or "   "则默认返回null
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public static <T, V> Map<T,V> toObjectMap(String json, Class<T> keyClazz, Class<V> valueClazz) throws JsonMappingException, JsonProcessingException {
		Map<T,V> map = toObjectMap(json, null, keyClazz, valueClazz);
		return map;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 上午10:46:03
	 * @version 1.0
	 * @description 将json字符串转换为TreeMap（Json转化后的Map将是有序的）  
	 * 		{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectT(String, String, Class, Class...)}
	 * @param <T>
	 * @param <V>
	 * @param json 需要转换的JSON字符串
	 * @param keyClazz Map中key的类型
	 * @param valueClazz Map中value的类型
	 * @return
	 * 			如果json为null or "" or "   "则默认返回null
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("unchecked")
	public static <T, V> TreeMap<T,V> toObjectTreeMap(String json, Class<T> keyClazz, Class<V> valueClazz) throws JsonMappingException, JsonProcessingException {
		TreeMap<T,V> map = toObjectT(json, null, TreeMap.class, keyClazz, valueClazz);
		return map;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午1:51:44
	 * @version 1.0
	 * @description 向已有json字符串添加新节点  
	 * 		{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectMap(String, Class, Class)}
	 * 		{@linkplain org.cat.core.util3.json.ArchJsonUtil#toJson(Object)}
	 * @param json 原始的json字符串
	 * @param nodes 需要添加的到原始json字符串中的新节点
	 * @return
	 * @throws JsonProcessingException 
	 */
	public static String addNewNodeToJson(String json, Map<String, Object> nodes){
		Map<String, Object> jsonMap = null;
		String jsonResult = null;
		try {
			jsonMap = toObjectMap(json, String.class, Object.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new ArchJsonUtilException("addNewNodeToJson时发生JsonProcessingException错误", e);
		}
		jsonMap.putAll(nodes);
		jsonResult = toJson(jsonMap);
		return jsonResult;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年7月13日 下午2:05:35
	 * @version 1.0
	 * @description 将多个json字符串合并为1个json(重复的key只保留先进行合并的key) 
	 * 		{@linkplain org.cat.core.util3.json.ArchJsonUtil#toObjectMap(String, Class, Class)}
	 * 		{@linkplain org.cat.core.util3.json.ArchJsonUtil#toJson(Object)}
	 * @param jsons 待合并的多个json字符串
	 * @return 合并后的json字符串
	 */
	public static String mergeJsons(String...jsons) {
		if(jsons==null) {
			return null;
		}
		Map<String, Object> jsonMapResult = Maps.newHashMap();
		for (int i = 0; i < jsons.length; i++) {
			if(StringUtils.isNoneBlank(jsons[i])) {
				Map<String, Object> jsonMap = null;
				try {
					jsonMap = toObjectMap(jsons[i], String.class, Object.class);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					throw new ArchJsonUtilException("在将多个json字符串合并为1个json字符串时发生JsonProcessingException异常", e);
				}
				if(MapUtils.isNotEmpty(jsonMap)) {
					jsonMapResult.putAll(jsonMap);
				}
			}
		}
		String result = toJson(jsonMapResult);
		return result;
	}
	
	public static String getValueForStr(String json, String path) {
		if(StringUtils.isBlank(json)) {
			return null;
		}
		if(StringUtils.isBlank(path)) {
			return json;
		}
		
		JSONObject jsonObject = JSONUtil.parseObj(json);
		String result = jsonObject.getByPath(path, String.class);
		return result;
		
	}
	
}
