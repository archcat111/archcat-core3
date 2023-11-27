package org.cat.core.web3.controller;

import javax.validation.ConstraintViolationException;

import org.cat.core.web3.resp.generator.IDownloadGenerator;
import org.cat.core.web3.resp.generator.IResultRespGenerator;
import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.core.web3.resp.generator.IWsRsRespGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbsArchRestRespController extends AbsBaseController {
	
	@Autowired(required=false)
	protected IResultRespGenerator resultRespGenerator;
	
	@Autowired(required=false)
	protected IDownloadGenerator downloadGenerator;
	
	@Autowired(required=false)
	protected ISpringRespGenerator springResponseGenerator;
	
	@Autowired(required=false)
	protected IWsRsRespGenerator wsrsResponseGenerator;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午2:23:52
	 * @version 1.0
	 * @description 统一拦截在Spring Controller之后的代码直接抛出的异常
	 * 		当是Ajax请求时，返回标准的ResultResp对象
	 * 		优先级：
	 * 			1：同一个异常只能被一个@ExceptionHandler处理
	 * 			2：业务Controller中定义的@ExceptionHandler优先级最高
	 * 			3：@ControllerAdvice其次
	 * 		注意：
	 * 			1：如果开启了RestLog，因为RestLog是基于Aspect的，因此会优先于这里执行
	 * 			2：因为RestLog需要记录Exception的Id和Code方便开发人员全局定义异常，所以RestLog会赋一个Id和Code
	 * 			3：如果RestLog赋过Id和Code，那么这里将直接使用RestLog给的Id和Code，否则这里会进行赋值
	 * @param exception
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler({Exception.class, RuntimeException.class})
	public Object doException(Exception exception,WebRequest webRequest){
		return this.resultRespGenerator.doResultResp(exception);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年5月5日 上午10:24:24
	 * @version 1.0
	 * @description 继承Javax-Valid框架后：
	 * 		bean的参数校验不进Log的Aspect，会抛出BindException直接到统一异常处理
	 * 		普通的参数校验会进Log的Aspect，会抛出ConstraintViolationException到统一异常处理 
	 * @param exception
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler({ConstraintViolationException.class})
	public Object doException(ConstraintViolationException exception,WebRequest webRequest){
		return this.resultRespGenerator.doResultResp(exception);
	}
	
	@ExceptionHandler({BindException.class})
	public Object doException(BindException exception,WebRequest webRequest){
		return this.resultRespGenerator.doResultResp(exception);
	}
	
}
