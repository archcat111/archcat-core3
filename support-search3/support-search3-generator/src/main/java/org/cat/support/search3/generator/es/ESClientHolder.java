package org.cat.support.search3.generator.es;

import java.util.Map;

import org.cat.core.util3.map.SourceHolder;

import com.google.common.collect.Maps;

import co.elastic.clients.elasticsearch.ElasticsearchClient;

/**
 * 
 * @author 王云龙
 * @date 2022年11月29日 下午1:41:04
 * @version 1.0
 * @description ElasticsearchClient的Holder
 * 		持有ElasticsearchClient类型的ElasticsearchClient，保存在父类的sourceMapper
 *
 */
public class ESClientHolder extends SourceHolder<ElasticsearchClient> {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年11月29日 下午1:40:55
	 * @version 1.0
	 * @description 返回value为本类顶层泛型Class的Map   
	 * @return
	 */
	public Map<String, ElasticsearchClient> getMinioClientMapper(){
		Map<String, ElasticsearchClient> minioClientMap = Maps.newHashMap();
		this.getSelfSourceMapper().forEach((name, minioClient) -> {
			minioClientMap.put(name, minioClient);
		});
		return minioClientMap;
	}
	
	
}
