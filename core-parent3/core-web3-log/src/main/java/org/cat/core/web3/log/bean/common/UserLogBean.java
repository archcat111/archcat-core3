package org.cat.core.web3.log.bean.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2021年8月17日 下午3:12:25
 * @version 1.0
 * @description Log中打印的用户信息Bean，可能会被多种不同的日志使用。比如：用户链路追踪日志、Event日志
 *
 */
@Getter
@Setter
public class UserLogBean implements Serializable {

	private static final long serialVersionUID = -184270186661885167L;
	
	@JsonProperty(value = "user_code")
	private String userCode;
	
	@JsonProperty(value = "session_id")
	private String sessionId;
	
	@JsonProperty(value = "client_ip")
	private String clientIp;
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月25日 下午3:07:28
	 * @version 1.0
	 * @description 创建一个 UserLogBean对象
	 * @param userCode
	 * @param sessionId
	 * @param clientIp
	 * @return
	 */
	public static UserLogBean createUserLogBean(String userCode, String sessionId, String clientIp) {
		UserLogBean userLogBean = new UserLogBean();
		userLogBean.setUserCode(userCode);
		userLogBean.setSessionId(sessionId);
		userLogBean.setClientIp(clientIp);
		return userLogBean;
	}

}
