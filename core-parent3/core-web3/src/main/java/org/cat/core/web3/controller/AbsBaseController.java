package org.cat.core.web3.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午2:43:55
 * @version 1.0
 * @description  实现{@linkplain IBaseController}的抽象类
 * 		该抽象类中使用{@linkplain Autowired}注入{@linkplain HttpServletRequest}、{@linkplainHttpServletResponse}、{@linkplain HttpSession}
 * 		并提供该三个参数的get以及set方法
 *
 */
@Getter
@Setter
public abstract class AbsBaseController implements IBaseController {
	
	@Autowired
	protected HttpServletRequest httpServletRequest;
	
	@Autowired
	protected HttpServletResponse httpServletResponse;
	
	@Autowired
	protected HttpSession httpSession;
}
