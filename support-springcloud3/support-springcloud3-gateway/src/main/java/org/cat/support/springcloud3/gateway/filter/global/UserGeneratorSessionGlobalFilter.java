package org.cat.support.springcloud3.gateway.filter.global;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.util3.json.ArchJsonUtil;
import org.cat.core.util3.string.ArchCharsets2;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.springcloud3.gateway.constants.ArchGatewayConstants.TransferAttrTypeConstants;
import org.slf4j.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

import com.google.common.collect.Lists;

import cn.hutool.core.net.URLEncodeUtil;
import reactor.core.publisher.Mono;

/**
 * 
 * @author 王云龙
 * @date 2022年8月11日 下午4:27:48
 * @version 1.0
 * @description 基于Session，将一部分信息填写到header中好传递给后台
 * 		例如：从session中将userCode信息取出来放在header或者其他介质中传递给后端服务，
 * 			后端服务可以通过userGeneratorForHttp的实现获取到用户信息
 *
 */
public class UserGeneratorSessionGlobalFilter implements GlobalFilter, Ordered {
	
	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	protected final transient String logPrefix = "UserInfoHeaderSessionGlobalFilter：";
	
	public static final int USER_INFO_HEADER_SESSION_GLOBAL_FILTER_ORDER = 5001;
	private int filterOrder = USER_INFO_HEADER_SESSION_GLOBAL_FILTER_ORDER;
	
	//哪些routeId或者uri会被排除掉，不进行信息传递
	private List<String> excludeRouteIds = Lists.newArrayList();
	private List<String> excludeUris = Lists.newArrayList();
	
	//在截至中获取需要传递的信息时，介质中的信息叫什么名字
	private String userAttrName = "user";
	//需要从介质中获取到名字为上面userAttrName配置的值的json字符串后，需要获取哪些key，传递给后端服务时name叫什么
	//key：举例：从session中获取到userCode为111，这里配置为user-code，则会在header中写入user-code=111
	//value：举例：从session中获取到的用户信息是{code:111,userName:aaa,detail:{status:1}}
	////那么获取code，则这里配置value为code，获取detail的status，则这里配置value为detail.status
	private Map<String, String> transferAttrs;
	//获取到信息后，通过何种方式向后端服务传递
	private String transferAttrType = TransferAttrTypeConstants.HEADER;
	
	public UserGeneratorSessionGlobalFilter(int filterOrder) {
		this.filterOrder = filterOrder;
	}
	
	public void setExcludeRouteIds(List<String> excludeRouteIds) {
		this.excludeRouteIds = excludeRouteIds;
	}
	public void setExcludeUris(List<String> excludeUris) {
		this.excludeUris = excludeUris;
	}
	
	public void setUserAttrName(String userAttrName) {
		this.userAttrName = userAttrName;
	}
	public void setTransferAttrs(Map<String, String> transferAttrs) {
		this.transferAttrs = transferAttrs;
	}
	public void setTransferAttrType(String transferAttrType) {
		this.transferAttrType = transferAttrType;
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
		}
		Set<URI> originalUrls = exchange.getAttribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
		URI originalUrl = originalUrls.iterator().next();
		String originalUrlPath = originalUrl.getPath();
		if(originalUrls.size()>1) {
			coreLogger.warn(this.logPrefix+"originalUrls的数量大于1，本次默认将使用匹配"+originalUrl);
		}
	
		if(excludeUris.contains(originalUrlPath)) {
			return chain.filter(exchange);
		}
		
		if(transferAttrs==null || transferAttrs.isEmpty()) {
			return chain.filter(exchange);
		}
		
		Mono<WebSession> webSessionMono = exchange.getSession();
		return webSessionMono.flatMap(webSession -> {
			//从session中获取用户信息，是个json，SessionUser.class
			String userJson = webSession.getAttribute(userAttrName);
			if(StringUtils.isNotBlank(userJson)) {
				//如果配置中配置的是以header的方式传递参数则执行
				if(TransferAttrTypeConstants.HEADER.equals(transferAttrType)) {
					//原来的request是不能够编辑，header是readOnly的，这里重新构建一个request
					ServerHttpRequest.Builder serverHttpRequestBuilder = exchange.getRequest().mutate();
					//轮询配置中配置的需要传递的参数列表
					transferAttrs.forEach((headerName, jsonPath) -> {
						//在session中取出的json中获取这些jsonPath对应的value
						String headerValue = ArchJsonUtil.getValueForStr(userJson, jsonPath);
						//如果value存在，则存入header中
						if(StringUtils.isNotBlank(headerValue)) {
							//因为vaue有可能是中文，因此，这次都会进行编码
							String encodeHeaderValue = URLEncodeUtil.encode(headerValue, ArchCharsets2.UTF_8);
							serverHttpRequestBuilder.header(headerName, encodeHeaderValue);
						}
					});
					ServerHttpRequest newServerHttpRequest = serverHttpRequestBuilder.build();
					return chain.filter(exchange.mutate().request(newServerHttpRequest).build());
				}
			}
			return chain.filter(exchange);
        });
	}

}
