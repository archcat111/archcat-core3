package org.cat.support.springboot3.starter.web.security.password;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.web.security.password")
@Getter
@Setter
public class PasswordProperties {
	private boolean enabled = false;
	
	private boolean bcrypt = false;
	private boolean pbkdf2 = false;
	private boolean scrypt = false;
	private boolean argon2 = false;
}
