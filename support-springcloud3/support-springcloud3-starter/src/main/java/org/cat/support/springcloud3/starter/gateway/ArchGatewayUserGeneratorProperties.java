package org.cat.support.springcloud3.starter.gateway;

import java.util.List;
import java.util.Map;

import org.cat.support.springcloud3.gateway.constants.ArchGatewayConstants.LoginConstants;
import org.cat.support.springcloud3.gateway.constants.ArchGatewayConstants.TransferAttrTypeConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.gateway.user-generator")
@Getter
@Setter
public class ArchGatewayUserGeneratorProperties {
	
	private int filterOrder = 5001;
	private List<String> excludeRouteIds = Lists.newArrayList();
	private List<String> excludeUris = Lists.newArrayList();
	
	//从哪里获取需要传递的信息
	public String storage = LoginConstants.LOGIN_STATUS_STORAGE_SESSION;
	//获取到信息后，通过何种方式向后端服务传递
	public String transferAttrType = TransferAttrTypeConstants.HEADER;
	//在截至中获取需要传递的信息时，介质中的信息叫什么名字
	private String userAttrName = "user";
	//需要从介质中获取到名字为上面userAttrName配置的值的json字符串后，需要获取哪些key，传递给后端服务时name叫什么
	//key：举例：从session中获取到userCode为111，这里配置为user-code，则会在header中写入user-code=111
	//value：举例：从session中获取到的用户信息是{code:111,userName:aaa,detail:{status:1}}
	////那么获取code，则这里配置value为code，获取detail的status，则这里配置value为detail.status
	private Map<String, String> transferAttrs; 
	
}
