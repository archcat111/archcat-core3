package org.cat.core.web3.validator.websocket;

import java.util.Map;

import org.cat.core.util3.map.SourceHolder;

import com.google.common.collect.Maps;

/**
 * 
 * @author 王云龙
 * @date 2022年9月23日 下午1:47:56
 * @version 1.0
 * @description ArchWebSocketHandler的管理器
 *
 */
public class WebsocketHandlerHolder extends SourceHolder<ArchWebSocketHandler> {

	public Map<String, ArchWebSocketHandler> getArchWebSocketHandlerMapper(){
		Map<String, ArchWebSocketHandler> archWebSocketHandlerMap = Maps.newHashMap();
		this.getSelfSourceMapper().forEach((name, archWebSocketHandler) -> {
			archWebSocketHandlerMap.put(name, archWebSocketHandler);
		});
		return archWebSocketHandlerMap;
	}
	
}
