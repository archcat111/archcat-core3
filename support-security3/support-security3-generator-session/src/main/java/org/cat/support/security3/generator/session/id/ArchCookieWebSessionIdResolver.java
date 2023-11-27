package org.cat.support.security3.generator.session.id;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.cat.support.security3.generator.session.exception.ArchSessionException;
import org.springframework.http.HttpCookie;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.session.CookieWebSessionIdResolver;

/**
 * 
 * @author 王云龙
 * @date 2022年7月29日 下午9:02:26
 * @version 1.0
 * @description Reactive架构的项目中，WebSessionManager从Cookie中解析到的SessionId为加密过的
 * 		例如：NjEyYmIyYjctYTYwOC00NGQ5LTg0MDUtZmY0NDA2MTQ0Y2Ri
 * 			这里需要将该SessionId解码为eb64e63f-d9d3-4eb5-b545-aefaae413097
 * 		否则无法从Memory或者Redis的Session池中获取Session
 * 		因为Session池中的SessionId是使用类似eb64e63f-d9d3-4eb5-b545-aefaae413097来记录的
 *
 */
public class ArchCookieWebSessionIdResolver extends CookieWebSessionIdResolver {
	
	@Override
	public List<String> resolveSessionIds(ServerWebExchange exchange) {
		MultiValueMap<String, HttpCookie> cookieMap = exchange.getRequest().getCookies();
		List<HttpCookie> cookies = cookieMap.get(getCookieName());
		if (cookies == null) {
			return Collections.emptyList();
		}
		List<String> sessionIds = cookies.stream().map(HttpCookie::getValue).map(this::base64Decode).collect(Collectors.toList());
		return sessionIds;
	}
	
	private String base64Decode(String base64Value) {
        try {
            byte[] decodedCookieBytes = Base64.getDecoder().decode(base64Value);
            return new String(decodedCookieBytes);
        } catch (Exception ex) {
            throw new ArchSessionException("对Cookie中的SessionId进行解码出现异常", ex);
        }
    }
}
