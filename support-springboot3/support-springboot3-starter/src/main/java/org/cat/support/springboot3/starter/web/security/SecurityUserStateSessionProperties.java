package org.cat.support.springboot3.starter.web.security;

import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityUserStateSessionProperties {
	
	private long durationTimeMillis = 60*60*1000; //过期时间1小时
	private String namespace = "spring:cat:session";
	//Sessions flush mode. Determines when session changes are written to the session store.
	private FlushMode flushMode = FlushMode.ON_SAVE;
	//Sessions save mode. Determines how session changes are tracked and saved to the session store.
	private SaveMode saveMode = SaveMode.ON_SET_ATTRIBUTE;
	//Cron expression for expired session cleanup job.
	private String cleanupCron = "0 * * * * *";
	
	//RedisSessionProperties
	private ConfigureAction configureAction = ConfigureAction.NOTIFY_KEYSPACE_EVENTS;
	
	/**
	 * Strategies for configuring and validating Redis.
	 */
	public enum ConfigureAction {

		/**
		 * Ensure that Redis Keyspace events for Generic commands and Expired events are
		 * enabled.
		 */
		NOTIFY_KEYSPACE_EVENTS,

		/**
		 * No not attempt to apply any custom Redis configuration.
		 */
		NONE

	}
}
