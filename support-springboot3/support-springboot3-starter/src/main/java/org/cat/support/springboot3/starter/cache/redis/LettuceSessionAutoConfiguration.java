package org.cat.support.springboot3.starter.cache.redis;

import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
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
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;

import io.lettuce.core.RedisClient;

@Configuration
@ConditionalOnProperty(prefix = "cat.support3.cache.redis", name = "enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnClass({RedisClient.class ,LettuceConnectionFactory.class})
@ConditionalOnWebApplication(type = Type.REACTIVE)
@EnableConfigurationProperties(RedisProperties.class)
public class LettuceSessionAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Redis Session[Lettuce]初始化：";
	
//	@Autowired
//	private ApplicationContext applicationContext;
	
	@Autowired
	private RedisProperties redisProperties;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月23日 上午11:12:43
	 * @version 1.0
	 * @description 根据配置初始化用于session的ReactiveRedisConnectionFactory
	 * 		需要添加@SpringSessionRedisConnectionFactory，会被SpringSession识别初始化时识别到优先用于SpringSession存储
	 * 		对于Reactive程序，目前只有Lettuce提供了实现，因此这里用Lettuce
	 * 		{@linkplain Impl}
	 * @return
	 */
	@ConditionalOnProperty(prefix = "cat.support3.cache.redis.session", name = "enabled", havingValue = "true", matchIfMissing = false)
	@SpringSessionRedisConnectionFactory
	@Bean
	public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
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
		
		
        GenericObjectPoolConfig<RedisClient> genericObjectPoolConfig = new GenericObjectPoolConfig<>();
        genericObjectPoolConfig.setMaxTotal(maxTotal);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMinIdle(minIdle);
        
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder configurationBuilder = LettucePoolingClientConfiguration.builder();
        LettuceClientConfiguration lettuceClientConfiguration = configurationBuilder.poolConfig(genericObjectPoolConfig).build();
		
        //LettuceConnectionFactory实现了ReactiveRedisConnectionFactory
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
		coreLogger.info(this.logPrefix+"完成初始化用于session存储的lettuceConnectionFactory");
		return lettuceConnectionFactory;
		
	}

}
