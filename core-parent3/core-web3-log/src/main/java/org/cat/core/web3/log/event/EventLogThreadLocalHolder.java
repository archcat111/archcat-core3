package org.cat.core.web3.log.event;

import org.cat.core.web3.log.bean.EventLogBean;

public class EventLogThreadLocalHolder {
	private static ThreadLocal<EventLogBean> eventLogBeanThreadLocal = new ThreadLocal<>();
	
	public static EventLogBean get() {
		return eventLogBeanThreadLocal.get();
	}
	
	public static void set(EventLogBean eventLogBean) {
		eventLogBeanThreadLocal.set(eventLogBean);
	}
}
