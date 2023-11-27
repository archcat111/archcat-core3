package org.cat.support.springboot3.starter.storage.minio;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinioAccountProperties {
	private boolean enabled = true;
	private String url; //对应endpoint
	private String accessKey;
	private String secretKey;
	private List<String> validateBuckets = Lists.newArrayList(); //启动的时候会验证配置的bucket是否可用
}
