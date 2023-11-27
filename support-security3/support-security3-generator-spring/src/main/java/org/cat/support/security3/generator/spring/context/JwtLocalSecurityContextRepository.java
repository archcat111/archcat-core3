package org.cat.support.security3.generator.spring.context;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.cat.support.security3.generator.spring.user.UserExt;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年11月30日 上午10:51:32
 * @version 1.0
 * @description 默认情况下，如果采用JWT，则后端无需存储其状态
 * 		但是经常还是会需要在后端保存一些基础状态信息，如：权限信息，那么就需要用到该Repository
 *
 */
public class JwtLocalSecurityContextRepository implements IJwtSecurityContextRepository {

	@Setter private String headerParamterName = "Authorization";
	@Setter AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	
	private Cache<String, SecurityContext>  cache;
	private long durationSeconds = 60;
	
	public JwtLocalSecurityContextRepository(long durationSeconds) {
		if(durationSeconds > 0) {
			this.durationSeconds = durationSeconds;
		}
		this.cache = CacheBuilder.newBuilder().expireAfterWrite(this.durationSeconds, TimeUnit.SECONDS).build();
	}
	
	@Override
	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
		String userCode = getUserCodeByRequest(requestResponseHolder.getRequest());
		if(StringUtils.isBlank(userCode)) {
			return null;
		}
		SecurityContext securityContext = this.cache.getIfPresent(userCode);
		if (securityContext == null) {
			securityContext = generateNewContext();
		}
		return securityContext;
	}

	@Override
	public void saveContext(SecurityContext securityContext, HttpServletRequest request, HttpServletResponse response) {
		final Authentication authentication = securityContext.getAuthentication();
		
		if (authentication == null){
			return;
		}
		if(this.trustResolver.isAnonymous(authentication)) {
			return;
		}
		
		UserExt userExt = (UserExt) authentication.getPrincipal();
		String userCode = userExt.getCode()+"";
		this.cache.put(userCode, securityContext);
		
	}
	
	@Override
	public void refreshSecurityContext(String jwtToken, long durationTimeSeconds) {
		String userCode = getUserCodeByJwtToken(jwtToken);
		if(StringUtils.isBlank(userCode)) {
			return;
		}
		SecurityContext securityContext = this.cache.getIfPresent(userCode);
		if(securityContext==null) {
			return;
		}
		this.cache.put(userCode, securityContext);
	}
	
	@Override
	public void refreshSecurityContext(String jwtToken) {
		refreshSecurityContext(jwtToken, this.durationSeconds);
	}

	@Override
	public void removeSecurityContext(String jwtToken) {
		String userCode = getUserCodeByJwtToken(jwtToken);
		this.cache.invalidate(userCode);
	}

	@Override
	public boolean containsContext(HttpServletRequest request) {
		String userCode = getUserCodeByRequest(request);
		if(StringUtils.isBlank(userCode)) {
			return false;
		}
		SecurityContext securityContext = this.cache.getIfPresent(userCode);
		if(securityContext==null) {
			return false;
		}
		return true;
	}
	
	protected SecurityContext generateNewContext() {
		return SecurityContextHolder.createEmptyContext();
	}
	
	protected String getUserCodeByRequest(HttpServletRequest request) {
		String jwtToken = request.getHeader(this.headerParamterName);
		if(StringUtils.isBlank(jwtToken)) {
			return null;
		}
		String userCode = getUserCodeByJwtToken(jwtToken);
		return userCode;
	}
	
	protected String getUserCodeByJwtToken(String jwtToken) {
		DecodedJWT jwt = JWT.decode(jwtToken);
		List<String> userCodes = jwt.getAudience();
		String userCode = userCodes.get(0);
		return userCode;
	}

}
