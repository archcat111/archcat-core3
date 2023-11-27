package org.cat.support.storage3.generator.redis;

import java.util.Map;

import org.cat.core.util3.map.SourceHolder;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.common.collect.Maps;

/**
 * 
 * @author 王云龙
 * @date 2022年9月23日 下午1:47:56
 * @version 1.0
 * @description JedisConnectionFactory的管理器
 *
 */
public class RedisTemplateHolder extends SourceHolder<RedisTemplate<Object, Object>> {

	public Map<String, RedisTemplate<Object, Object>> getRedisTemplateMapper(){
		Map<String, RedisTemplate<Object, Object>> redisTemplateMap = Maps.newHashMap();
		this.getSelfSourceMapper().forEach((name, redisTemplate) -> {
			redisTemplateMap.put(name, redisTemplate);
		});
		return redisTemplateMap;
	}
	
}
