package org.cat.support.springboot3.starter.web.security.password;

import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.cat.support.security3.generator.password.PasswordEncoderHolder;
import org.cat.support.security3.generator.password.SupportPassword3ConditionalFlag;
import org.cat.support.security3.generator.password.constants.PasswordConstants;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

@Configuration
@ConditionalOnClass({PasswordEncoder.class, SupportPassword3ConditionalFlag.class})
@ConditionalOnProperty(prefix = "cat.support3.web.security.password", name = "enabled",  havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(value = {PasswordProperties.class})
public class PasswordAutoConfiguration {

	protected final transient Logger coreLogger = ArchLoggerFactory.getCoreLogger();
	
	@Autowired
	private PasswordProperties passwordProperties;
	
	@Bean
	public PasswordEncoderHolder passwordEncoderHolder() {
		PasswordEncoderHolder passwordEncoderHolder = new PasswordEncoderHolder();
		
		if(this.passwordProperties.isBcrypt()) {
			PasswordEncoder passwordEncoder = bcryptPasswordEncoder();
			passwordEncoderHolder.put(PasswordConstants.Encryption.BCRYPT, passwordEncoder);
		}
		if(this.passwordProperties.isPbkdf2()) {
			PasswordEncoder passwordEncoder = pbkdf2PasswordEncoder();
			passwordEncoderHolder.put(PasswordConstants.Encryption.PBKDF2, passwordEncoder);
		}
		if(this.passwordProperties.isScrypt()) {
			PasswordEncoder passwordEncoder = scryptPasswordEncoder();
			passwordEncoderHolder.put(PasswordConstants.Encryption.SCRYPT, passwordEncoder);
		}
		if(this.passwordProperties.isArgon2()) {
			PasswordEncoder passwordEncoder = argon2PasswordEncoder();
			passwordEncoderHolder.put(PasswordConstants.Encryption.ARGON2, passwordEncoder);
		}
		
		return passwordEncoderHolder;
	}
	
	@Primary
	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoderHolder passwordEncoderHolder = passwordEncoderHolder();
		
		PasswordEncoder passwordEncoder = passwordEncoderHolder.get(PasswordConstants.Encryption.BCRYPT);
		if(passwordEncoder == null) {
			passwordEncoder = passwordEncoderHolder.get(PasswordConstants.Encryption.PBKDF2);
		}
		if(passwordEncoder == null) {
			passwordEncoder = passwordEncoderHolder.get(PasswordConstants.Encryption.SCRYPT);
		}
		if(passwordEncoder == null) {
			passwordEncoder = passwordEncoderHolder.get(PasswordConstants.Encryption.ARGON2);
		}
		
		return passwordEncoder;
	}
	
	@ConditionalOnProperty(prefix = "cat.support3.web.security.password", name = "bcrypt",  havingValue = "true", matchIfMissing = false)
	@Bean
	public PasswordEncoder bcryptPasswordEncoder() {
		//SHA-256 + 随机盐 + 密钥 对密码进行加密
		BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
		return bcryptPasswordEncoder;
	}
	
	@ConditionalOnProperty(prefix = "cat.support3.web.security.password", name = "pbkdf2",  havingValue = "true", matchIfMissing = false)
	@Bean
	public PasswordEncoder pbkdf2PasswordEncoder() {
		Pbkdf2PasswordEncoder pbkdf2PasswordEncoder = new Pbkdf2PasswordEncoder();
		return pbkdf2PasswordEncoder;
	}
	
	@ConditionalOnProperty(prefix = "cat.support3.web.security.password", name = "scrypt",  havingValue = "true", matchIfMissing = false)
	@Bean
	public PasswordEncoder scryptPasswordEncoder() {
		SCryptPasswordEncoder scryptPasswordEncoder = new SCryptPasswordEncoder();
		return scryptPasswordEncoder;
	}
	
	@ConditionalOnProperty(prefix = "cat.support3.web.security.password", name = "pbkdf2",  havingValue = "true", matchIfMissing = false)
	@Bean
	public PasswordEncoder argon2PasswordEncoder() {
		Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();
		return argon2PasswordEncoder;
	}
}
