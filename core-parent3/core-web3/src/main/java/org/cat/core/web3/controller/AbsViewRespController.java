package org.cat.core.web3.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.core.web3.resp.generator.IDownloadGenerator;
import org.cat.core.web3.resp.generator.IResultRespGenerator;
import org.cat.core.web3.resp.generator.ISpringRespGenerator;
import org.cat.core.web3.resp.generator.IWsRsRespGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbsViewRespController extends AbsBaseController {
	
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
	 * 		当是页面请求时，返回error/500路径的页面，并且返回异常信息到exception参数 
	 * @param exception
	 * @param webRequest
	 * @return
	 */
	@ExceptionHandler(value=Exception.class)
	public Object doException(Exception exception,WebRequest webRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		exception.printStackTrace();
		ModelAndView modelAndView=new ModelAndView("error/500");
		modelAndView.addObject("exception", exception);
		return modelAndView;
	}
}
