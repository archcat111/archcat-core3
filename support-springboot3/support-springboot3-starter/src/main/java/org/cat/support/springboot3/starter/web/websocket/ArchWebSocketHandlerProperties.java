package org.cat.support.springboot3.starter.web.websocket;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchWebSocketHandlerProperties {
	private String url;
	private String beanName;
	private String handlerClassName;
	private List<String> interceptorNames;
	private boolean withSockJS = false;
}
