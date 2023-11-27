package org.cat.support.springboot3.starter.web.security;

import java.util.List;

import org.apache.commons.pool2.impl.DefaultEvictionPolicy;
import org.cat.support.security3.generator.spring.constants.SecurityUserStateConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUserStateRedisProperties {
	
	private String redisConnectionFactoryName;
	
	private String infra = SecurityUserStateConstants.RedisInfra.STANDALONE;
	private Standalone standalone;
	private Cluster cluster;
	
	private Client client;
	
	@Getter
	@Setter
	public class Standalone {
		private String hostName;
		private Integer port;
		private Integer dbIndex = 0;
		
		private String userName;
		private String password;
	}
	
	@Getter
	@Setter
	public class Cluster {
		private List<String> clusterNodes; //127.0.0.1:23679, 127.0.0.1:23680, ...
		
		private Integer maxRedirects;
		
		private String userName;
		private String password;
	}
	
	@Getter
	@Setter
	public class Client {
		private String clientName = "SpringSecurityRedisClient";
		private int connectTimeoutSeconds = 3;
		private int readTimeoutSeconds = 10;
		
		private boolean blockWhenExhausted = true;
		private long maxWaitMillis = 3*1000L;
		private int maxTotal = 8;
		private int maxIdle = 8;
		private int minIdle = 0;
		private boolean testOnBorrow = false;
		private boolean testOnCreate = false;
		private boolean testOnReturn = false;
		private boolean testWhileIdle = true;
		
		private long minEvictableIdleTimeMillis = 60*1000L;
		private int numTestsPerEvictionRun = -1;
		private long softMinEvictableIdleTimeMillis = -1;
		private long timeBetweenEvictionRunsMillis = 30*1000L;
		private long evictorShutdownTimeoutMillis = 10*1000L;
		private String evictionPolicyClassName = DefaultEvictionPolicy.class.getName();
		
		private boolean lifo = true;
		private boolean fairness = false;
		
		private boolean jmxEnabled = true;
		private String jmxNameBase = null;
		private String jmxNamePrefix = "pool";
	}
}
