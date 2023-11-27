package org.cat.support.springboot3.starter.cache.redis;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.core.web3.util.ArchSpringBeanUtil;
import org.cat.support.storage3.generator.redis.JedisConnectionFactoryHolder;
import org.cat.support.storage3.generator.redis.RedisTemplateHolder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnProperty(prefix = "cat.support3.cache.redis", name = "enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnClass({Jedis.class,JedisConnectionFactory.class, JedisConnectionFactoryHolder.class})
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(RedisProperties.class)
public class JedisAutoConfiguration {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "Redis [Jedis]初始化：";
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private RedisProperties redisProperties;
	
	@Bean
	public JedisConnectionFactoryHolder jedisConnectionFactoryHolder() {
		JedisConnectionFactoryHolder jedisConnectionFactoryHolder = new JedisConnectionFactoryHolder();
		
		Map<String, RedisClusterProperties> redisClusterPropertiesMap = redisProperties.getClusters();
		
		for (Entry<String, RedisClusterProperties> entry : redisClusterPropertiesMap.entrySet()) {
			String jedisConnectionFactoryName = entry.getKey();
			String jedisConnectionFactoryBeanName = jedisConnectionFactoryName + "-jedisConnectionFactory";
			RedisClusterProperties redisClusterProperties = entry.getValue();
			
			if(!redisClusterProperties.isEnabled()) {
				continue;
			}
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
			
			RedisTemplate<?, ?> redisTemplate = new StringRedisTemplate();
			redisTemplate.setConnectionFactory(jedisConnectionFactory);
			
			ArchSpringBeanUtil.registerBean(applicationContext, jedisConnectionFactoryBeanName, jedisConnectionFactory);
			this.coreLogger.info(this.logPrefix+"已完成：将JedisConnectionFactory["+jedisConnectionFactoryBeanName+"]注入到Spring容器中");
			jedisConnectionFactoryHolder.put(jedisConnectionFactoryName, jedisConnectionFactory);
		}
		
		return jedisConnectionFactoryHolder;
	}
	
	@Bean
	public RedisTemplateHolder redisTemplateHolder() {
		RedisTemplateHolder redisTemplateHolder = new RedisTemplateHolder();
		JedisConnectionFactoryHolder jedisConnectionFactoryHolder = jedisConnectionFactoryHolder();
		jedisConnectionFactoryHolder.getJedisConnectionFactoryMapper().forEach((jedisConnectionFactoryName, jedisConnectionFactory) -> {
			String redisTemplateName = jedisConnectionFactoryName;
			String redisTemplateBeanName = redisTemplateName + "-redisTemplate";
			
			RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
			redisTemplate.setConnectionFactory(jedisConnectionFactory);
			
			ArchSpringBeanUtil.registerBean(applicationContext, redisTemplateBeanName, redisTemplate);
			this.coreLogger.info(this.logPrefix+"已完成：将JedisConnectionFactory["+redisTemplateBeanName+"]注入到Spring容器中");
			redisTemplateHolder.put(redisTemplateName, redisTemplate);
		});
		
		return redisTemplateHolder;
	}
	
}
