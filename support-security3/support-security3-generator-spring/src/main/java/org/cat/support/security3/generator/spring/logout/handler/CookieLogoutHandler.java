package org.cat.support.security3.generator.spring.logout.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

/**
 * 
 * @author 王云龙
 * @date 2021年12月1日 上午10:27:10
 * @version 1.0
 * @description 参考ContextLogoutHandler
 * 		处理Cookie
 *
 */
public class CookieLogoutHandler implements LogoutHandler {

	private final List<Function<HttpServletRequest, Cookie>> cookiesToClear;
	
	public CookieLogoutHandler(String... cookiesToClear) {
		Assert.notNull(cookiesToClear, "List of cookies cannot be null");
		
		List<Function<HttpServletRequest, Cookie>> cookieList = new ArrayList<>();
		for (String cookieName : cookiesToClear) {
			cookieList.add((request) -> {
				Cookie cookie = new Cookie(cookieName, null);
				String cookiePath = request.getContextPath() + "/";
				cookie.setPath(cookiePath);
				cookie.setMaxAge(0);
				cookie.setSecure(request.isSecure());
				return cookie;
			});
		}
		this.cookiesToClear = cookieList;
	}
	
	public CookieLogoutHandler(Cookie... cookiesToClear) {
		Assert.notNull(cookiesToClear, "List of cookies cannot be null");
		
		List<Function<HttpServletRequest, Cookie>> cookieList = new ArrayList<>();
		for (Cookie cookie : cookiesToClear) {
			Assert.isTrue(cookie.getMaxAge() == 0, "Cookie maxAge must be 0");
			cookieList.add((request) -> cookie);
		}
		this.cookiesToClear = cookieList;
	}



	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		this.cookiesToClear.forEach((f) -> response.addCookie(f.apply(request)));
	}

}
