package org.cat.support.security3.generator.spring.core;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

/**
 * 
 * @author 王云龙
 * @date 2021年11月23日 下午2:42:49
 * @version 1.0
 * @description TODO
 *
 */
public class ComplexRequestMatcher<T extends RequestMatcher> implements RequestMatcher {

	private final List<T> requestMatchers;
	
	public ComplexRequestMatcher(List<T> requestMatchers) {
		Assert.notEmpty(requestMatchers, "requestMatchers must contain a value");
		Assert.isTrue(!requestMatchers.contains(null), "requestMatchers cannot contain null values");
		this.requestMatchers = requestMatchers;
	}
	
	@SuppressWarnings("unchecked")
	public ComplexRequestMatcher( T... requestMatchers) {
		this(Arrays.asList(requestMatchers));
	}
	
	@Override
	public boolean matches(HttpServletRequest request) {
		for (T matcher : this.requestMatchers) {
			if (matcher.matches(request)) {
				return true;
			}
		}
		return false;
	}
	
	public T getMatchReqeustMatcher(HttpServletRequest request) {
		for (T matcher : this.requestMatchers) {
			if (matcher.matches(request)) {
				return matcher;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "And " + this.requestMatchers;
	}

}
