package org.cat.support.web3.generator.resp;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.core.web3.resp.ArchResultResp;
import org.cat.core.web3.resp.generator.IWsRsRespGenerator;
import org.cat.core.web3.util.ResultRespUtil;

import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午6:15:45
 * @version 1.0
 * @description 基于ws.rs的Response进行输出控制标准接口的实现类
 *
 */
public class WsRsRespGeneratorImpl implements IWsRsRespGenerator {

	@Setter private IExceptionIdGenerator exceptionIdGenerator;
	@Setter private IExceptionCodeGenerator exceptionCodeGenerator;
	
	@Override
	public <T> Response doResponse(Status status, Throwable throwable, T businessRespBean) {
		ArchResultResp<T> resultResp=ResultRespUtil.createResultRespAndDoThrowable(exceptionIdGenerator, exceptionCodeGenerator, throwable);
		resultResp.setResponse(businessRespBean);
		
		return Response.status(status).entity(resultResp).build();
	}

}
