package org.cat.support.web3.generator.resp;

import org.cat.core.exception3.code.IExceptionCodeGenerator;
import org.cat.core.exception3.id.IExceptionIdGenerator;
import org.cat.core.web3.resp.ArchResultResp;
import org.cat.core.web3.resp.generator.IResultRespGenerator;
import org.cat.core.web3.util.ResultRespUtil;

import lombok.Setter;

/**
 * 
 * @author wangyunlong
 * @date 2018年8月24日 下午5:46:35
 * @version 1.0
 * @description 自定义的Json返回结果的实现标准接口的实现类
 *
 */
/**
 * 
 * @author 王云龙
 * @date 2021年8月16日 下午3:53:17
 * @version 1.0
 * @description 自定义的Json返回结果的实现标准接口的实现类
 * 		这里会处理异常，支持ParentException、ParentRuntimeException、系统异常
 * 		最终返回标准的ResultResp
 *
 */
public class ResultRespGeneratorImpl implements IResultRespGenerator {

	@Setter private IExceptionIdGenerator exceptionIdGenerator; //标准异常id生成器
	@Setter private IExceptionCodeGenerator exceptionCodeGenerator; //标准异常Code生成器
	
	@Override
	public <T> ArchResultResp<T> doResultResp(Throwable throwable, T businessRespBean) {
		ArchResultResp<T> resultResp=ResultRespUtil.createResultRespAndDoThrowable(exceptionIdGenerator, exceptionCodeGenerator, throwable);
		resultResp.setResponse(businessRespBean);
		return resultResp;
	}
	
	@Override
	public <T> ArchResultResp<T> doResultResp(Throwable throwable) {
		ArchResultResp<T> resultResp=doResultResp(throwable, null);
		return resultResp;
	}

	@Override
	public <T> ArchResultResp<T> doResultResp(T businessRespBean) {
		ArchResultResp<T> resultResp=doResultResp(null, businessRespBean);
		return resultResp;
	}
	
}
