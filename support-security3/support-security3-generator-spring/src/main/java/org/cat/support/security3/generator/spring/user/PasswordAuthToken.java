package org.cat.support.security3.generator.spring.user;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import lombok.Getter;

/**
 * 
 * @author 王云龙
 * @date 2021年11月24日 下午3:47:15
 * @version 1.0
 * @description 增加了roles
 *
 */
public class PasswordAuthToken extends UsernamePasswordAuthenticationToken {
	
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	
	@Getter private final Collection<String> roles;

	public PasswordAuthToken(Object principal, Object credentials) {
		super(principal, credentials);
		roles = null;
	}

	public PasswordAuthToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities, Collection<String> roles) {
		super(principal, credentials, authorities);
		this.roles = roles;
	}
	
	

	
	
	
	

}
