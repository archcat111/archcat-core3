package org.cat.support.springboot3.starter.web.websocket;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.resp.generator.IResultRespGenerator;
import org.cat.core.web3.user.IUserGenerator;
import org.cat.core.web3.util.ArchSpringBeanUtil;
import org.cat.core.web3.validator.websocket.ArchWebSocketHandler;
import org.cat.core.web3.validator.websocket.WebsocketHandlerHolder;
import org.cat.support.web3.generator.user.websocket.UserGeneratorWebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import cn.hutool.core.util.ClassUtil;

@Configuration
@ConditionalOnClass({TextWebSocketHandler.class, UserGeneratorWebSocketInterceptor.class})
@ConditionalOnProperty(prefix = "cat.support3.web.websocket", name = "enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(ArchWebSocketProperties.class)
@EnableWebSocket
public class ArchWebSocketAutoConfiguration implements WebSocketConfigurer {
	
	@Autowired
	private ArchWebSocketProperties archWebSocketProperties;
	
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private IResultRespGenerator resultRespGenerator;
	@Autowired
	private IUserGenerator userGenerator;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		Map<String, ArchWebSocketHandlerProperties> handlers = archWebSocketProperties.getHandlers();
		if(handlers.size()>0) {
			WebsocketHandlerHolder websocketHandlerHolder = websocketHandlerHolder();
			handlers.forEach((name, handlerProperties) -> {
				String url = handlerProperties.getUrl();
				String beanName = handlerProperties.getBeanName();
				ArchWebSocketHandler archWebSocketHandler = websocketHandlerHolder.get(beanName);
				//将url注册到该handler
				WebSocketHandlerRegistration webSocketHandlerRegistration = registry.addHandler(archWebSocketHandler, url);
				//如果该handler配置了拦截器，则添加拦截器
				webSocketHandlerRegistration.addInterceptors(userGeneratorWebSocketInterceptor());
				List<String> interceptors = handlerProperties.getInterceptorNames();
				if(interceptors!=null && interceptors.size()>0) {
					interceptors.forEach(interceptorClassName -> {
						//从spring容器中获取名字叫xxx的类型为HttpSessionHandshakeInterceptor的拦截器Bean
						String interceptorBeanName = StringUtils.substringAfterLast(interceptorClassName, ".");
						Class<?> interceptorClazz = ClassUtil.loadClass(interceptorClassName);
						Object handshakeInterceptorObj = ArchSpringBeanUtil.getBean(applicationContext, interceptorBeanName, interceptorClazz);
						HandshakeInterceptor handshakeInterceptor = handshakeInterceptorObj==null?null:(HandshakeInterceptor)handshakeInterceptorObj;
						//如果spring容器中没有名字叫xxx的类型为HttpSessionHandshakeInterceptor的拦截器Bean则创建
						if(handshakeInterceptor==null){
							handshakeInterceptor = (HandshakeInterceptor) ArchSpringBeanUtil.registerBean(applicationContext, interceptorBeanName, interceptorClazz);
						}
						//将拦截器逐个注册到该handler
						webSocketHandlerRegistration.addInterceptors(handshakeInterceptor);
					});
				}
				if(handlerProperties.isWithSockJS()) {
					webSocketHandlerRegistration.withSockJS();
				}
			});
		}
		
	}
	
	@Bean 
	public UserGeneratorWebSocketInterceptor userGeneratorWebSocketInterceptor() {
		UserGeneratorWebSocketInterceptor userGeneratorWebSocketInterceptor = new UserGeneratorWebSocketInterceptor(userGenerator);
		return userGeneratorWebSocketInterceptor;
	}
	
	@Bean
	public WebsocketHandlerHolder websocketHandlerHolder() {
		WebsocketHandlerHolder websocketHandlerHolder = new WebsocketHandlerHolder();
		Map<String, ArchWebSocketHandlerProperties> handlers = archWebSocketProperties.getHandlers();
		if(handlers.size()>0) {
			handlers.forEach((name, handlerProperties) -> {
				String beanName = handlerProperties.getBeanName();
				String handlerClassName = handlerProperties.getHandlerClassName();
				Class<?> handlerClazz = ClassUtil.loadClass(handlerClassName);
				Object archWebSocketHandlerObj = ArchSpringBeanUtil.getBean(applicationContext, beanName, handlerClazz);
				ArchWebSocketHandler archWebSocketHandler = archWebSocketHandlerObj==null?null:(ArchWebSocketHandler)archWebSocketHandlerObj;
				if(archWebSocketHandler==null) {
					archWebSocketHandler = (ArchWebSocketHandler) ArchSpringBeanUtil.registerBean(applicationContext, beanName, handlerClazz, resultRespGenerator);
				}
				websocketHandlerHolder.put(beanName, archWebSocketHandler);
			});
		}
		return websocketHandlerHolder;
	}
	
}
