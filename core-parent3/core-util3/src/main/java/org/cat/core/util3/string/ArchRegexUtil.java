package org.cat.core.util3.string;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import cn.hutool.core.util.ReUtil;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月13日 上午1:39:14
 * @version 1.0
 * @description 正则表达式工具
 *
 */
public class ArchRegexUtil {
	/**
	 * email
	 */
	private static final String REGEX_EMAIL="\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
	/**
	 * 手机号
	 */
	private static final String REGEX_DOMESTIC_MOBILE="0?(13|14|15|17|18)[0-9]{9}";
	
	/**
	 * 短信验证码
	 */
	private static final String REGEX_SMS_CODE="[0-9]{4,6}";
	
	/**
	 * 图形验证码
	 */
	private static final String REGEX_VALIDATE_IMAGE_CODE="[0-9a-zA-Z]{4,8}";
	/**
	 * 密码（6到30位，字母数字字符至少两种的组合）
	 */
//	private static final  String REGEX_PASSWORD="(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_#@]+$).{6,30}";
	private static final  String REGEX_PASSWORD="[^\\s]{6,20}";
	
	/**
	 * 用于存储数据的KEY
	 */
	private static final  String REGEX_DB_KEY="[0-9a-zA-Z\\-]{1,50}";
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年1月19日 下午3:08:29
	 * @version 1.0
	 * @description 公用的使用正则表达式校验的方法
	 *
	 * @param regex
	 * @param str
	 * @return
	 */
	public static boolean regex(String regex ,String str){
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(str);
		return matcher.matches();
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2018年1月29日 下午4:23:11
	 * @version 1.0
	 * @description 获取字符串中符合正则表达式中的第N个子字符串
	 *
	 * @param str 原始字符串
	 * @param regex 正则表达式
	 * @param index 符合正则的第几个部分
	 * @return 最终匹配出来的子字符串
	 */
	public static String getRegexPartStr(String str,String regex,int index){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		for (int i = 0; i < index+1; i++) {
			if(matcher.find() && i==index){
				return matcher.group(0);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年5月10日 下午1:56:46
	 * @version 1.0
	 * @description 获取字符串中符合正则表达式的第一个子字符串
	 *
	 * @param str 原始字符串
	 * @param regex 正则表达式
	 * @return 最终匹配出来的子字符串
	 */
	public static String getRegexFirstPartStr(String str,String regex){
		return getRegexPartStr(str, regex, 0);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2018年1月29日 下午4:24:34
	 * @version 1.0
	 * @description 获取字符串中符合正则表达式的子字符串的数量
	 *
	 * @param str 原始字符串
	 * @param regex 正则表达式
	 * @return 最终匹配出来的子字符串的个数
	 */
	public static int getRegexPartCount(String str,String regex){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		int result = 0;
		while (matcher.find()) {
			result++;
		}
		return result;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月21日 上午10:38:00
	 * @version 1.0
	 * @description 获取字符串中符合正则表达式的所有子字符串 
	 * @param str 原始字符串
	 * @param regex 正则表达式
	 * @return 最终匹配出来的子字符串的List
	 */
	public static List<String> getRegexParts(String str,String regex){
		List<String> result = ReUtil.findAll(regex, str, 0);
		return result;
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年1月19日 下午3:09:05
	 * @version 1.0
	 * @description 判断是否是email
	 *
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		if(StringUtils.isBlank(email)){
			return false;
		}
		return regex(REGEX_EMAIL, email);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年1月19日 下午3:11:05
	 * @version 1.0
	 * @description 判断是否是国内电话号码
	 *
	 * @param mobile
	 * @return
	 */
	public static boolean isDomesticMobile(String mobile){
		if(StringUtils.isBlank(mobile)){
			return false;
		}
		return regex(REGEX_DOMESTIC_MOBILE, mobile);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年2月16日 下午2:52:09
	 * @version 1.0
	 * @description 判断是否是短信验证码
	 *
	 * @param smsCode
	 * @return
	 */
	public static boolean isSmsCode(String smsCode){
		if(StringUtils.isBlank(smsCode)){
			return false;
		}
		return regex(REGEX_SMS_CODE, smsCode);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年2月16日 下午3:33:53
	 * @version 1.0
	 * @description 判断是否是图形验证码
	 *
	 * @param imageCode
	 * @return
	 */
	public static boolean isValidateImageCode(String imageCode){
		if(StringUtils.isBlank(imageCode)){
			return false;
		}
		return regex(REGEX_VALIDATE_IMAGE_CODE, imageCode);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年2月16日 下午3:20:08
	 * @version 1.0
	 * @description 判断是否是符合要求的密码
	 *
	 * @param password
	 * @return
	 */
	public static boolean isPassword(String password){
		if(StringUtils.isBlank(password)){
			return false;
		}
		return regex(REGEX_PASSWORD, password);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年2月16日 下午3:36:51
	 * @version 1.0
	 * @description 判断是否是存储redis等db的key
	 *
	 * @param dbKey
	 * @return
	 */
	public static boolean isDBKey(String dbKey){
		if(StringUtils.isBlank(dbKey)){
			return false;
		}
		return regex(REGEX_DB_KEY, dbKey);
	}
	
}
