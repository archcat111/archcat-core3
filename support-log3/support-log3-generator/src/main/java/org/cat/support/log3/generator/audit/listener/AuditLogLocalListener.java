package org.cat.support.log3.generator.audit.listener;

import org.cat.core.util3.json.ArchJsonUtil;
import org.cat.core.web3.log.audit.AuditLogEvent;
import org.cat.core.web3.log.bean.AuditLogBean;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationListener;

/**
 * 
 * @author 王云龙
 * @date 2021年9月9日 上午11:05:56
 * @version 1.0
 * @description 将AuditLog打印在本地的配置
 *
 */
public class AuditLogLocalListener implements ApplicationListener<AuditLogEvent> {

	protected final transient Logger auditLogger = ArchLoggerFactory.getAuditLogger();
	
	@Override
	public void onApplicationEvent(AuditLogEvent event) {
		AuditLogBean auditLogBean = event.getAuditLogBean();
		this.auditLogger.error(ArchJsonUtil.toJson(auditLogBean));
	}

}
