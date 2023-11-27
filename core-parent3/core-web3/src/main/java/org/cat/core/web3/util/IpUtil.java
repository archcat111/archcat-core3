package org.cat.core.web3.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月13日 下午9:48:23
 * @version 1.0
 * @description 用于处理IP的Web工具类
 *
 */
public class IpUtil {
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月13日 下午9:48:36
	 * @version 1.0
	 * @description 通过HttpServletRequest对象获取客户端IP 
	 * 首先从Header中的x-forwarded-for里获取IP
	 * 如果没有找到IP或者为unknown，从Header中的Proxy-Client-IP里获取IP
	 * 如果还没找到IP或者为unknown，从Header中的WL-Proxy-Client-IP里获取IP
	 * 如果还没找到IP或者为unknown，尝试从httpServletRequest.getRemoteAddr()中获取IP
	 * 如果结果为null，则直接返回
	 * 如果结果不为null，则判断IP是否为IPv6的0:0:0:0:0:0:0:1，如果是，则返回127.0.0.1，否则返回获取到的IP
	 * 因为当用户请求到达该方法的时候可能已经经历过多个网络中间件，而一般中间件会将自己的IP追加存放，所以可能该方法会获取到多个IP
	 * 例如：222.140.93.122 , 55.34.156.92 , 192.168.10.2
	 * @param httpServletRequest HttpServletRequest对象
	 * @return 例如：222.140.93.122 , 55.34.156.92 , 192.168.10.2
	 */
	public static String getRequestClientIps(HttpServletRequest httpServletRequest){
		String client_ip = httpServletRequest.getHeader("x-forwarded-for");
		if (null == client_ip || client_ip.length() == 0 || "unknown".equalsIgnoreCase(client_ip)) {
			client_ip = httpServletRequest.getHeader("Proxy-Client-IP");
		}
		if (null == client_ip || client_ip.length() == 0 || "unknown".equalsIgnoreCase(client_ip)) {
			client_ip = httpServletRequest.getHeader("WL-Proxy-Client-IP");
		}
		if (null == client_ip || client_ip.length() == 0 || "unknown".equalsIgnoreCase(client_ip)) {
			client_ip = httpServletRequest.getRemoteAddr();
		}
		if(client_ip != null) {
			client_ip = client_ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : client_ip;
		}
		return client_ip;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月13日 下午9:54:51
	 * @version 1.0
	 * @description 获取客户端的远端IP
	 * 因为当用户请求到达该方法的时候可能已经经历过多个网络中间件，而一般中间件会将自己的IP追加存放，所以可能该方法会获取到多个IP
	 * 例如：222.140.93.122 , 55.34.156.92 , 192.168.10.2
	 * 所以一般第一个IP为客户端的公网IP，则该方法会尝试获取该IP，如果没有获取到则返回null
	 * @param httpServletRequest
	 * @return
	 */
	public static String getRequestClientRemoteIp(HttpServletRequest httpServletRequest){
		String ips=getRequestClientIps(httpServletRequest);
		if(StringUtils.isBlank(ips)){
			return null;
		}
		String[] ipArr=ips.split(",");
		
		if(ipArr!=null && ipArr.length>0){
			String remoteIp=ipArr[0];
			return remoteIp;
		}
		return null;
		
	}
}
