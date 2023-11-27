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
public class UrlAuthorityResp extends AbsArchJsonResp {

	private static final long serialVersionUID = 8707979304157436245L;
	
	private String code;
	private String type;
	private String url;
	private String data;

}
