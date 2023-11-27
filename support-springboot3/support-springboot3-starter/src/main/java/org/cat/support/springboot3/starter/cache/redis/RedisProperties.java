package org.cat.support.springboot3.starter.cache.redis;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.cache.redis")
@Getter
@Setter
public class RedisProperties {
	private RedisClusterProperties session;
	
	private Map<String, RedisClusterProperties> clusters = Maps.newHashMap();
	
	//private Map<String, RedisStandaloneProperties> standalones = Maps.newHashMap();
}
