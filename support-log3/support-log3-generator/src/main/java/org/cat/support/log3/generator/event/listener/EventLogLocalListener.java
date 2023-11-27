package org.cat.support.log3.generator.event.listener;

import org.cat.core.util3.json.ArchJsonUtil;
import org.cat.core.web3.log.bean.EventLogBean;
import org.cat.core.web3.log.event.EventLogEvent;
import org.cat.core.web3.log.id.ArchLoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationListener;

/**
 * 
 * @author 王云龙
 * @date 2021年9月9日 上午11:05:56
 * @version 1.0
 * @description 将EventLog打印在本地的配置
 *
 */
public class EventLogLocalListener implements ApplicationListener<EventLogEvent> {

	protected final transient Logger eventLogger = ArchLoggerFactory.getEventLogger();
	
	@Override
	public void onApplicationEvent(EventLogEvent event) {
		EventLogBean eventLogBean = event.getEventLogBean();
		this.eventLogger.info(ArchJsonUtil.toJson(eventLogBean));
	}

}
