package org.cat.support.security3.generator.spring.user;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import lombok.Getter;

public class RemoteAddressAuthDetails implements Serializable {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	
	@Getter final String remoteAddress;
	
	public RemoteAddressAuthDetails(HttpServletRequest request) {
		this.remoteAddress = request.getRemoteAddr();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WebAuthenticationDetails) {
			WebAuthenticationDetails other = (WebAuthenticationDetails) obj;
			if ((this.remoteAddress == null) && (other.getRemoteAddress() != null)) {
				return false;
			}
			if ((this.remoteAddress != null) && (other.getRemoteAddress() == null)) {
				return false;
			}
			if (this.remoteAddress != null) {
				if (!this.remoteAddress.equals(other.getRemoteAddress())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int code = 7654;
		if (this.remoteAddress != null) {
			code = code * (this.remoteAddress.hashCode() % 7);
		}
		return code;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(" [");
		sb.append("RemoteIpAddress=").append(this.getRemoteAddress()).append(", ");
		return sb.toString();
	}
}
