package org.cat.core.web3.log.audit;

import java.util.Date;

import javax.ws.rs.core.Response;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.cat.core.exception3.ParentException;
import org.cat.core.exception3.ParentRuntimeException;
import org.cat.core.util3.date.ArchDateTimeUtil;
import org.cat.core.util3.json.ArchJsonUtil;
import org.cat.core.web3.constants.ResultRespContants;
import org.cat.core.web3.log.bean.AuditLogBean;
import org.cat.core.web3.log.bean.common.UserLogBean;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.core.web3.log.constants.AuditLogConstants;
import org.cat.core.web3.log.id.ILogIdGenerator;
import org.cat.core.web3.resp.ArchResultResp;
import org.cat.core.web3.user.IUserGenerator;
import org.cat.core.web3.util.SpringIpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;

import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年9月8日 下午1:59:20
 * @version 1.0
 * @description Audit Log的切入实现类
 * 		顺序（正常）：Around1、Before、执行、AfterReturning、After、Around2
 * 		顺序（异常）：Around1、Before、执行、AfterThrowing、After
 * 		代理目标：Controller、Feign
 *
 */
public class AuditLogAspect implements Ordered {

	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	
	////////////////////////////////////////////////////////////////从配置中获取到的变量值
	@Setter private String company;
	@Setter private String platform;
	@Setter private String appName;
	@Setter private String appVersion;
	@Setter private String framework;
	@Setter private String frameworkSub;
	@Setter private String hostName;
	@Setter private String hostIp;
	
	////////////////////////////////////////////////////////////////需要用到的工具
	@Setter private ILogIdGenerator logIdGenerator;
	@Setter private IUserGenerator userGenerator;
	
	@Autowired(required = true)
	private AuditLogPulisher aduitLogPulisher;
	
	////////////////////////////////////////////////////////////////临时数据
	private AuditLogBean auditLogBean;
	
	private long auditLogStartExecuteTime;//日志开始执行的时间，在@around最开始进行记录
	private long apiStartTime;//代理方法开始执行时间，在@before最后进行记录
	private long apiEndTime;//代理方法完成执行时间，在@after最开始进行记录
	
	//方法上有@IgnoreAuditLog && 类上有@IgnoreAuditLog
	@Pointcut("(@annotation(org.cat.core.web3.log.audit.IgnoreAuditLog) || @within(org.cat.core.web3.log.audit.IgnoreAuditLog)")
	public void ignoreAuditLog() {}
	
	//方法上有@AuditLog && 类上有@AuditLog
	@Pointcut("@annotation(org.cat.core.web3.log.audit.AuditLog) || @within(org.cat.core.web3.log.audit.AuditLog)")
	public void auditLog() {
	}
	
	@Around(value = "auditLog() && !ignoreAuditLog()")
	public Object doAuditAspectAround(ProceedingJoinPoint joinPoint) throws Throwable {
		//临时数据
		this.auditLogStartExecuteTime = System.currentTimeMillis();
		
		Object object = joinPoint.proceed();
		return object;
	}
	
	@Before(value = "eventEventLog() && !eventIgnoreEventLog() && annotation(eventLog)")
	public void doAuditAspectBefore(JoinPoint joinPoint, AuditLog auditLog) {
		
		//创建AuditLogBean
		this.auditLogBean = new AuditLogBean();
		
		//Base数据
		this.auditLogBean.setLogId(logIdGenerator.getLogId());
//		eventLogBean.setLogType(eventLogType); //ok auditLogBean中已经有初始值为BaseLogConstants.Type.AUDIT，无需设置
		this.auditLogBean.setLogCreateTimetamp(ArchDateTimeUtil.getFormatDateStr(new Date(), ArchDateTimeUtil.FormatConstants.DATETIME_DETAIL));
//		eventLogBean.setLogSelfExecutionTime(apiEndTime);//在AfterReturning And AfterThrowing中计算
		this.auditLogBean.setCompany(this.company);
		this.auditLogBean.setPlatform(this.platform);
		this.auditLogBean.setAppName(this.appName);
		this.auditLogBean.setAppVersion(this.appVersion);
		this.auditLogBean.setHostName(this.hostName);
		this.auditLogBean.setHostIp(this.hostIp);
		this.auditLogBean.setFramework(this.framework);
		this.auditLogBean.setFrameworkSub(this.frameworkSub);
		
		//audit数据
		this.auditLogBean.setModule(auditLog.module());
//		eventLogBean.setEventLogStatus(eventLogStatus);//在@AfterReturning、@AfterThrowing中设置
		this.auditLogBean.setUserLogBean(
				UserLogBean.createUserLogBean(
						userGenerator.getUserCode(), 
						userGenerator.getSessionId(), 
						SpringIpUtil.getRequestClientIps()
				)
		);
		this.auditLogBean.setAction(auditLog.action());
		this.auditLogBean.setAuditMsg(auditLog.auditMsg());
//		this.auditLogBean.setResult(null); //在@AfterReturning、@AfterThrowing中设置
		
		this.apiStartTime = System.currentTimeMillis();//代理方法开始执行时间
	}
	
	@After(value = "auditLog() && !ignoreAuditLog()")
	public void doAuditAspectAfter(JoinPoint joinPoint) {
		
		long auditLogEndExecuteTime = System.currentTimeMillis();
		long auditLogExecuteTime = auditLogEndExecuteTime - this.auditLogStartExecuteTime 
				- (this.apiEndTime - this.apiStartTime);
		this.auditLogBean.setLogSelfExecutionTime(auditLogExecuteTime);
		
//		this.auditLogger.info(JsonUtil.toJson(auditLogBean));
		this.aduitLogPulisher.publish(auditLogBean);
	}
	
	@AfterReturning(value = "auditLog() && !ignoreAuditLog()", returning = "result")
	public void doEventAspectAfterReturning(JoinPoint joinPoint, Object result) {
		this.apiEndTime = System.currentTimeMillis();//代理方法完成执行时间
		
		if(result == null) {
			//TODO 如果代理的Controller or Feign没有返回值
		}else{
			if(ArchResultResp.class.isAssignableFrom(result.getClass())) {//如果返回对象是ResultResp
				recordResultRespNormal(result);
			}else if(ResponseEntity.class.isAssignableFrom(result.getClass())) {//如果返回对象是Controller的ResponseEntity
				recordResponseEntityNormal(result);
			}else if(Response.class.isAssignableFrom(result.getClass())){//如果返回对象是Jersey获取其他框架的Wsrs标准的Response
				recordWsrsResponse(result);
			}
		}
		
	}
	
	@AfterThrowing(pointcut = "auditLog() && !ignoreAuditLog()", throwing = "e")
	public void doEventAspectAfterThrowing(JoinPoint joinPoint, Throwable e) {
		this.apiEndTime = System.currentTimeMillis();//代理方法完成执行时间
		
		if (e != null) {
			if(ParentException.class.isAssignableFrom(e.getClass())) {
				this.auditLogBean.setResult(AuditLogConstants.Result.BUSINESS_EXCEPTION);
			}else if(ParentRuntimeException.class.isAssignableFrom(e.getClass())) {
				this.auditLogBean.setResult(AuditLogConstants.Result.BUSINESS_EXCEPTION);
			}else {
				this.auditLogBean.setResult(AuditLogConstants.Result.SYSTEM_EXCEPTION);
			}
		}
		
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月8日 下午4:04:00
	 * @version 1.0
	 * @description 如果返回的对象是自定义的ResultResp，从中解析设置auditLogResult
	 * @param result
	 */
	private void recordResultRespNormal(Object result) {
		ArchResultResp<?> resultResp = (ArchResultResp<?>) result;
		
		recordAuditLogBeanResult(resultResp);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午1:37:27
	 * @version 1.0
	 * @description 解析spring response Audit记录日志的auditLogResult
	 * @param result AspectJ返回的代理方法结果
	 */
	private void recordResponseEntityNormal(Object result) {
		@SuppressWarnings("unchecked")
		ResponseEntity<String> responseEntity = (ResponseEntity<String>) result;
		
		String responseEntityBody = responseEntity.getBody();
		ArchResultResp<?> resultResp = ArchJsonUtil.toObject(responseEntityBody, ArchResultResp.class);
		
		recordAuditLogBeanResult(resultResp);
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2017年4月24日 下午5:13:10
	 * @version 1.0
	 * @description 解析jersey response
	 *
	 * @param result
	 */
	private void recordWsrsResponse(Object result) {
		Response response = (Response) result;
		
		ArchResultResp<?> resultResp = (ArchResultResp<?>) response.getEntity();
		
		recordAuditLogBeanResult(resultResp);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月8日 下午4:32:07
	 * @version 1.0
	 * @description 从ResultResp中获取Status信息，填补AuditLog.Result 
	 * @param resultResp
	 */
	private void recordAuditLogBeanResult(ArchResultResp<?> resultResp) {
		String status=resultResp.getStatus();
		
		if(ResultRespContants.Status.NORMAL.equals(status)) {//正常
			this.auditLogBean.setResult(AuditLogConstants.Result.NORMAL);
		}else if(ResultRespContants.Status.BUSINESS_EXCEPTION.equals(status)){//业务异常
			this.auditLogBean.setResult(AuditLogConstants.Result.BUSINESS_EXCEPTION);
		}else if(ResultRespContants.Status.SYSTEM_EXCEPTION.equals(status)) {//系统异常
			this.auditLogBean.setResult(AuditLogConstants.Result.SYSTEM_EXCEPTION);
		}else if(ResultRespContants.Status.UNKNOWN.equals(status)) {//Controller or Feign拦截器设置为Unknown
			this.auditLogBean.setResult(AuditLogConstants.Result.UNKNOWN);
		}else {//其他情况
			this.auditLogBean.setResult(null);
		}
	}
	
	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE - 600;
	}

}
