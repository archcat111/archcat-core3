package org.cat.support.springboot3.starter.storage.minio;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "cat.support3.storage.minio")
@Getter
@Setter
public class MinioProperties {
	private boolean enabled;
	
	private Map<String, MinioAccountProperties> accounts = Maps.newHashMap();
}
