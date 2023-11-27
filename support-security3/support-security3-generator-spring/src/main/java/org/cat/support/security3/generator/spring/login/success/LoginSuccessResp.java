package org.cat.support.security3.generator.spring.login.success;

import java.util.List;
import java.util.Map;

import org.cat.core.web3.resp.AbsArchJsonResp;
import org.cat.support.security3.generator.spring.user.resp.RoleResp;
import org.cat.support.security3.generator.spring.user.resp.UrlAuthorityResp;

import com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessResp extends AbsArchJsonResp {
	
	private static final long serialVersionUID = 5228489674864380116L;
	
	private String code;
	private String nickName;
	private String userName;
	private boolean accountExpired;
	private boolean accountLocked;
	private boolean credentialsExpired;
	
	List<RoleResp> roles;
	List<UrlAuthorityResp> authorites;
	
	private Map<String, String> other = Maps.newHashMap();

}
