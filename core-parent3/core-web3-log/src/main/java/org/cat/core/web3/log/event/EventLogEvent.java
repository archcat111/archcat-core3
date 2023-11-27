package org.cat.core.web3.log.event;

import org.cat.core.web3.log.bean.EventLogBean;
import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author 王云龙
 * @date 2021年9月7日 下午5:36:25
 * @version 1.0
 * @description EventLog事件
 *
 */
@Getter
@Setter
@ToString
public class EventLogEvent extends ApplicationEvent {

	private static final long serialVersionUID = -965992832816346629L;
	
	private EventLogBean eventLogBean;

	public EventLogEvent(Object source, EventLogBean eventLogBean) {
		super(source);
		this.eventLogBean = eventLogBean;
	}

}
