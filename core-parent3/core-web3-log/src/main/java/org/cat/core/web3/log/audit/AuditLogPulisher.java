package org.cat.core.web3.log.audit;

import org.cat.core.web3.log.bean.AuditLogBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

/**
 * 
 * @author 王云龙
 * @date 2021年9月7日 下午6:29:22
 * @version 1.0
 * @description AuditLog事件发送器
 *
 */
public class AuditLogPulisher {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Async
	public void publish(AuditLogBean auditLogBean){
		this.applicationContext.publishEvent(auditLogBean);
	}
}
