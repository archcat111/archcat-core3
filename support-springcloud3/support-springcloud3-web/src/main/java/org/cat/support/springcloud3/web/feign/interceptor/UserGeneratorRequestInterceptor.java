package org.cat.support.springcloud3.web.feign.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.util3.string.ArchCharsets2;
import org.cat.core.web3.constants.SessionConstants;
import org.cat.core.web3.user.IUserGenerator;
import org.cat.core.web3.util.SpringReqAndRespUtil;
import org.cat.support.web3.generator.contants.UserConstants;

import cn.hutool.core.net.URLEncodeUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 
 * @author 王云龙
 * @date 2022年8月15日 下午11:34:50
 * @version 1.0
 * @description 用于从当前request中获取userGerenator需要的一些参数加入header，传递给其他服务
 * 		例如：本方法接受到了SESSION_ID、userCode等header信息，则取出来，调用其他服务的feign时也会set到header中
 * 		如果工程没有引入该jar，但是配置里有，会报错
 *
 */
public class UserGeneratorRequestInterceptor implements RequestInterceptor {

	//如果ArchFeignAutoConfiguration没有开启UserGeneratorRequestInterceptor并且没有初始化该实例
	//但是在配置文件中统一配置了Feign的拦截器为该类
	//那么FeignClientFactoryBean也会自动初始化该实例，因此这里配置该开关，默认情况下该实例会开启，但是没作用
	private boolean powerSwitch = false;
	
	private String sessionAttrName = SessionConstants.ATTR_NAME_DEFAULT;
	private IUserGenerator userGenerator;
	
	public void openSwitch() {
		this.powerSwitch = true;
	}

	public void setSessionAttrName(String sessionAttrName) {
		this.sessionAttrName = sessionAttrName;
	}

	public void setUserGenerator(IUserGenerator userGenerator) {
		this.userGenerator = userGenerator;
	}

	@Override
	public void apply(RequestTemplate template) {
		if(!powerSwitch) {
			return;
		}
		HttpServletRequest httpServletRequest = SpringReqAndRespUtil.getHttpServletRequest();
		String sessionId = httpServletRequest.getHeader(sessionAttrName);
		if(StringUtils.isNotBlank(sessionId)) {
			addHeader(template, sessionAttrName, sessionId);
		}
		if(userGenerator!=null) {
			String userCode = userGenerator.getUserCode();
			if(StringUtils.isNotBlank(userCode)) {
				String encodeUserCode = URLEncodeUtil.encode(userCode, ArchCharsets2.UTF_8);
				addHeader(template, UserConstants.ParamName.USER_CODE, encodeUserCode);
			}
			String userName = userGenerator.getUserName();
			if(StringUtils.isNotBlank(userName)) {
				String encodeUserName = URLEncodeUtil.encode(userName, ArchCharsets2.UTF_8);
				addHeader(template, UserConstants.ParamName.USER_NAME, encodeUserName);
			}
			String nickName = userGenerator.getNickName();
			if(StringUtils.isNotBlank(nickName)) {
				String encodeNickName = URLEncodeUtil.encode(nickName, ArchCharsets2.UTF_8);
				addHeader(template, UserConstants.ParamName.NICK_NAME, encodeNickName);
			}
			String device = userGenerator.getDevice();
			if(StringUtils.isNotBlank(device)) {
				String encodeDevice = URLEncodeUtil.encode(device, ArchCharsets2.UTF_8);
				addHeader(template, UserConstants.ParamName.DEVICE, encodeDevice);
			}
		}

	}
	
	protected void addHeader(RequestTemplate requestTemplate, String name, String... values) {

		if (!requestTemplate.headers().containsKey(name)) {
			requestTemplate.header(name, values);
		}
	}

}
