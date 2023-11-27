package org.cat.core.web3.log.audit;

/**
 * 
 * @author 王云龙
 * @date 2021年9月8日 下午2:35:31
 * @version 1.0
 * @description 标记了该注解的类或者方法会被记录AuditLog
 * 		注意：如果在类上标记@AuditLog，在方法上标记@IgnoreAuditLog，那么该类中没有标记@IgnoreAuditLog的方法会记录AuditLog
 * 		注意：如果在类上或者方法上同时标记了@AuditLog和@IgnoreAuditLog，那么该类或者该方法不会记录AuditLog
 *
 */
public @interface AuditLog {
	String module() default "未填写";
	String action() default "未填写";
	String auditMsg();
}
