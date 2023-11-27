package org.cat.support.security3.generator.spring.context;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.cat.support.security3.generator.spring.user.UserExt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

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
public class JwtRedisSecurityContextRepository implements IJwtSecurityContextRepository {

	public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT:";
	
	@Setter AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	@Setter private String headerParamterName = "Authorization";
	
	private RedisTemplate<Object, Object> redisTemplate;
	private long durationSeconds = 60;
	
	
	public JwtRedisSecurityContextRepository(RedisTemplate<Object, Object> redisTemplate, long durationSeconds) {
		this.redisTemplate = redisTemplate;
		if(durationSeconds > 0) {
			this.durationSeconds = durationSeconds;
		}
	}
	
	@Override
	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
		String userCode = getUserCodeByRequest(requestResponseHolder.getRequest());
		if(StringUtils.isBlank(userCode)) {
			return null;
		}
		String key = SPRING_SECURITY_CONTEXT_KEY+userCode;
		Object securityContextObj = this.redisTemplate.opsForValue().get(key);
		SecurityContext securityContext = null;
		if (securityContextObj == null) {
			securityContext = generateNewContext();
		}else {
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
		String key = SPRING_SECURITY_CONTEXT_KEY+userCode;
		this.redisTemplate.opsForValue().set(key, securityContext, this.durationSeconds, TimeUnit.SECONDS);
		
	}
	
	@Override
	public void refreshSecurityContext(String jwtToken, long durationTimeSeconds) {
		String userCode = getUserCodeByJwtToken(jwtToken);
		String key = SPRING_SECURITY_CONTEXT_KEY+userCode;
		Object securityContextObj = this.redisTemplate.opsForValue().get(key);
		if(securityContextObj==null) {
			return;
		}
		this.redisTemplate.expire(key, durationTimeSeconds, TimeUnit.SECONDS);
	}
	
	@Override
	public void refreshSecurityContext(String jwtToken) {
		refreshSecurityContext(jwtToken, this.durationSeconds);
	}

	@Override
	public void removeSecurityContext(String jwtToken) {
		String userCode = getUserCodeByJwtToken(jwtToken);
		String key = SPRING_SECURITY_CONTEXT_KEY+userCode;
		this.redisTemplate.delete(key);
	}

	@Override
	public boolean containsContext(HttpServletRequest request) {
		String userCode = getUserCodeByRequest(request);
		if(StringUtils.isBlank(userCode)) {
			return false;
		}
		String key = SPRING_SECURITY_CONTEXT_KEY+userCode;
		Object securityContextObj = this.redisTemplate.opsForValue().get(key);
		if(securityContextObj==null) {
			return false;
		}
		return true;
	}
	
	protected SecurityContext generateNewContext() {
		return SecurityContextHolder.createEmptyContext();
	}
	
	protected String getUserCodeByRequest(HttpServletRequest request) {
		String jwtToken = request.getHeader(this.headerParamterName);
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
