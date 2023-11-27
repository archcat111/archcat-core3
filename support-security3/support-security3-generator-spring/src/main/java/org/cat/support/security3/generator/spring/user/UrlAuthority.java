package org.cat.support.security3.generator.spring.user;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UrlAuthority implements GrantedAuthority {

	private static final long serialVersionUID = 9114949982795149247L;
	
	private String code;
	private String type;
	private String url;
	private String data;

	@Override
	public String getAuthority() {
		StringBuilder stringBuilder=new StringBuilder();
		stringBuilder.append(url);
		stringBuilder.append(data);
		String authority=stringBuilder.toString();
		return authority;
	}
	
	@Override
	public String toString() {
		return "{\"code\":" + code + "\"type\":" + type + "\"url\":" + url + "\"data\":" + data + "}";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof UrlAuthority) {
			return code.equals(((UrlAuthority) obj).code);
		}

		return false;
	}

}
