package org.cat.core.web3.log.audit;

import org.cat.core.web3.log.bean.AuditLogBean;
import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author 王云龙
 * @date 2021年9月8日 下午2:32:54
 * @version 1.0
 * @description Audit Log的事件
 *
 */
@Getter
@Setter
@ToString
public class AuditLogEvent extends ApplicationEvent {

	private static final long serialVersionUID = -3281831337154247501L;

	private AuditLogBean auditLogBean;
	
	public AuditLogEvent(Object source, AuditLogBean auditLogBean) {
		super(source);
		this.auditLogBean = auditLogBean;
	}

}
