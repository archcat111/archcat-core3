package org.cat.core.web3.log.event;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.cat.core.exception3.ParentException;
import org.cat.core.exception3.ParentRuntimeException;
import org.cat.core.exception3.bean.ExceptionBean;
import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.constants.ExceptionConstants;
import org.cat.core.exception3.exception.StandardRuntimeException;
import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.core.util3.bean.ArchBeanUtil;
import org.cat.core.util3.date.ArchDateTimeUtil;
import org.cat.core.util3.json.ArchJsonUtil;
import org.cat.core.web3.constants.ResultRespContants;
import org.cat.core.web3.log.bean.EventLogBean;
import org.cat.core.web3.log.bean.common.ExceptionLogBean;
import org.cat.core.web3.log.bean.common.UserLogBean;
import org.cat.core.web3.log.constants.AppenderConstants;
import org.cat.core.web3.log.constants.EventLogConstants;
import org.cat.core.web3.log.id.ILogIdGenerator;
import org.cat.core.web3.resp.ArchResultResp;
import org.cat.core.web3.user.IUserGenerator;
import org.cat.core.web3.util.SpringHttpUtil;
import org.cat.core.web3.util.SpringIpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;

import lombok.Setter;


/**
 * 
 * @author 王云龙
 * @date 2017年3月8日 下午6:33:03
 * @version 1.0
 * @description Rest Log的切入实现类
 * 		顺序（正常）：Around1、Before、执行、AfterReturning、After、Around2
 * 		顺序（异常）：Around1、Before、执行、AfterThrowing、After
 * 		代理目标：Controller、Feign
 * 		
 * 		注意：
 * 			继承Javax-Valid框架后：
 * 			1：bean的参数校验不进这里的Aspect，会抛出BindException直接到统一异常处理
 * 			2：普通的参数校验会进这里的doEventAspectAfterThrowing，会抛出ConstraintViolationException到统一异常处理
 *
 */
@Aspect
public class EventLogAspect implements Ordered{

	protected final transient Logger coreLogger = LoggerFactory.getLogger(AppenderConstants.AppenderName.CORE);
	
	@Setter private IExceptionIdGenerator exceptionIdGenerator;
	@Setter private IExceptionCodeGenerator exceptionCodeGenerator;

	////////////////////////////////////////////////////////////////从配置中获取到的变量值
	@Setter private String company;
	@Setter private String platform;
	@Setter private String appName;
	@Setter private String appVersion;
	@Setter private String framework;
	@Setter private String frameworkSub;
	@Setter private String hostName;
	@Setter private String hostIp;
	@Setter private String hostPort;
	
	////////////////////////////////////////////////////////////////需要用到的工具
	@Setter private ILogIdGenerator logIdGenerator;
	@Setter private IUserGenerator userGenerator;
	
	@Autowired(required = true)
	private EventLogPulisher eventLogPulisher;
	
	////////////////////////////////////////////////////////////////临时数据
	private EventLogBean eventLogBean;
	
	private long eventLogStartExecuteTime;//日志开始执行的时间，在@around最开始进行记录
	private long apiStartTime;//代理方法开始执行时间，在@before最后进行记录
	private long apiEndTime;//代理方法完成执行时间，在@after最开始进行记录
	
	//方法上有@IgnoreEventLog && 类上有@IgnoreEventLog
	@Pointcut("@annotation(IgnoreEventLog) || @within(IgnoreEventLog)")
	public void ignoreEventLog() {}
	
	//方法上有@EventLog && 类上有@EventLog
	@Pointcut("@annotation(EventLog) || @within(EventLog)")
	public void eventLog() {}
	
	@Around(value = "eventLog() && !ignoreEventLog()")
	public Object doEventAspectAround(ProceedingJoinPoint joinPoint) throws Throwable {
		//临时数据
		this.eventLogStartExecuteTime = System.currentTimeMillis();
		
		Object object = joinPoint.proceed();
		return object;
	}
	
	/**
	 * @author wangyunlong
	 * @date 2022年1月25日 上午11:01:13
	 * @version 1.0
	 * @description 
	 * 		@annotation(eventLog)：这个参数值和该方法中的参数名必须相同，例如这里的eventLog和方法参数中的EventLog eventLog
	 * 			当该注解标注在!!!方法!!!上时需要该定义
	 * 			用于在该方法内获取该EventLog eventLog注解的对象，从而可以获取该注解中配置的一些属性 
	 * 		@within(eventLog))：这个参数值和该方法中的参数名必须相同，例如这里的eventLog和方法参数中的EventLog eventLog
	 * 			当该注解标注在!!!类!!!上时需要该定义
	 * 			用于在该方法内获取该EventLog eventLog注解的对象，从而可以获取该注解中配置的一些属性 
	 * 		注意！！！：如果只定义了@annotation(eventLog)但是没定义@within(eventLog))时：
	 * 			如果该注解是标注在!!!类!!!上时，那么@Before将不会起作用
	 * @param joinPoint
	 * @param eventLog
	 */
	@Before(value = "(eventLog() && !ignoreEventLog()) && (@annotation(eventLog) || @within(eventLog))")
	public void doEventAspectBefore(JoinPoint joinPoint, EventLog eventLog) {
		
		//创建EventLogBean
		this.eventLogBean = new EventLogBean();
		
		//Base数据
		this.eventLogBean.setLogId(logIdGenerator.getLogId());
//		eventLogBean.setLogType(eventLogType); //ok eventLogBean中已经有初始值为BaseLogConstants.Type.EVENT，无需设置
		this.eventLogBean.setLogCreateTimetamp(ArchDateTimeUtil.getFormatDateStr(new Date(), ArchDateTimeUtil.FormatConstants.DATETIME_DETAIL));
//		eventLogBean.setLogSelfExecutionTime(apiEndTime);//在AfterReturning And AfterThrowing中计算
		this.eventLogBean.setCompany(this.company);
		this.eventLogBean.setPlatform(this.platform);
		this.eventLogBean.setAppName(this.appName);
		this.eventLogBean.setAppVersion(this.appVersion);
		this.eventLogBean.setHostName(this.hostName);
		this.eventLogBean.setHostIp(this.hostIp);
		this.eventLogBean.setHostPort(this.hostPort);
		this.eventLogBean.setFramework(this.framework);
		this.eventLogBean.setFrameworkSub(this.frameworkSub);
		
		//event数据
		this.eventLogBean.setEventLogType(eventLog.type());
//		eventLogBean.setEventLogStatus(eventLogStatus);//在@AfterReturning、@AfterThrowing中设置
		this.eventLogBean.setUserLogBean(
				UserLogBean.createUserLogBean(
						userGenerator.getUserCode(), 
						userGenerator.getSessionId(), 
						SpringIpUtil.getRequestClientIps()
				)
		);
		this.eventLogBean.setApiName(eventLog.apiName());//从注解@EventLog的ApiName属性中获取
		this.eventLogBean.setApiVersion(SpringHttpUtil.getClientApiVersion());
		this.eventLogBean.setApiParams(ArchJsonUtil.toJson(SpringHttpUtil.getClientHttpParamsAndEncrypt(new String[] {"password"}, new String[] {"***"})));
		this.eventLogBean.setClazzName(joinPoint.getTarget().getClass().getName());
		this.eventLogBean.setMethodName(joinPoint.getSignature().getName());
//		eventLogBean.setApiExecutionTime(apiExecutionTime);//ok 在Before、After中进行计算
		this.eventLogBean.setHttpUri(SpringHttpUtil.getClientHttpUri());
		this.eventLogBean.setHttpMethod(SpringHttpUtil.getClientHttpMethod());
//		eventLogBean.setHttpStatus(httpStatus);//ok 在@AfterReturning、@AfterThrowing中设置
//		eventLogBean.setExceptionLogBean(exceptionLogBean);//ok 在@AfterReurning、@AfterThrowing中设置
		
		this.apiStartTime = System.currentTimeMillis();//代理方法开始执行时间
	}
	
	@After(value = "eventLog() && !ignoreEventLog()")
	public void doEventAspectAfter(JoinPoint joinPoint) {
		
		long eventLogEndExecuteTime = System.currentTimeMillis();
		long eventLogExecuteTime = eventLogEndExecuteTime - eventLogStartExecuteTime - eventLogBean.getApiExecutionTime();
		this.eventLogBean.setLogSelfExecutionTime(eventLogExecuteTime);
		
//		this.eventLogger.info(JsonUtil.toJson(eventLogBean));
		
		//因为异常的Id等需要在@ControllerAdvice中赋值的，例如ExceptionRestRespController
		//而@Aspect会优先于@ControllerAdvice，故这里输出的日志中，如果包含异常，则获取不到id
		//因此添加一个RestLogRespController的ControllerAdvice
		//这里将eventLogBean添加到ThreadLocal中，在RestLogRespController给EventLogBean中的Exception补全异常Id
//		EventLogThreadLocalHolder.set(eventLogBean);
		//然后publish eventlogEvent
		this.eventLogPulisher.publish(eventLogBean);
		
	}
	
	@AfterReturning(value = "eventLog() && !ignoreEventLog()", returning = "result")
	public void doEventAspectAfterReturning(JoinPoint joinPoint, Object result) {
		this.apiEndTime = System.currentTimeMillis();//代理方法完成执行时间
		this.eventLogBean.setApiExecutionTime(apiEndTime - apiStartTime);
		
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
	
	@AfterThrowing(pointcut = "eventLog() && !ignoreEventLog()", throwing = "e")
	public void doEventAspectAfterThrowing(JoinPoint joinPoint, Throwable e) {
		this.apiEndTime = System.currentTimeMillis();//代理方法完成执行时间
		this.eventLogBean.setApiExecutionTime(apiEndTime - apiStartTime);
		
		eventLogBean.setEventLogStatus(EventLogConstants.Status.UNKNOWN);
		
		//因为这里会优先于@ExceptionHandler捕获异常，所以在这里给异常
		if (e != null) {
			ExceptionLogBean exceptionLogBean = null;
			if(ParentException.class.isAssignableFrom(e.getClass())) {
				this.eventLogBean.setEventLogStatus(EventLogConstants.Status.BUSINESS_EXCEPTION);
				ParentException parentException = (ParentException) e;
				parentException.setExceptionId(exceptionIdGenerator.getExceptionId());
				parentException.setProjectExceptionCode(exceptionCodeGenerator.getExceptionProjectCode());
				exceptionLogBean = ArchBeanUtil.toBean(parentException.getExceptionBean(), ExceptionLogBean.class);
				this.eventLogBean.setExceptionLogBean(exceptionLogBean);
			}else if(ParentRuntimeException.class.isAssignableFrom(e.getClass())) {
				this.eventLogBean.setEventLogStatus(EventLogConstants.Status.BUSINESS_EXCEPTION);
				ParentRuntimeException parentRuntimeException = (ParentRuntimeException) e;
				parentRuntimeException.setExceptionId(exceptionIdGenerator.getExceptionId());
				parentRuntimeException.setProjectExceptionCode(exceptionCodeGenerator.getExceptionProjectCode());
				exceptionLogBean = ArchBeanUtil.toBean(parentRuntimeException.getExceptionBean(), ExceptionLogBean.class);
			}else if(ConstraintViolationException.class.isAssignableFrom(e.getClass())) {
				ConstraintViolationException constraintViolationException = (ConstraintViolationException)e;
				this.eventLogBean.setEventLogStatus(EventLogConstants.Status.BUSINESS_EXCEPTION);
				ExceptionBean exceptionBean=new ExceptionBean();
				exceptionBean.setExceptionId(exceptionIdGenerator.getExceptionId());
				exceptionBean.setExceptionProjectCode(exceptionCodeGenerator.getExceptionProjectCode());
				exceptionBean.setExceptionCode(ExceptionConstants.ParamValue.CODE_BUSSINESS_VALID_EXCEPTION);
				for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
					exceptionBean.setExceptionMsg(constraintViolation.getMessage());
					break;
				}
				StandardRuntimeException standardRuntimeException = new StandardRuntimeException();
				standardRuntimeException.setExceptionBean(exceptionBean);
				e.addSuppressed(standardRuntimeException);
				
				exceptionLogBean = ArchBeanUtil.toBean(exceptionBean, ExceptionLogBean.class);
			}else {
				this.eventLogBean.setEventLogStatus(EventLogConstants.Status.SYSTEM_EXCEPTION);
				
				ExceptionBean exceptionBean=new ExceptionBean();
				exceptionBean.setExceptionId(exceptionIdGenerator.getExceptionId());
				exceptionBean.setExceptionProjectCode(exceptionCodeGenerator.getExceptionProjectCode());
				exceptionBean.setExceptionCode(exceptionCodeGenerator.getExceptionSystemCode());
				exceptionBean.setExceptionMsg(e.getMessage());
				
				StandardRuntimeException standardRuntimeException = new StandardRuntimeException();
				standardRuntimeException.setExceptionBean(exceptionBean);
				e.addSuppressed(standardRuntimeException);
				
				exceptionLogBean = ArchBeanUtil.toBean(exceptionBean, ExceptionLogBean.class);
			}
			this.eventLogBean.setExceptionLogBean(exceptionLogBean);
		}
		
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2018年7月18日 下午7:03:15
	 * @version 1.0
	 * @description 如果返回的对象是自定义的ResultResp，从中解析设置HttpStatus、 eventLogStatus、ExceptionBean
	 * @param result
	 */
	private void recordResultRespNormal(Object result) {
		ArchResultResp<?> resultResp = (ArchResultResp<?>) result;
		
		int httpStatus = HttpServletResponse.SC_OK;
		this.eventLogBean.setHttpStatus(httpStatus + "");
		
		recordEventLogBeanStatusAndExecption(resultResp);
	}

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午1:37:27
	 * @version 1.0
	 * @description 解析spring responseEvent记录日志的httpStatus、eventLogStatus、exceptionBean
	 * @param result AspectJ返回的代理方法结果
	 */
	private void recordResponseEntityNormal(Object result) {
		@SuppressWarnings("unchecked")
		ResponseEntity<String> responseEntity = (ResponseEntity<String>) result;
		
		int httpStatus = responseEntity.getStatusCodeValue();
		this.eventLogBean.setHttpStatus(httpStatus + "");
		
		String responseEntityBody = responseEntity.getBody();
		ArchResultResp<?> resultResp = ArchJsonUtil.toObject(responseEntityBody, ArchResultResp.class);
		
		recordEventLogBeanStatusAndExecption(resultResp);
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
		
		int httpStatus = response.getStatus();
		this.eventLogBean.setHttpStatus(httpStatus + "");
		
		ArchResultResp<?> resultResp = (ArchResultResp<?>) response.getEntity();
		
		recordEventLogBeanStatusAndExecption(resultResp);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年9月8日 下午4:32:07
	 * @version 1.0
	 * @description 从ResultResp中获取Status以及Exception信息，填补EventLog 
	 * @param resultResp
	 */
	private void recordEventLogBeanStatusAndExecption(ArchResultResp<?> resultResp) {
		String status=resultResp.getStatus();
		
		if(ResultRespContants.Status.NORMAL.equals(status)) {//正常
			this.eventLogBean.setEventLogStatus(EventLogConstants.Status.NORMAL);
		}else if(ResultRespContants.Status.BUSINESS_EXCEPTION.equals(status)){//业务异常
			this.eventLogBean.setEventLogStatus(EventLogConstants.Status.BUSINESS_EXCEPTION);
			ExceptionLogBean commonExceptionLogBean = new ExceptionLogBean();
			BeanUtils.copyProperties(resultResp.getExceptionBean(), commonExceptionLogBean);
			this.eventLogBean.setExceptionLogBean(commonExceptionLogBean);
		}else if(ResultRespContants.Status.SYSTEM_EXCEPTION.equals(status)) {//系统异常
			this.eventLogBean.setEventLogStatus(EventLogConstants.Status.SYSTEM_EXCEPTION);
			ExceptionLogBean commonExceptionLogBean = new ExceptionLogBean();
			BeanUtils.copyProperties(resultResp.getExceptionBean(), commonExceptionLogBean);
			this.eventLogBean.setExceptionLogBean(commonExceptionLogBean);
		}else if(ResultRespContants.Status.UNKNOWN.equals(status)) {//Controller or Feign拦截器设置为Unknow
			this.eventLogBean.setEventLogStatus(EventLogConstants.Status.UNKNOWN);
		}else {//其他情况
			this.eventLogBean.setEventLogStatus(status);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 500;
	}

}
