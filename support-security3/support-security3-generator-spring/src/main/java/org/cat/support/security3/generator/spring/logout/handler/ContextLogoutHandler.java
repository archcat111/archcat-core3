package org.cat.support.security3.generator.spring.logout.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * 
 * @author 王云龙
 * @date 2021年12月1日 上午10:27:10
 * @version 1.0
 * @description 参考ContextLogoutHandler
 * 		处理SecurityContextHolder中的Authentication、Context
 *
 */
public class ContextLogoutHandler implements LogoutHandler {
	
//	@Getter @Setter private SecurityContextRepository securityContextRepository;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(null);
		SecurityContextHolder.clearContext();

	}

}
