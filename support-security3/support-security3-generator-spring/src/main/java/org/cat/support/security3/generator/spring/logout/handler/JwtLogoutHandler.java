package org.cat.support.security3.generator.spring.logout.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.context.IJwtSecurityContextRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年12月1日 上午10:27:10
 * @version 1.0
 * @description 参考ContextLogoutHandler
 * 		处理Jwt的过期以及状态信息
 *
 */
public class JwtLogoutHandler implements LogoutHandler {
	
	@Getter @Setter private SecurityContextRepository securityContextRepository;
	@Getter @Setter private String headerParamterName = "Authorization";

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if(IJwtSecurityContextRepository.class.isAssignableFrom(this.securityContextRepository.getClass())) {
			IJwtSecurityContextRepository jwtSecurityContextRepository = (IJwtSecurityContextRepository) this.securityContextRepository;
			jwtSecurityContextRepository.removeSecurityContext(this.headerParamterName);
		}

	}

}
