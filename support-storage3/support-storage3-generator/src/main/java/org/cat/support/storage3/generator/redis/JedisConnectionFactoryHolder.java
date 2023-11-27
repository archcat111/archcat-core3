package org.cat.support.storage3.generator.redis;

import java.util.Map;

import org.cat.core.util3.map.SourceHolder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import com.google.common.collect.Maps;

/**
 * 
 * @author 王云龙
 * @date 2022年9月23日 下午1:47:56
 * @version 1.0
 * @description JedisConnectionFactory的管理器
 *
 */
public class JedisConnectionFactoryHolder extends SourceHolder<JedisConnectionFactory> {

	public Map<String, JedisConnectionFactory> getJedisConnectionFactoryMapper(){
		Map<String, JedisConnectionFactory> jedisConnectionFactoryMap = Maps.newHashMap();
		this.getSelfSourceMapper().forEach((name, jedisConnectionFactory) -> {
			jedisConnectionFactoryMap.put(name, jedisConnectionFactory);
		});
		return jedisConnectionFactoryMap;
	}
	
}
