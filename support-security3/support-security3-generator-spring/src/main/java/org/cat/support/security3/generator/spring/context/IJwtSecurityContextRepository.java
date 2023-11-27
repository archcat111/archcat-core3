package org.cat.support.security3.generator.spring.context;

import org.springframework.security.web.context.SecurityContextRepository;

public interface IJwtSecurityContextRepository extends SecurityContextRepository {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年12月2日 下午3:36:55
	 * @version 1.0
	 * @description 刷新jwtToken中的Audience对应的SecurityContext的过期时间
	 * @param jwtToken
	 * @param durationTimeSeconds 过期时长，单位为分钟
	 */
	public void refreshSecurityContext(String jwtToken, long durationTimeSeconds);
	
	public void removeSecurityContext(String jwtToken);

	public void refreshSecurityContext(String jwtToken);
}
