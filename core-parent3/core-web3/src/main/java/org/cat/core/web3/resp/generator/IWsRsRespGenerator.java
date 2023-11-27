package org.cat.core.web3.resp.generator;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * 
 * @author wangyunlong
 * @date 2018年8月24日 下午2:46:27
 * @version 1.0
 * @description 基于ws.rs的Response进行输出控制标准接口
 *
 */
public interface IWsRsRespGenerator {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午4:40:18
	 * @version 1.0
	 * @description 将自定义的json返回结果使用ws.rs标准进行返回  
	 * @param <T> 需要返回的业务Bean
	 * @param status {@linkplain Status}
	 * @param throwable 发生在流程中的业务异常or系统异常
	 * @param businessRespBean 需要写入ResultResp中的返回给客户端的业务数据，没有结果则填写null
	 * @return
	 */
	public <T> Response doResponse(Status status, Throwable throwable, T businessRespBean);
	
}
