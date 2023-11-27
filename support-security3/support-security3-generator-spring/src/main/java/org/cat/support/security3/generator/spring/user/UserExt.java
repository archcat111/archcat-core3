package org.cat.support.security3.generator.spring.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;

import lombok.Getter;
import lombok.Setter;

public class UserExt extends User {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	
	@Getter @Setter private Long code;
	@Getter @Setter private String nickName;
	@Getter @Setter private List<String> roles;
	
	public UserExt(Long code, String nickName, List<String> roles, 
			String username, String password, Collection<? extends GrantedAuthority> authorities,
			boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.code=code;
		this.nickName=nickName;
		this.roles=roles;
	}
	
	
	
	public static UserExtBuilder extBuilder() {
		return new UserExtBuilder();
	}
	
	public static class UserExtBuilder {
		private String username;
		private String password;
		private List<GrantedAuthority> authorities=new ArrayList<GrantedAuthority>();
		private boolean accountExpired;
		private boolean accountLocked;
		private boolean credentialsExpired;
		private boolean disabled;
		private Function<String, String> passwordEncoder = password -> password;
		
		private Long code;
		private String nickName;
		private List<String> roles;

		/**
		 * Creates a new instance
		 */
		private UserExtBuilder() {
		}
		
		public Long getCode() {
			return code;
		}
		public UserExtBuilder setCode(Long code) {
			this.code = code;
			return this;
		}

		public String getNickName() {
			return nickName;
		}
		public UserExtBuilder setNickName(String nickName) {
			this.nickName = nickName;
			return this;
		}
		
		public UserExtBuilder roles(List<String> roles) {
			this.roles = new ArrayList<String>();
			for (String role : roles) {
				Assert.isTrue(!role.startsWith("ROLE_"), role
						+ " cannot start with ROLE_ (it is automatically added)");
				
				this.roles.add(role);
				authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
			}
			return this;
		}
		
		public UserExtBuilder username(String username) {
			Assert.notNull(username, "username cannot be null");
			this.username = username;
			return this;
		}

		public UserExtBuilder password(String password) {
			Assert.notNull(password, "password cannot be null");
			this.password = password;
			return this;
		}

		/**
		 * 
		 * @author wangyunlong
		 * @date 2021年11月24日 下午3:23:09
		 * @version 1.0
		 * @description 可以设置一个处理密码的lamuda表达式，默认不处理 
		 * @param encoder
		 * @return
		 */
		public UserExtBuilder passwordEncoder(Function<String, String> encoder) {
			Assert.notNull(encoder, "encoder cannot be null");
			this.passwordEncoder = encoder;
			return this;
		}
		
		public UserExtBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
			this.authorities.addAll(authorities);
			return this;
		}
		public UserExtBuilder authorities(String... authorities) {
			return authorities(AuthorityUtils.createAuthorityList(authorities));
		}
		

		public UserExtBuilder urlAuthorities(UrlAuthority... urlAuthorities) {
			return urlAuthorities(Arrays.asList(urlAuthorities));
		}
		public UserExtBuilder urlAuthorities(Collection<UrlAuthority> urlAuthorities) {
			for (UrlAuthority urlAuthority : urlAuthorities) {
				String authortyname = urlAuthority.getAuthority();
				Assert.isTrue(!authortyname.startsWith("AUTH_"), authortyname
						+ " cannot start with AUTH_ (it is automatically added)");
				
				StringBuilder stringBuilder=new StringBuilder();
				stringBuilder.append("AUTH_");
				stringBuilder.append(urlAuthority.getUrl());
				urlAuthority.setUrl(stringBuilder.toString());
				
				this.authorities.add(urlAuthority);
			}
			
			return this;
		}

		public UserExtBuilder accountExpired(boolean accountExpired) {
			this.accountExpired = accountExpired;
			return this;
		}
		public UserExtBuilder accountLocked(boolean accountLocked) {
			this.accountLocked = accountLocked;
			return this;
		}
		public UserExtBuilder credentialsExpired(boolean credentialsExpired) {
			this.credentialsExpired = credentialsExpired;
			return this;
		}
		public UserExtBuilder disabled(boolean disabled) {
			this.disabled = disabled;
			return this;
		}

		public UserExt build() {
			String encodedPassword = this.passwordEncoder.apply(password);
			UserExt userExt = new UserExt(this.code, this.nickName, this.roles,
					this.username, encodedPassword, this.authorities,
					!this.disabled, !this.accountExpired, !this.credentialsExpired, !this.accountLocked);
			return userExt;
		}
		
	}

}
