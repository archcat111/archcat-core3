package org.cat.support.springboot3.starter.web.websocket;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.web.websocket")
@Getter
@Setter
public class ArchWebSocketProperties {
	//key为handler这件事的逻辑名称
	private Map<String, ArchWebSocketHandlerProperties> handlers = Maps.newHashMap();
}
