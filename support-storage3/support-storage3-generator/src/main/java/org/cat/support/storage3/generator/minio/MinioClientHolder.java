package org.cat.support.storage3.generator.minio;

import java.util.Map;

import org.cat.core.util3.map.SourceHolder;

import com.google.common.collect.Maps;

import io.minio.MinioClient;

/**
 * 
 * @author 王云龙
 * @date 2022年7月28日 下午5:59:39
 * @version 1.0
 * @description MinioClient的Holder
 * 		持有MinioClient类型的MinioClient，保存在父类的sourceMapper
 *
 */
public class MinioClientHolder extends SourceHolder<MinioClient> {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年7月28日 下午5:54:34
	 * @version 1.0
	 * @description 返回value为本类顶层泛型Class的Map  
	 * @return
	 */
	public Map<String, MinioClient> getMinioClientMapper(){
		Map<String, MinioClient> minioClientMap = Maps.newHashMap();
		this.getSelfSourceMapper().forEach((name, minioClient) -> {
			minioClientMap.put(name, minioClient);
		});
		return minioClientMap;
	}
	
	
}
