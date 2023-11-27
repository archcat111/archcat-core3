package org.cat.support.springboot3.starter.web.session;

import org.cat.core.web3.constants.SessionConstants;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author 王云龙
 * @date 2022年8月2日 下午5:05:30
 * @version 1.0
 * @description 使用Header传递SessionId实现session时的配置文件
 *
 */
@Getter
@Setter
public class WebSessionHeaderProperties {
	
	private String sessionAttrName = SessionConstants.ATTR_NAME_DEFAULT;
	
}
