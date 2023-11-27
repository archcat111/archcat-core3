package org.cat.core.web3.info;

import java.time.LocalDateTime;

import org.cat.core.web3.constants.ArchConstants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArchInfoGenerator {
	private String company = "未填写内容";
	private String platform = "未填写内容";
	private String framework = ArchConstants.Framework.UNKNOWN;
	private String frameworkSub = ArchConstants.FrameworkSub.UNKNOWN;
	private String principal="未填写内容";
	private String principalEmail="未填写内容";
	
	private String hostName;
	private String hostIp;
	private String port;
	private String appCode;
	private String appName;
	private String appVersion;
	private String appDescription;
	
	private LocalDateTime buildTime;
	
	private String gitBranch;
	private String gitCommitId;
	private LocalDateTime gitCommitTime;
	
	private LocalDateTime appStartTime;
	private LocalDateTime appRunningTime;
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
}
