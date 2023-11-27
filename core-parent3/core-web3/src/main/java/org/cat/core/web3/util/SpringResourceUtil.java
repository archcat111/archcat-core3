package org.cat.core.web3.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.cat.core.web3.exception.SpringResourceException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月20日 下午4:11:15
 * @version 1.0
 * @description 用于处理org.springframework.core.io.Resource的工具
 *
 */
public class SpringResourceUtil {

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午12:33:24
	 * @version 1.0
	 * @description 从指定的文件描述路径符获取Resource[] 
	 * @param locations 指定的文件描述路径符
	 * 			例如：classpath*:com/baidu/fsg/uid/worker/xml/*.xml（读取jar中的文件需要使用classpath*）
	 * @return Resource[]
	 */
//	public static Resource[] resolveLocationsToResource(String[] locations) {
//		ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
//		List<Resource> resources = new ArrayList<Resource>();
//		
//		if (locations != null) {
//			for (String location : locations) {
//				try {
//					Resource[] mappers = resourceResolver.getResources(location);
//					resources.addAll(Arrays.asList(mappers));
//				} catch (IOException e) {
//					throw new SpringResourceException("在获取指定路径下配置文件时出错", e);
//				}
//			}
//		}
//		return resources.toArray(new Resource[resources.size()]);
//	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月27日 下午4:12:53
	 * @version 1.0
	 * @description 从指定的多个文件描述路径符获取Resource[]，并将这些Resource[]合并到一个Resource[]中返回
	 * @param locations 指定的多个文件描述路径符
	 * 		例如：classpath*:com/baidu/fsg/uid/worker/xml/*.xml（读取jar中的文件需要使用classpath*）
	 * @param ignoreEmpty 是否忽略路径不存在的异常
	 * @return Resource[]
	 */
	public static Resource[] resolveLocationsToResources(String[] locations, boolean ignoreEmpty) {
		Resource[] resourcesArr = Arrays.stream(locations)
				.map(location -> resolveLocationToResources(location, ignoreEmpty))
				.flatMap(resourceArr -> Arrays.stream(resourceArr))
				.toArray(Resource[]::new);
		
		return resourcesArr;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月27日 下午4:40:44
	 * @version 1.0
	 * @description 从指定的一个文件描述路径符获取Resource[]，并返回
	 * @param location 指定的多个文件描述路径符
	 * 		例如：classpath*:com/baidu/fsg/uid/worker/xml/*.xml（读取jar中的文件需要使用classpath*）
	 * @param ignoreEmpty 是否忽略路径不存在的异常
	 * @return Resource[]
	 */
	public static Resource[] resolveLocationToResources(String location, boolean ignoreEmpty) {
		Resource[] resources = null;
		
		ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
		try {
			resources = resourceResolver.getResources(location);
		} catch (FileNotFoundException e) {
			if(ignoreEmpty) {
				return new Resource[]{};
			}else {
				throw new SpringResourceException("在获取指定路径下配置文件时出错", e);
			}
		} catch (IOException e) {
			throw new SpringResourceException("在获取指定路径下配置文件时出错", e);
		}
		
		return resources;
	}
}
