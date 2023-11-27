package org.cat.core.web3.resp;

import java.util.Map;

import org.cat.core.util3.json.ArchJsonUtil;
import org.cat.core.web3.constants.ArchVersion;
import org.cat.core.web3.exception.RespException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * @author wangyunlong
 * @date 2017年8月13日 下午10:44:19
 * @version 1.0
 * @description Response抽象基础类，继承了序列化接口，同时显示了toJson方法
 *
 */
public abstract class AbsArchJsonResp implements IArchResp {

	private static final long serialVersionUID = ArchVersion.V3;

	public String toJson() {
		try {
			return ArchJsonUtil.toJsonAndLongToString(this);
		} catch (JsonProcessingException e) {
			throw new RespException("在对本Resp进行Json化时发生错误", e);
		}
	}
	
	public byte[] toJsonBytes() {
		try {
			return ArchJsonUtil.toJsonBytesAndLongToString(this);
		} catch (JsonProcessingException e) {
			throw new RespException("在对本Resp进行Json化时发生错误", e);
		}
	}
	
	public String toJson(Map<String, Object> addParameters) {
		String json = toJson();
		String result = ArchJsonUtil.addNewNodeToJson(json, addParameters);
		return result;
	}

	/**
	 * 
	 * @author 王云龙
	 * @date 2017年12月25日 上午11:23:14
	 * @version 1.0
	 * @description 重写toString方法，返回json
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return this.toJson();
	}
	
	
}
