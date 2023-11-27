package org.cat.support.security3.generator.spring.user.resp;

import org.cat.core.web3.resp.AbsArchJsonResp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResp extends AbsArchJsonResp {
	
	private static final long serialVersionUID = -5243241264996962351L;
	
	private String code;
	private String name;
}
