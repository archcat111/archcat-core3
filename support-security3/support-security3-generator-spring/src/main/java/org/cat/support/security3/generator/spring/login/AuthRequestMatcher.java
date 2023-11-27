package org.cat.support.security3.generator.spring.login;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.cat.support.security3.generator.password.constants.PasswordConstants;
import org.cat.support.security3.generator.spring.constants.SecurityLoginConstants;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年11月23日 下午2:45:56
 * @version 1.0
 * @description TODO
 *
 */
public class AuthRequestMatcher implements RequestMatcher {
	
	private static final String MATCH_ALL = "/**";
	private final Matcher matcher;
	private final String pattern;
	private final HttpMethod httpMethod;
	private final UrlPathHelper urlPathHelper;
	
	@Getter @Setter private String way = SecurityLoginConstants.Way.USERNAME_AND_PASSWORD;
	@Getter @Setter private String requestWay = SecurityLoginConstants.RequestWay.FORM;
	
	//USERNAME_AND_PASSWORD
	@Getter @Setter private String usernameParameter = "userName";
	@Getter @Setter private String passwordParameter = "password";
	@Getter @Setter private String passwordEncryption = PasswordConstants.Encryption.BCRYPT;
	
	@Getter @Setter private List<String> otherParameters = Lists.newArrayList(); //<parameterName1>、<parameterName2>、...
	@Getter @Setter private boolean hideUserNotFoundExceptions = true; //是否隐藏登录失败抛出的异常细节，安全问题
	
	@Getter @Setter private Success success;
	@Getter @Setter private Fail fail;
	
	@Getter
	@Setter
	public class Success {
		private String defaultTargetUrl = "/"; //登录成功后的跳转url
		private boolean alwaysUseDefaultTargetUrl = false; //登录成功后是否总是跳转到defaultTargetUrl
		private String targetUrlParameter = "targetUrl"; //如果需要通过参数来控制跳转的页面，则会从该参数获取登录成功后的跳转url
		private boolean useReferer = false; //如果alwaysUseDefaultTargetUrl=false && <targetUrlParameter>对应的value没有值，则尝试从referer中获取要跳转的页面
		private Map<String, String> roleTargetUrl;//key为roleName，value为跳转的url
	}
	
	@Getter
	@Setter
	public class Fail {
		private String defaultTargetUrl = "/"; //登录失败后的跳转url
	}
	
	
	public AuthRequestMatcher(String pattern) {
		this(pattern, null);
	}
	
	public AuthRequestMatcher(String pattern, String httpMethod) {
		this(pattern, httpMethod, true);
	}
	
	public AuthRequestMatcher(String pattern, String httpMethod, boolean caseSensitive) {
		this(pattern, httpMethod, caseSensitive, null);
	}
	
	public AuthRequestMatcher(String pattern, String httpMethod, boolean caseSensitive,
			UrlPathHelper urlPathHelper) {
		Assert.hasText(pattern, "Pattern cannot be null or empty");
		if (pattern.equals(MATCH_ALL) || pattern.equals("**")) {
			pattern = MATCH_ALL;
			this.matcher = null;
		}
		else {
			// If the pattern ends with {@code /**} and has no other wildcards or path
			// variables, then optimize to a sub-path match
			if (pattern.endsWith(MATCH_ALL)
					&& (pattern.indexOf('?') == -1 && pattern.indexOf('{') == -1 && pattern.indexOf('}') == -1)
					&& pattern.indexOf("*") == pattern.length() - 2) {
				this.matcher = new SubpathMatcher(pattern.substring(0, pattern.length() - 3), caseSensitive);
			}
			else {
				this.matcher = new SpringAntMatcher(pattern, caseSensitive);
			}
		}
		this.pattern = pattern;
		this.httpMethod = StringUtils.hasText(httpMethod) ? HttpMethod.valueOf(httpMethod) : null;
		this.urlPathHelper = urlPathHelper;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (this.httpMethod != null && StringUtils.hasText(request.getMethod())
				&& this.httpMethod != HttpMethod.resolve(request.getMethod())) {
			return false;
		}
		if (this.pattern.equals(MATCH_ALL)) {
			return true;
		}
		String url = getRequestPath(request);
		return this.matcher.matches(url);
	}
	
	private String getRequestPath(HttpServletRequest request) {
		if (this.urlPathHelper != null) {
			return this.urlPathHelper.getPathWithinApplication(request);
		}
		String url = request.getServletPath();
		String pathInfo = request.getPathInfo();
		if (pathInfo != null) {
			url = StringUtils.hasLength(url) ? url + pathInfo : pathInfo;
		}
		return url;
	}
	
	
	
	private interface Matcher {

		boolean matches(String path);

		Map<String, String> extractUriTemplateVariables(String path);

	}
	
	private static final class SpringAntMatcher implements Matcher {

		private final AntPathMatcher antMatcher;

		private final String pattern;

		private SpringAntMatcher(String pattern, boolean caseSensitive) {
			this.pattern = pattern;
			this.antMatcher = createMatcher(caseSensitive);
		}

		@Override
		public boolean matches(String path) {
			return this.antMatcher.match(this.pattern, path);
		}

		@Override
		public Map<String, String> extractUriTemplateVariables(String path) {
			return this.antMatcher.extractUriTemplateVariables(this.pattern, path);
		}

		private static AntPathMatcher createMatcher(boolean caseSensitive) {
			AntPathMatcher matcher = new AntPathMatcher();
			matcher.setTrimTokens(false);
			matcher.setCaseSensitive(caseSensitive);
			return matcher;
		}

	}
	
	private static final class SubpathMatcher implements Matcher {

		private final String subpath;

		private final int length;

		private final boolean caseSensitive;

		private SubpathMatcher(String subpath, boolean caseSensitive) {
			Assert.isTrue(!subpath.contains("*"), "subpath cannot contain \"*\"");
			this.subpath = caseSensitive ? subpath : subpath.toLowerCase();
			this.length = subpath.length();
			this.caseSensitive = caseSensitive;
		}

		@Override
		public boolean matches(String path) {
			if (!this.caseSensitive) {
				path = path.toLowerCase();
			}
			return path.startsWith(this.subpath) && (path.length() == this.length || path.charAt(this.length) == '/');
		}

		@Override
		public Map<String, String> extractUriTemplateVariables(String path) {
			return Collections.emptyMap();
		}

	}

}
