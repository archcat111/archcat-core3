package org.cat.support.springboot3.starter.web.log.audit;

import java.util.List;

import org.cat.support.log3.generator.constants.LogSupportConstants;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebLogAuditProperties {
	private boolean enabled = false;
	
	private String idGeneratorName = LogSupportConstants.IdGenerator.NO_USE;	//即：在cat.support3.id中配置的id生成器的名称
	/**
	 * {@linkplain LogSupportConstants.Out}
	 */
	private List<String> out = Lists.newArrayList(LogSupportConstants.Out.LOCAL); //支持local、mysql、appender、udp、kafka
	
}
