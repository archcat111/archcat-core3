package org.cat.support.security3.generator.spring.logout.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.event.LogoutSuccessEvent;

/**
 * 
 * @author 王云龙
 * @date 2018年1月29日 下午12:09:43
 * @version 1.0
 * @description 登出成功事件发布器
 *
 */
public class LogoutSuccessEventPublisher {
	@Autowired
	private ApplicationContext applicationContext;
	
	@Async
	public void publish(LogoutSuccessEvent logoutSuccessEvent){
		applicationContext.publishEvent(logoutSuccessEvent);
	}
	
}
