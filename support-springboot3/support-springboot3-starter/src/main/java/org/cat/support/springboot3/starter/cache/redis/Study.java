package org.cat.support.springboot3.starter.cache.redis;

public class Study {
	public void JedisPoolConfig() {
		/**
		 * blockWhenExhausted：
		 * 		默认值：true
		 * 		连接耗尽时是否阻塞，false报异常，ture阻塞直到超时
		 *
		 *	maxWaitMillis：
		 *		默认值：-1（<0：阻塞不确定的时间）
		 *		获取连接时的最大等待毫秒数（如果设置为阻塞时BlockWhenExhausted），如果超时就抛异常
		 *
		 *	maxTotal：
		 *		默认值：8
		 *		最大连接数
		 *
		 *	maxIdle：
		 *		默认值：8
		 *		最大空闲连接数
		 *
		 *	minIdle：
		 *		默认值：0
		 *		最小空闲连接数
		 *
		 *	testOnBorrow：
		 *		默认值：false
		 *		在获取连接的时候检查有效性
		 *
		 *	testOnCreate：
		 *		默认值：false
		 *		在创建连接的时候检查有效性
		 *
		 *	testOnReturn：
		 *		默认值：false
		 *
		 *	testWhileIdle：
		 *		默认值：false（JedisPoolConfig会默认设置成true）
		 *		在空闲时检查有效性
		 *
		 *	//逐出相关：
		 *	minEvictableIdleTime：
		 *		默认值：1000L * 60L * 30L（30分钟）（JedisPoolConfig会默认设置成60秒，已标记过期）
		 *		逐出连接的最小空闲时间
		 *
		 *	numTestsPerEvictionRun：
		 *		默认值：3（JedisPoolConfig会默认设置成-1）
		 *		每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n)
		 *
		 *	softMinEvictableIdleTime：
		 *		默认值：-1
		 *		对象空闲多久后逐出
		 *		当空闲时间>该值 && 空闲连接>最大空闲数 时 直接逐出
		 *
		 *	timeBetweenEvictionRuns：
		 *		默认值：-1（JedisPoolConfig会默认设置成30秒，已标记过期）
		 *		逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程
		 *
		 * evictorShutdownTimeout：
		 * 		默认值：10L * 1000L
		 * 	
		 * evictionPolicyClassName：
		 * 		默认值：DefaultEvictionPolicy.class.getName()（当连接超过最大空闲时间,或连接数超过最大空闲连接数）
		 * 		设置连接的逐出策略类名
		 *		
		 *	lifo：
		 *		默认值：true
		 *		是否启用后进先出
		 * 	
		 *	fairness：
		 *		默认值：false
		 *
		 *	jmxEnabled：
		 *		默认值：true
		 *		是否启用pool的jmx管理功能
		 *
		 *	jmxNameBase：
		 *		默认值：null
		 *
		 *	jmxNamePrefix：
		 *		默认值："pool"
		 *		MBean ObjectName = new ObjectName("org.apache.commons.pool2:type=GenericObjectPool,name=" + "pool" + i);
		 *
		 */
	}
	
}
