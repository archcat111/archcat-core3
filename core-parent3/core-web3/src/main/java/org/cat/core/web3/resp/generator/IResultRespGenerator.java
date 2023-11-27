package org.cat.core.web3.resp.generator;

import org.cat.core.web3.resp.ArchResultResp;

/**
 * 
 * @author wangyunlong
 * @date 2018年8月24日 下午3:09:51
 * @version 1.0
 * @description 自定义的Json返回结果的实现标准接口
 *
 */
public interface IResultRespGenerator {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2018年8月24日 下午5:47:21
	 * @version 1.0
	 * @description 根据异常和数据组装成返回自定义的json结果
	 * @param parentException 没有异常则填写null
	 * @param businessRespBean 需要写入ResultResp中的返回给客户端的业务数据，没有结果则填写null
	 * @return
	 */
	public <T> ArchResultResp<T> doResultResp(Throwable throwable, T businessRespBean);
	public <T> ArchResultResp<T> doResultResp(Throwable throwable);
	public <T> ArchResultResp<T> doResultResp(T businessRespBean);
	
}
