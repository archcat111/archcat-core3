package org.cat.core.util3.system;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * 
 * @author 王云龙
 * @date 2017年3月9日 下午12:32:58
 * @version 1.0
 * @description 本地网络相关的工具类
 *
 */
public class ArchLocalNetWorkArchUtil {

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 上午11:33:50
	 * @version 1.0
	 * @description 获取Liunx的hostName  
	 * @return
	 */
	public static String getHostNameForLiunx() {  
        try {  
            return (InetAddress.getLocalHost()).getHostName();  
        } catch (UnknownHostException e) {  
            String host = e.getMessage(); // host = "hostname: hostname"  
            if (host != null) {  
                int colon = host.indexOf(':');  
                if (colon > 0) {  
                    return host.substring(0, colon);  
                }  
            }  
            return "UnknownHost";  
        }  
    }  
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月18日 上午11:32:47
	 * @version 1.0
	 * @description 获取主机hostName，兼容windows和liunx  
	 * @return
	 */
    public static String getHostName() {  
    	String hostName = System.getenv("COMPUTERNAME") ;
        if (hostName != null) {  
            return hostName;  
        } else {  
            return getHostNameForLiunx();  
        }  
    } 
    
    /**
     * 
     * @author wangyunlong
     * @date 2021年8月18日 下午1:54:01
     * @version 1.0
     * @description 获取主机IP
     * 		注意：在Linux有可能会获取到127.0.0.1 
     * @return
     * @throws UnknownHostException
     */
    public static String getHostIpV1() {
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new ArchNetWorkUtilException("在获取本地IP地址时发生异常", e);
		}
		String hostIp = inetAddress.getHostAddress();
		return hostIp;
    }
    
    /**
     * 
     * @author wangyunlong
     * @date 2021年8月18日 下午2:03:26
     * @version 1.0
     * @description 获取主机IP
     * 		基于NetworkInterface类，获取本机所有的物理网络接口和虚拟机等软件利用本机的物理网络接口创建的逻辑网络接口的信息
     * 		从中去除回传接口、虚拟网卡、没有启用的物理网卡、IPv6
     * @return	返回本机第一个IPv4，如果没有则返回null
     * @throws SocketException
     */
    public static String getHostIpV2() {
    	try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while(allNetInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = allNetInterfaces.nextElement();
				//非回传接口、非虚拟网卡、正在使用中
				if(networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
					continue;
				}else {
					Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
					while(addresses.hasMoreElements()) {
						ip = addresses.nextElement();
						if(ip!=null && ip instanceof Inet4Address) {
							return ip.getHostAddress();
						}
					}
				}
			}
		} catch (SocketException e) {
			throw new ArchNetWorkUtilException("在获取本地IP地址时发生异常", e);
		}
		return null;
    }
  
}
