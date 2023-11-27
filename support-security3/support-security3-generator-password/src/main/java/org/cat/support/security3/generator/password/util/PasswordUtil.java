package org.cat.support.security3.generator.password.util;

import org.cat.support.security3.generator.password.constants.PasswordConstants;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

public class PasswordUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年4月8日 下午5:47:21
	 * @version 1.0
	 * @description 判断当前的加密实现类为哪种加密方式 
	 * @param <T>
	 * @param clazz 当前的加密实现类的Class
	 * @return
	 */
	public static < T extends PasswordEncoder> String judgeEncryption(Class<T> clazz) {
		if(BCryptPasswordEncoder.class.isAssignableFrom(clazz)) {
			return PasswordConstants.Encryption.BCRYPT;
		}
		if(Pbkdf2PasswordEncoder.class.isAssignableFrom(clazz)) {
			return PasswordConstants.Encryption.PBKDF2;
		}
		if(SCryptPasswordEncoder.class.isAssignableFrom(clazz)) {
			return PasswordConstants.Encryption.SCRYPT;
		}
		if(Argon2PasswordEncoder.class.isAssignableFrom(clazz)) {
			return PasswordConstants.Encryption.ARGON2;
		}
		return PasswordConstants.Encryption.UNKNOWN;
	}
}
