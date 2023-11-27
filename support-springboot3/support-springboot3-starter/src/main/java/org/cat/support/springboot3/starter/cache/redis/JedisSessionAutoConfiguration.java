package org.cat.support.springboot3.starter.cache.redis;

import java.util.List;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.security3.generator.session.study.Impl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnProperty(prefix = "cat.support3.cache.redis", name = "enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnClass({Jedis.class,JedisConnectionFactory.class})
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(RedisProperties.class)
public class JedisSessionAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Redis Session[Jedis]初始化：";
	
	@Autowired
	private RedisProperties redisProperties;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月23日 上午11:07:18
	 * @version 1.0
	 * @description 根据配置初始化用于session的JedisConnectionFactory
	 * 		需要添加@SpringSessionRedisConnectionFactory，会被SpringSession识别初始化时识别到优先用于SpringSession存储
	 * 		{@linkplain Impl}
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.cache.redis.session", name = "enabled", havingValue = "true", matchIfMissing = false)
	@SpringSessionRedisConnectionFactory
	@Bean
	public JedisConnectionFactory jedisConnectionFactoryForSession() {
		coreLogger.info(this.logPrefix+"开始初始化用于session存储的JedisConnectionFactory");
		
		RedisClusterProperties redisClusterProperties = redisProperties.getSession();
		List<String> nodes = redisClusterProperties.getNodes();
		Integer maxRedirects = redisClusterProperties.getMaxRedirects();
		int maxTotal = redisClusterProperties.getMaxTotal();
		int maxIdle = redisClusterProperties.getMaxIdle();
		int minIdle = redisClusterProperties.getMinIdle();
		
		RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(nodes);
		if (maxRedirects != null) {
			redisClusterConfiguration.setMaxRedirects(maxRedirects);
		}
		
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
		JedisClientConfiguration.JedisClientConfigurationBuilder configurationBuilder = JedisClientConfiguration.builder();
		JedisClientConfiguration jedisClientConfiguration = configurationBuilder.usePooling().poolConfig(jedisPoolConfig).build();
		
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration,jedisClientConfiguration);
		
		coreLogger.info(this.logPrefix+"完成初始化用于session存储的JedisConnectionFactory");
		return jedisConnectionFactory;
		
	}
	
}
