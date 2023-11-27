package org.cat.support.springcloud3.gateway.filter.global;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.core.web3.resp.ArchResultResp;
import org.cat.core.web3.resp.generator.IResultRespGenerator;
import org.cat.support.springcloud3.gateway.exception.GatewayException;
import org.cat.support.springcloud3.gateway.exception.GatewayExceptionEnum;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import com.google.common.collect.Lists;

import reactor.core.publisher.Mono;

/**
 * 
 * @author 王云龙
 * @date 2022年7月21日 下午4:59:19
 * @version 1.0
 * @description 基于Session得登录状态验证过滤器
 *
 */
public class LoginStatusVerifySessionGlobalFilter implements GlobalFilter, Ordered {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "LoginStatusVerifySessionGlobalFilter：";
	
	public static final int LOGIN_STATUS_VERIFY_GLOBAL_FILTER_ORDER = 5000;
	
	private int filterOrder = LOGIN_STATUS_VERIFY_GLOBAL_FILTER_ORDER;
	
	private String loginStatusAttrName;
	private List<String> excludeRouteIds = Lists.newArrayList();
	private List<String> excludeUris = Lists.newArrayList();
	
	@Autowired
	private IResultRespGenerator resultRespGenerator;
	
	public LoginStatusVerifySessionGlobalFilter(String loginStatusAttrName) {
		this.loginStatusAttrName = loginStatusAttrName;
	}
	
	public LoginStatusVerifySessionGlobalFilter(String loginStatusAttrName, int filterOrder) {
		this.loginStatusAttrName = loginStatusAttrName;
		this.filterOrder = filterOrder;
	}
	
	public void setExcludeRouteIds(List<String> excludeRouteIds) {
		this.excludeRouteIds = excludeRouteIds;
	}

	public void setExcludePaths(List<String> excludeUris) {
		this.excludeUris = excludeUris;
	}

	@Override
	public int getOrder() {
		return filterOrder;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
		//从配置加载排除的routeId，直接调用chain.filter(exchange);
		String routeId = route.getId();
		if(excludeRouteIds.contains(routeId)) {
			return chain.filter(exchange);
//					.then(Mono.just(exchange))
//					.map(serverWebExchange -> {
//						当前端请求是HTTPS时，response中Set-Cookie的value值中需要增加"...; Secure; ..."
//						ServerHttpRequest serverHttpRequest = serverWebExchange.getRequest();
//						HttpHeaders httpReqHeaders = serverHttpRequest.getHeaders();
//						String origin = httpReqHeaders.get("origin").get(0);
//						ServerHttpResponse serverHttpResponse = serverWebExchange.getResponse();
//						HttpHeaders httpRespHeaders = serverHttpResponse.getHeaders();
//						String setCookie = httpRespHeaders.get("Set-Cookie").get(0);
//						List<String> setCookieValues = StrUtil.split(setCookie, ",");
//						String newSetCookieValues = StrUtil.join(";", setCookieValues);
//						httpRespHeaders.put("Set-Cookie", Lists.newArrayList(newSetCookieValues));
//						MultiValueMap<String, ResponseCookie> cookies = serverHttpResponse.getCookies();
//						ResponseCookie responseCookie = ResponseCookie.from("a", "b").httpOnly(true).maxAge(3600).path("/").build();
//						serverHttpResponse.addCookie(responseCookie);
//						return serverWebExchange;
//					})
//					.then();
		}
		// /api/loginStatus
		Set<URI> originalUrls = exchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
		URI originalUrl = originalUrls.iterator().next();
		String originalUrlPath = originalUrl.getPath();
		if(originalUrls.size()>1) {
			coreLogger.warn(this.logPrefix+"originalUrls的数量大于1，本次默认将使用匹配"+originalUrl);
		}
	
		if(excludeUris.contains(originalUrlPath)) {
			return chain.filter(exchange);
		}
		
		Mono<WebSession> webSessionMono = exchange.getSession();
		return webSessionMono.flatMap(webSession -> {
			boolean loginStatus = isLogin(webSession);
			
			if(loginStatus) {
				return chain.filter(exchange);
			}else {
				return responseUnauthorized(exchange);
			}
            
        });
		
	}
	
	protected boolean isLogin(WebSession webSession) {
//		String sessionId = webSession.getId();
//		System.out.println("gateway-->"+sessionId);
		
		Boolean loginStatus = webSession.getAttribute(loginStatusAttrName);

		if(loginStatus==null || loginStatus==false) {
			return false;
		}
		return loginStatus.booleanValue();
	}
	
	protected Mono<Void> responseUnauthorized(ServerWebExchange exchange){
		ServerHttpResponse serverHttpResponse = exchange.getResponse();
		serverHttpResponse.setStatusCode(HttpStatus.OK);
		serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		
		byte[] body = createBodyFor401();
		
		DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();
		DataBuffer wrap = bufferFactory.wrap(body);
		return serverHttpResponse.writeWith(Mono.fromSupplier(() -> wrap));
	}
	
	private byte[] createBodyFor401() {
		GatewayException gatewayException = new GatewayException(GatewayExceptionEnum.UNAUTHORIZED);
		ArchResultResp<Void> resultResp = resultRespGenerator.doResultResp(gatewayException);
		byte[] resultRespJson = resultResp.toJsonBytes();
		return resultRespJson;
	}

}
