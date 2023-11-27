package org.cat.core.web3.resp.generator;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.cat.core.web3.resp.ArchResultResp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 
 * @author wangyunlong
 * @date 2018年8月24日 下午2:46:27
 * @version 1.0
 * @description 基于HttpServletResponse的PrintWriter进行输出控制标准接口
 *
 */
public interface ISpringRespGenerator {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午1:17:10
	 * @version 1.0
	 * @description 将HTTP状态及业务Bean写入HttpServletResponse对象  
	 * @param <T> 业务Bean的Class类型
	 * @param httpServletResponse {@code HttpServletResponse}
	 * @param httpStatus {@code HttpServletResponse#setStatus(int)}、{@linkplain HttpStatus}
	 * @param throwable 发生在流程中的业务异常or系统异常
	 * @param businessRespBean 需要写入ResultResp中的返回给客户端的业务数据，没有结果则填写null
	 * @throws IOException
	 */
	public <T> void doResponse(HttpServletResponse httpServletResponse, HttpStatus httpStatus, Throwable throwable, T businessRespBean) throws IOException;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午1:19:25
	 * @version 1.0
	 * @description 将HTTP状态、contentType及业务Bean写入HttpServletResponse对象 
	 * @param <T> 业务Bean的Class类型
	 * @param httpServletResponse {@code HttpServletResponse}
	 * @param httpStatus {@code HttpServletResponse#setStatus(int)}、{@linkplain HttpStatus}
	 * @param contentType  {@code MediaType}
	 * @param throwable 发生在流程中的业务异常or系统异常
	 * @param businessRespBean 需要写入ResultResp中的返回给客户端的业务数据，没有结果则填写null
	 * @throws IOException
	 */
	public <T> void doResponse(HttpServletResponse httpServletResponse, HttpStatus httpStatus, String contentType, Throwable throwable, T businessRespBean) throws IOException;

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午1:27:01
	 * @version 1.0
	 * @description 创建一个ResponseEntity对象并返回 
	 * @param <T> 业务Bean的Class类型
	 * @param httpStatus {@code HttpServletResponse#setStatus(int)}、{@linkplain HttpStatus}
	 * @param throwable 发生在流程中的业务异常or系统异常
	 * @param businessRespBean 需要写入ResultResp中的返回给客户端的业务数据，没有结果则填写null
	 * @return {@linkplain ResponseEntity}
	 */
	public <T> ResponseEntity<ArchResultResp<T>> doResponse(HttpStatus httpStatus, Throwable throwable, T businessRespBean);
	
}
