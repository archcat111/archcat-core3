package org.cat.support.security3.generator.spring.constants;

public class SecurityUserStateConstants {
	public static final class Frame{
		public static final String SESSION = "session";
		public static final String JWT = "jwt";
		public static final String NULL = "null";
	}
	
	public static final class Storage{
		public static final String LOCAL = "local";
		public static final String REDIS = "redis";
	}
	
	public static final class RedisInfra{
		public static final String STANDALONE = "standalone";
		public static final String CLUSTER = "cluster";
	}
}
