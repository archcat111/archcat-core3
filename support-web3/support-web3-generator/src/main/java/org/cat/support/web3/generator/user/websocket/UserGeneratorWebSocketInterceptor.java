package org.cat.support.web3.generator.user.websocket;

import java.util.Map;

import org.cat.core.web3.user.IUserGenerator;
import org.cat.support.web3.generator.contants.UserConstants;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * 
 * @author 王云龙
 * @date 2022年9月22日 下午2:37:14
 * @version 1.0
 * @description UserGenerator对WebSocket的支持
 *
 */
public class UserGeneratorWebSocketInterceptor extends HttpSessionHandshakeInterceptor {
	
	private IUserGenerator userGenerator;
	
	public UserGeneratorWebSocketInterceptor(IUserGenerator userGenerator) {
		super();
		this.userGenerator = userGenerator;
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		//父类的默认实现，会获取sessionId以及session中的所有参数存储在attributes中
		//一般的微服务中不会涉及session因此，不使用父类中的实现
		if(userGenerator!=null) {
			Long userCode = userGenerator.getUserCodeForLong();
			String device = userGenerator.getDevice();
			attributes.put(UserConstants.ParamName.USER_CODE, userCode);
			attributes.put(UserConstants.ParamName.DEVICE, device);
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception ex) {
		super.afterHandshake(request, response, wsHandler, ex);
	}

}
