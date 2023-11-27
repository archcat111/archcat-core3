package org.cat.core.web3.resp;

import java.io.Serializable;

/**
 * 
 * @author 王云龙
 * @date 2017年1月23日 下午5:28:16
 * @version 1.0
 * @description Response对象的基础接口，继承了序列化接口
 *
 */
public interface IArchResp extends Serializable {
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月13日 下午10:37:49
	 * @version 1.0
	 * @description 将本对象json化 
	 * @return
	 */
	public String toJson();
}
