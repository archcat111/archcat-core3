package org.cat.support.security3.generator.spring.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cat.support.security3.generator.spring.constants.SecurityLoginConstants;
import org.cat.support.security3.generator.spring.core.ComplexRequestMatcher;
import org.cat.support.security3.generator.spring.user.PasswordAuthToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * 
 * @author 王云龙
 * @date 2021年11月23日 上午10:59:19
 * @version 1.0
 * @description 
 * 		requiresAuthenticationRequestMatcher：必须，用来判断是否是登录请求
 * 		authenticationManager：必须，登录管理器，用来调用登录验证
 * 		attemptAuthentication()：验证过程，返回一个Authentication
 * 		authenticationDetailsSource：默认WebAuthenticationDetailsSource。用于填充AuthenticationToken的Details
 * 		sessionStrategy：默认NullAuthenticatedSessionStrategy。不进行session校验
 * 		continueChainBeforeSuccessfulAuthentication：默认false。在认证成功并且session校验完成后，不执行后续filterChain，而是直接交给successHandler处理
 * 		rememberMeServices：默认NullRememberMeServices。不记住用户
 * 		eventPublisher：默认null。发送认证成功的InteractiveAuthenticationSuccessEvent
 * 		successHandler：默认SavedRequestAwareAuthenticationSuccessHandler
 * 		failureHandler：SimpleUrlAuthenticationFailureHandler
 * 
 * 		messages：默认MessageSourceAccessor(new SpringSecurityMessageSource())。用于国际化
 * 		allowSessionCreation：默认true，是否允许创建session，貌似没用到
 *
 */
public class ComplexAuthFilter extends AbstractAuthenticationProcessingFilter {
	
	private ComplexRequestMatcher<AuthRequestMatcher> complexAuthRequestMatcher;
	//覆盖父类AbstractAuthenticationProcessingFilter的authenticationDetailsSource
	protected DynamicParameterAuthDetailsSource authenticationDetailsSource = new DynamicParameterAuthDetailsSource();
	
	public ComplexAuthFilter(ComplexRequestMatcher<AuthRequestMatcher> complexAuthRequestMatcher,
			AuthenticationManager authenticationManager) {
		super(complexAuthRequestMatcher, authenticationManager);
		this.complexAuthRequestMatcher = complexAuthRequestMatcher;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		//获取本次登录url+method配置的相应参数，根据参数执行后续操作
		AuthRequestMatcher authRequestMatcher = complexAuthRequestMatcher.getMatchReqeustMatcher(request);
		String loginWay = authRequestMatcher.getWay();
		
		//根据登录方式创建Authentication
		AbstractAuthenticationToken authenticationToken = null;
		switch (loginWay) {
		case SecurityLoginConstants.Way.USERNAME_AND_PASSWORD: //用户名和密码的方式进行登录
			authenticationToken = authenticationByPassword(request, authRequestMatcher);
			break;
//		case SecurityLoginConstants.Way.SMS:
//			//TODO
		default:
			throw new ServletException("没有找到匹配的登录方式LoginWay，您配置的LoginWay为"+loginWay);
		}
		
		//获取详情
		setDetails(request, authRequestMatcher, authenticationToken);
		
		//登录校验
		AuthenticationManager authenticationManager = getAuthenticationManager();
		Authentication authenticationResult = authenticationManager.authenticate(authenticationToken);
		
		//返回结果
		return authenticationResult;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年11月23日 下午3:07:38
	 * @version 1.0
	 * @description 以用户名和密码的方式进行登录时，组装一个 UsernamePasswordAuthenticationToken
	 * @param request
	 * @return
	 */
	protected AbstractAuthenticationToken authenticationByPassword(HttpServletRequest request, AuthRequestMatcher authRequestMatcher) {
		//获取用户名和密码
		String userName = obtainUsername(request, authRequestMatcher.getUsernameParameter());
		String password = obtainPassword(request, authRequestMatcher.getPasswordParameter());
		PasswordAuthToken passwordAuthToken = new PasswordAuthToken(userName, password);
		
		return passwordAuthToken;
	}
	
	protected String obtainUsername(HttpServletRequest request, String userNameParameter) {
		String userName = request.getParameter(userNameParameter);
		userName = (userName != null) ? userName : "";
		userName = userName.trim();
		return userName;
	}
	
	protected String obtainPassword(HttpServletRequest request, String passwordParameter) {
		String password = request.getParameter(passwordParameter);
		password = (password != null) ? password : "";
		return password;
	}
	
	protected void setDetails(HttpServletRequest request, AuthRequestMatcher authRequestMatcher, AbstractAuthenticationToken authenticationToken) {
		if(this.authenticationDetailsSource!=null) {
			this.authenticationDetailsSource.setAuthRequestMatcher(authRequestMatcher);
			authenticationToken.setDetails(this.authenticationDetailsSource.buildDetails(request));
		}
	}

	public void setAuthenticationDetailsSource(DynamicParameterAuthDetailsSource authenticationDetailsSource) {
		this.authenticationDetailsSource = authenticationDetailsSource;
	}
	


}
