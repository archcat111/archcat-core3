package org.cat.support.springboot3.actuator.info;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cat.core.web3.info.ArchInfoGenerator;
import org.cat.support.springboot3.actuator.ActuatorConstants;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;

/**
 * @author 王云龙
 * @date 2021年9月6日 下午3:41:15
 * @version 1.0
 * @description 
 * 		独立于Actuator自带的InfoEndpoint
 *
 */
@Endpoint(id = "info2")
public class Info2Endpoint {
	
	private final List<InfoContributor> infoContributors;
	private final ArchInfoGenerator archInfoProperties;
	
	private String activation = ActuatorConstants.Info.ARCH;
	
	public Info2Endpoint(List<InfoContributor> infoContributors) {
		Assert.notNull(infoContributors, "Info contributors must not be null");
		this.infoContributors = infoContributors;
		this.archInfoProperties = null;
		this.activation = ActuatorConstants.Info.DEFAULT;
	}
	
	public Info2Endpoint(ArchInfoGenerator archInfoProperties) {
		this.infoContributors = null;
		this.archInfoProperties = archInfoProperties;
		this.activation = ActuatorConstants.Info.ARCH;
	}
	
	@ReadOperation
	public Map<String, Object> info() {
		Info.Builder builder = new Info.Builder();
		if(ActuatorConstants.Info.DEFAULT.equals(this.activation)) {
			for (InfoContributor contributor : this.infoContributors) {
				contributor.contribute(builder);
			}
			Info build = builder.build();
			return build.getDetails();
		}else if(ActuatorConstants.Info.ARCH.equals(this.activation)) {
			
			Map<String, Object> infoMap = processLocalDateTimeValue(BeanUtil.beanToMap(this.archInfoProperties));
			builder.withDetail("本应用信息", infoMap);
		}else {
			builder.withDetail("error", "info2监控端点异常");
		}
		Info info = builder.build();
		return info.getDetails();
	}
	
	private Map<String, Object> processLocalDateTimeValue(Map<String, Object> infoMap) {
		Map<String, Object> newInfoMap = Maps.newHashMap();
		for (Entry<String, Object> entry : infoMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value instanceof LocalDateTime) {
				LocalDateTime localDateTimeValue = (LocalDateTime) value;
				String localDateTimeStr = LocalDateTimeUtil.format(localDateTimeValue, DatePattern.NORM_DATETIME_PATTERN);
				newInfoMap.put(key, localDateTimeStr);
			}else {
				newInfoMap.put(key, value);
			}
		}
		return newInfoMap;
	}
	
	public static void main(String[] args) {
		System.out.println(LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_PATTERN));
	}
}
