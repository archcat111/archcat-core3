package org.cat.support.security3.generator.spring.login;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.cat.support.security3.generator.spring.user.RemoteAddressAuthDetails;
import org.springframework.security.core.SpringSecurityCoreVersion;

import lombok.Getter;

public class DynamicParameterAuthDetails extends RemoteAddressAuthDetails {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	
	@Getter private Map<String, String> otherParameters;
	@Getter private String passwordEncryption;

	public DynamicParameterAuthDetails(HttpServletRequest request, AuthRequestMatcher authRequestMatcher) {
		super(request);
		List<String> otherParameters = authRequestMatcher.getOtherParameters();
		if(otherParameters==null || otherParameters.size()==0) {
			otherParameters = null;
		}else {
			otherParameters.forEach(otherParameter -> {
				String otherParameterValue = request.getParameter(otherParameter);
				this.otherParameters.put(otherParameter, otherParameterValue);
			});
		}
		
		this.passwordEncryption = authRequestMatcher.getPasswordEncryption();
	}

}
