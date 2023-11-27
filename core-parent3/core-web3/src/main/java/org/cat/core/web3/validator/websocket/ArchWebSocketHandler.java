package org.cat.core.web3.validator.websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.resp.ArchResultResp;
import org.cat.core.web3.resp.generator.IResultRespGenerator;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.common.collect.Lists;

public abstract class ArchWebSocketHandler extends TextWebSocketHandler {
	
	protected IResultRespGenerator resultRespGenerator;
	
	protected Map<String, WebSocketSession> webSocketSessionMap = new ConcurrentHashMap<>();
	
	public ArchWebSocketHandler(IResultRespGenerator resultRespGenerator) {
		this.resultRespGenerator = resultRespGenerator;
	}
	
	//创建WebSocket连接时
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String sessionkey = this.getWebSocketSessionKey(session);
		if(StringUtils.isNotBlank(sessionkey)) {
			webSocketSessionMap.put(sessionkey, session);
			ArchResultResp<String> resultResp = resultRespGenerator.doResultResp("建立服务端连接成功！");
			session.sendMessage(new TextMessage(resultResp.toJson()));
		}
	}
	
	//传输过程出现异常时
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		ArchResultResp<?> resultResp = resultRespGenerator.doResultResp(exception);
		session.sendMessage(new TextMessage(resultResp.toJson()));
	}
	
	//关闭WebSocket连接时
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		String sessionkey = this.getWebSocketSessionKey(session);
		if(StringUtils.isNotBlank(sessionkey)) {
			webSocketSessionMap.remove(sessionkey);
		}
	}
	
	//发送信息给对应sessionKey的websocket连接
	public <T> void sendMessageToSessionKey(String sessionKey, T contents) throws IOException {
		WebSocketSession session = webSocketSessionMap.get(sessionKey);
		if (session != null && session.isOpen()) {
			ArchResultResp<T> resultResp = resultRespGenerator.doResultResp(contents);
			TextMessage message = new TextMessage(resultResp.toJson());
			session.sendMessage(message);
		}
	}
	
	//发送信息给本实例所有sessionKey的websocket连接
	public <T> void sendMessageToAllSessionKeys(T contents) throws IOException {
		Set<String> sessionKeys = webSocketSessionMap.keySet();
		for (String sessionKey : sessionKeys) {
			this.sendMessageToSessionKey(sessionKey, contents);
		}
	}
	
	//获取本实例所有的sessionKey
	public List<String> getAllSessionKey(){
		List<String> listSessionkey = Lists.newArrayList(webSocketSessionMap.keySet());
		return listSessionkey;
	}
	
	//从websocketSession中获取sessionKey
	protected abstract String getWebSocketSessionKey(WebSocketSession webSocketSession);
		
}
