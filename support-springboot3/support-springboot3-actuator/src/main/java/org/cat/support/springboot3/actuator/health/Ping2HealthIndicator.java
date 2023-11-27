package org.cat.support.springboot3.actuator.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.Status;

/**
 * @author 王云龙
 * @date 2021年9月7日 上午10:03:37
 * @version 1.0
 * @description 一个用于验证Health检查器是否正常工作的HealthIndicator
 * 		注意：该HealthIndicator如果正常返回result，可以说明：
 * 			1：该应用启动正常
 * 			2：HealthIndicator以及自定义HealthIndicator响应正常
 * 			但是不能说明Controller or Feign一定可以正常提供服务
 * 			如果为了说明Controller or Feign可以正常提供服务，请参考：
 * 			WebMvcTestPingProperties、WebMvcAutoConfiguration(arch的)
 * 
 * 			spring-boot-actuator-2.5.4.jar有一个PingHealthIndicator
 * 			当配置{@code management.health.ping.enabled} or {@code management.health.defaults.enabled}
 * 			的值为true时，会起作用
 * 			PingHealthIndicator中只有builder.up()，但是不能返回自定义的结果
 *
 */
public class Ping2HealthIndicator extends AbstractHealthIndicator {
	
	private String result = "pong2(HealthIndicator)";
	
	public Ping2HealthIndicator() {
	}

	public Ping2HealthIndicator(String result) {
		this.result = result;
	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
//		builder.down().withDetail("状态", "异常");
//		builder.down(new XxxException()).withDetail("状态", "异常");
//		builder.status(Status.OUT_OF_SERVICE).withDetail("xxx", "xxx");
		builder.status(Status.UP);
		builder.up().withDetail("result", this.result);
	}

}
