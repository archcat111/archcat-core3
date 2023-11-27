package org.cat.support.security3.generator.spring.login.password;

import java.util.ArrayList;
import java.util.List;

import org.cat.support.security3.generator.spring.user.UrlAuthority;
import org.cat.support.security3.generator.spring.user.UserExt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TestUserService implements UserDetailsService {

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		if(!username.equals("testUserName")) {
			throw new UsernameNotFoundException("用户未找到");
		}
		
		//假设从数据库或者远程服务获取如下信息
		Long dbUserCode = 123L;
		String dbUserName = "testUserName";
		String dbPassowrd = "$2a$10$52.zJyfIr9TgMvFXnYsv5ew5iJ52n3.eOK5ik7qVae/HFK7Ao/FBa";
		String dbNickName = "测试小c";
		
		List<String> dbRoles=new ArrayList<String>();
		dbRoles.add("USER");
		
		List<UrlAuthority> dbAuthorities=new ArrayList<>();
		dbAuthorities.add(new UrlAuthority("1", "menu", "/v1/prod", "code=3"));
		dbAuthorities.add(new UrlAuthority("11", "button", "/v1/prod/insert", null));
		dbAuthorities.add(new UrlAuthority("12", "button", "/v1/prod/update", null));
		dbAuthorities.add(new UrlAuthority("2", "menu", "/v1/prodSupoort", "1,2,3"));
		
		
		UserDetails userDetails=UserExt.extBuilder()
				.setCode(dbUserCode)
				.setNickName(dbNickName)
				.roles(dbRoles)
				.username(dbUserName)
				.password(dbPassowrd)
				.authorities(dbAuthorities)
				.disabled(false)
				.accountExpired(false)
				.accountLocked(false)
				.credentialsExpired(false)
				.build();
		
		return userDetails;
	}
	
}
