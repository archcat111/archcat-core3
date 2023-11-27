package org.cat.support.springboot3.starter.cache.redis;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisClusterProperties {
	private boolean enabled = true;
	private List<String> nodes;
	private Integer maxRedirects;
	
	private int maxTotal = 50; //最大连接数
	private int maxIdle = 10; //最大空闲连接数
	private int minIdle = 3; //最小空闲连接数
}
