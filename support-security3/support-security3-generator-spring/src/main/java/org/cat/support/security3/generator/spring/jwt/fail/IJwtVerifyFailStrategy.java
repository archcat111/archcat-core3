package org.cat.support.security3.generator.spring.jwt.fail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.exception.SecurityJwtVerifyException;

/**
 * 
 * @author 王云龙
 * @date 2021年12月3日 上午10:34:34
 * @version 1.0
 * @description jwt验证失败
 *
 */
public interface IJwtVerifyFailStrategy {
	void onVerifyFailure(HttpServletRequest request, HttpServletResponse response,
			SecurityJwtVerifyException exception) throws IOException, ServletException;
}
