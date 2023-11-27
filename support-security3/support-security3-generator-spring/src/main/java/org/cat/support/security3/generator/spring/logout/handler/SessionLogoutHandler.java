package org.cat.support.security3.generator.spring.logout.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * 
 * @author 王云龙
 * @date 2021年12月1日 上午10:27:10
 * @version 1.0
 * @description 参考ContextLogoutHandler
 * 		处理Session
 *
 */
public class SessionLogoutHandler implements LogoutHandler {

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}

}
