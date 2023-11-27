package org.cat.support.springcloud3.starter.feign;

import org.cat.core.web3.constants.SessionConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchUsergeneratorReqInterceptorFeignProperties {
	private boolean enabled;
	private String sessionAttrName = SessionConstants.ATTR_NAME_DEFAULT;
}
