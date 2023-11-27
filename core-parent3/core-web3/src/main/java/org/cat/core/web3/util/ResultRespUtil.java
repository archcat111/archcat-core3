package org.cat.core.web3.util;


import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.cat.core.exception3.ParentException;
import org.cat.core.exception3.ParentRuntimeException;
import org.cat.core.exception3.bean.ExceptionBean;
import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.constants.ExceptionConstants;
import org.cat.core.exception3.exception.StandardRuntimeException;
import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.core.web3.constants.ResultRespContants;
import org.cat.core.web3.resp.ArchResultResp;
import org.springframework.validation.BindException;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午6:08:00
 * @version 1.0
 * @description ResultResp处理相关工具类
 *
 */
public class ResultRespUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午6:13:02
	 * @version 1.0
	 * @description 创建一个ResultResp，并根据异常来初始化ResultResp的status、exception
	 * @param <T> 业务Bean的Class类型
	 * @param exceptionIdGenerator 异常ID生成器
	 * @param exceptionCodeGenerator 异常Code生成器
	 * @param throwable 业务返回的异常，不用处理，Controller继承的的上层AbsController会自动处理
	 * @return ResultResp（需要自行设置response）
	 */
	public static <T> ArchResultResp<T> createResultRespAndDoThrowable(IExceptionIdGenerator exceptionIdGenerator, IExceptionCodeGenerator exceptionCodeGenerator, Throwable throwable){
		ArchResultResp<T> resultResp=new ArchResultResp<T>();
		//如果存在异常
		if(throwable!=null) {
			//如果异常是ParentException或者其子类
			if(ParentException.class.isAssignableFrom(throwable.getClass())) {
				//在业务代码中已经设置ExceptionCode和ExceptionMsg
				//这里设置ExceptionId和ExceptionProjectCode
				ParentException parentException = (ParentException) throwable;
				if(ExceptionConstants.ParamValue.ID_NOT_WRITE.equals(parentException.getExceptionBean().getExceptionId())) {
					parentException.setExceptionId(exceptionIdGenerator.getExceptionId());
				}
				if(ExceptionConstants.ParamValue.PROJECT_CODE_NOT_WRITE.equals(parentException.getExceptionBean().getExceptionProjectCode())) {
					parentException.setProjectExceptionCode(exceptionCodeGenerator.getExceptionProjectCode());
				}
				//ExceptionCode和ExceptionMsg在该Exception进来之前已经被开发人员set进去了
				resultResp.setStatus(ResultRespContants.Status.BUSINESS_EXCEPTION);
				resultResp.setException(parentException);
			//如果异常是ParentRuntimeException或者其子类
			}else if(ParentRuntimeException.class.isAssignableFrom(throwable.getClass())) {
				//在业务代码中已经设置ExceptionCode和ExceptionMsg
				//这里设置ExceptionId和ExceptionProjectCode
				ParentRuntimeException parentRuntimeException = (ParentRuntimeException) throwable;
				if(ExceptionConstants.ParamValue.ID_NOT_WRITE.equals(parentRuntimeException.getExceptionBean().getExceptionId())) {
					parentRuntimeException.setExceptionId(exceptionIdGenerator.getExceptionId());
				}
				if(ExceptionConstants.ParamValue.PROJECT_CODE_NOT_WRITE.equals(parentRuntimeException.getExceptionBean().getExceptionProjectCode())) {
					parentRuntimeException.setProjectExceptionCode(exceptionCodeGenerator.getExceptionProjectCode());
				}
				//ExceptionCode和ExceptionMsg在该Exception进来之前已经被开发人员set进去了
				resultResp.setStatus(ResultRespContants.Status.BUSINESS_EXCEPTION);
				resultResp.setException(parentRuntimeException);
			}else if(BindException.class.isAssignableFrom(throwable.getClass())) { //javax.validation异常（）Bean中发生的异常
				BindException bindException = (BindException) throwable;
				
				ExceptionBean exceptionBean = new ExceptionBean();
				exceptionBean.setExceptionId(exceptionIdGenerator.getExceptionId());
				exceptionBean.setExceptionProjectCode(exceptionCodeGenerator.getExceptionProjectCode());
				exceptionBean.setExceptionCode(ExceptionConstants.ParamValue.CODE_BUSSINESS_VALID_EXCEPTION);
				exceptionBean.setExceptionMsg(bindException.getBindingResult().getFieldError().getDefaultMessage());
				resultResp.setExceptionBean(exceptionBean);
				resultResp.setStatus(ResultRespContants.Status.BUSINESS_EXCEPTION);
			}else if(ConstraintViolationException.class.isAssignableFrom(throwable.getClass())) { //javax.validation异常（）Bean中发生的异常
				ExceptionBean exceptionBean = new ExceptionBean();
				
				ConstraintViolationException constraintViolationException = (ConstraintViolationException) throwable;
				if(constraintViolationException.getSuppressed().length>0) {
					Throwable throwable0 = constraintViolationException.getSuppressed()[0];
					if(StandardRuntimeException.class.isAssignableFrom(throwable0.getClass())) {
						StandardRuntimeException standardRuntimeException = (StandardRuntimeException) throwable0;
						exceptionBean = standardRuntimeException.getExceptionBean()!=null?standardRuntimeException.getExceptionBean():null;
					}
				}
				resultResp.setStatus(ResultRespContants.Status.BUSINESS_EXCEPTION);
				if(ExceptionConstants.ParamValue.ID_NOT_WRITE.equals(exceptionBean.getExceptionId())) {
					exceptionBean.setExceptionId(exceptionIdGenerator.getExceptionId());
				}
				if(ExceptionConstants.ParamValue.PROJECT_CODE_NOT_WRITE.equals(exceptionBean.getExceptionProjectCode())) {
					exceptionBean.setExceptionProjectCode(exceptionCodeGenerator.getExceptionProjectCode());
				}
				if(ExceptionConstants.ParamValue.CODE_NOT_WRITE.equals(exceptionBean.getExceptionCode())) {
					exceptionBean.setExceptionCode(ExceptionConstants.ParamValue.CODE_BUSSINESS_VALID_EXCEPTION);
				}
				if(ExceptionConstants.ParamValue.EXCEPTION_MSG_NOT_WRITE.equals(exceptionBean.getExceptionMsg())) {
					for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
						exceptionBean.setExceptionMsg(constraintViolation.getMessage());
						break;
					}
				}
				resultResp.setExceptionBean(exceptionBean);
				
			}else {
				//系统抛出的普通异常，在此进行捕获并转化为标准输出
				//设置ExceptionId和ExceptionProjectCode
				//设置ExceptionCode为-2
				//设置ExceptionMsg为Exception的getMessage()
				ExceptionBean exceptionBean = new ExceptionBean();
				
				//如果启用了例如EventLog等功能，EventLog会在其处理过程中构建一个exceptionBean，这里直接服用
				//否则这里会自行构建ExceptionBean
				if(throwable.getSuppressed().length>0) {
					Throwable throwable0 = throwable.getSuppressed()[0];
					if(StandardRuntimeException.class.isAssignableFrom(throwable0.getClass())) {
						StandardRuntimeException standardRuntimeException = (StandardRuntimeException) throwable0;
						exceptionBean = standardRuntimeException.getExceptionBean()!=null?standardRuntimeException.getExceptionBean():null;
					}
				}
				resultResp.setStatus(ResultRespContants.Status.SYSTEM_EXCEPTION);
				if(ExceptionConstants.ParamValue.ID_NOT_WRITE.equals(exceptionBean.getExceptionId())) {
					exceptionBean.setExceptionId(exceptionIdGenerator.getExceptionId());
				}
				if(ExceptionConstants.ParamValue.PROJECT_CODE_NOT_WRITE.equals(exceptionBean.getExceptionProjectCode())) {
					exceptionBean.setExceptionProjectCode(exceptionCodeGenerator.getExceptionProjectCode());
				}
				if(ExceptionConstants.ParamValue.CODE_NOT_WRITE.equals(exceptionBean.getExceptionCode())) {
					exceptionBean.setExceptionCode(exceptionCodeGenerator.getExceptionSystemCode());
				}
				if(ExceptionConstants.ParamValue.EXCEPTION_MSG_NOT_WRITE.equals(exceptionBean.getExceptionMsg())) {
					exceptionBean.setExceptionMsg(throwable.getMessage());
				}
				resultResp.setExceptionBean(exceptionBean);
				throwable.printStackTrace();
			}
		}else {
			resultResp.setStatus(ResultRespContants.Status.NORMAL);
		}
		return resultResp;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年11月24日 下午5:27:12
	 * @version 1.0
	 * @description 创建一个正常的不考虑异常情况的 ResultResp
	 * @param <T>
	 * @return
	 */
	public static <T> ArchResultResp<T> createResultRespForNormal(){
		return createResultRespAndDoThrowable(null, null, null);
	}
}
