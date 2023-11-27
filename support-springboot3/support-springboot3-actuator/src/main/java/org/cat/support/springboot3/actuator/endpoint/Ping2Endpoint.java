package org.cat.support.springboot3.actuator.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

/**
 * @author 王云龙
 * @date 2021年9月6日 下午3:41:15
 * @version 1.0
 * @description 一个用于验证自定义Endpoint可用的Endpoint
 * 		注意：该Endpoint如果正常返回result，可以说明：
 * 			1：该应用启动正常
 * 			2：Endpoint以及自定义Endpoint响应正常
 * 			但是不能说明Controller or Feign一定可以正常提供服务
 * 			如果为了说明Controller or Feign可以正常提供服务，请参考：
 * 			WebMvcTestPingProperties、WebMvcAutoConfiguration(arch框架的)
 *
 */
@Endpoint(id = "ping2")
public class Ping2Endpoint {
	
	private String result = "pong2(Endpoint)";
	
	public Ping2Endpoint() {
	}

	public Ping2Endpoint(String result) {
		this.result = result;
	}

//	@ReadOperation
//	public String demo(@Selector String demoParam){
//		Map<String, String> result = Maps.newHashMap();
//		result.put("demo1", "demoResult1");
//		return result;
//	}
	
	@ReadOperation
	public String result(){
		return this.result;
	}
}
