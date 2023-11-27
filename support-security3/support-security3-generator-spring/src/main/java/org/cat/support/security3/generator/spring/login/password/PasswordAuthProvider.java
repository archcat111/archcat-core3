package org.cat.support.security3.generator.spring.login.password;

import org.cat.support.security3.generator.spring.user.PasswordAuthToken;
import org.cat.support.security3.generator.spring.user.UserExt;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;

/**
 * 
 * @author 王云龙
 * @date 2021年11月24日 下午1:58:19
 * @version 1.0
 * @description 基于用户名和密码的登录验证提供者
 * 
 * 		passwordEncoder：必须，默认为BCryptPasswordEncoder，DaoAuthenticationProvider中默认为null，密码加密解密
 * 		userDetailsService：必须，默认为null，根据登录信息从介质中获取用户信息
 * 		userDetailsPasswordService：默认为null，当该参数!=null并且passwordEncoder.upgradeEncoding(user.getPassword())!=null，则会更新密码
 * 
 * 		userCache：默认为NullUserCache，用户缓存
 * 		forcePrincipalAsString：默认为false，返回的AuthenticationToken中的principal为UserDetails，如果为true，则为user.getUsername()
 * 		hideUserNotFoundExceptions：默认为true，当发生UsernameNotFoundException异常时抛出BadCredentialsException，为false时返回真实的UsernameNotFoundException
 * 		preAuthenticationChecks：默认为DefaultPreAuthenticationChecks，检查从DB返回的UserDetails的isAccountNonLocked、isEnabled、isAccountNonExpired
 * 		postAuthenticationChecks：默认为DefaultPostAuthenticationChecks，检查从DB返回的UserDetails的isCredentialsNonExpired
 * 		authoritiesMapper：默认为NullAuthoritiesMapper，将this.authoritiesMapper.mapAuthorities(从DB返回的UserDetails的getAuthorities())的结果set到AuthenticationToken中的Collection<GrantedAuthority> authorities
 *
 */
public class PasswordAuthProvider extends DaoAuthenticationProvider {
	
	//覆盖DaoAuthenticationProvider中的userDetailsPasswordService
	private UserDetailsPasswordService userDetailsPasswordService;
	//覆盖AbstractUserDetailsAuthenticationProvider中的authoritiesMapper
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
	
	@Override
	public boolean supports(Class<?> authentication) {
		return (PasswordAuthToken.class.isAssignableFrom(authentication));
	}

	@Override
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
			UserDetails user) {
		boolean upgradeEncoding = this.userDetailsPasswordService != null
				&& this.getPasswordEncoder().upgradeEncoding(user.getPassword());
		if (upgradeEncoding) {
			String presentedPassword = authentication.getCredentials().toString();
			String newPassword = this.getPasswordEncoder().encode(presentedPassword);
			user = this.userDetailsPasswordService.updatePassword(user, newPassword);
		}
		
		UserExt userExt = (UserExt) user;
		PasswordAuthToken result = new PasswordAuthToken(principal,
				authentication.getCredentials(), this.authoritiesMapper.mapAuthorities(userExt.getAuthorities()), 
				userExt.getRoles());
		result.setDetails(authentication.getDetails());
		this.logger.debug("Authenticated user");
		return result;
	}

	@Override
	public void setUserDetailsPasswordService(UserDetailsPasswordService userDetailsPasswordService) {
		this.userDetailsPasswordService = userDetailsPasswordService;
	}

	@Override
	public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
		this.authoritiesMapper = authoritiesMapper;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// TODO 父类的实现中会强制类型转换为UsernamePasswordAuthenticationToken
		//不知道是否会有问题，有可能需要重写
		return super.authenticate(authentication);
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		super.additionalAuthenticationChecks(userDetails, authentication);
		//TODO
		//这个方法进行密码校验，如果需要根据每个登录连接配置的密码策略进行密码校验则需要重写这个方法
		//userDetails是从数据库获取的，authentication是登录传入的
		//authentication.getDetails()是一个DynamicParameterAuthDetails，里面有passwordEncryption
		
//		if (authentication.getCredentials() == null) {
//			this.logger.debug("Failed to authenticate since no credentials provided");
//			throw new BadCredentialsException(this.messages
//					.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
//		}
//		String presentedPassword = authentication.getCredentials().toString();
//		if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
//			this.logger.debug("Failed to authenticate since password does not match stored value");
//			throw new BadCredentialsException(this.messages
//					.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
//		}
	}	
	
	
	
}
