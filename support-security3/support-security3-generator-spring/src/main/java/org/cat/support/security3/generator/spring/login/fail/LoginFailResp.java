package org.cat.support.security3.generator.spring.login.fail;

import java.util.Map;

import org.cat.core.web3.resp.AbsArchJsonResp;

import com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginFailResp extends AbsArchJsonResp {
	
	private static final long serialVersionUID = 5228489674864380116L;
	
	private Map<String, String> other = Maps.newHashMap();

}
