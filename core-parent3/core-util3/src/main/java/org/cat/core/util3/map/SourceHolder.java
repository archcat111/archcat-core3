package org.cat.core.util3.map;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

/**
 * 
 * @author 王云龙
 * @date 2021年9月17日 下午4:26:58
 * @version 1.0
 * @description 资源持有者
 *
 * @param <T> 资源的Class类型
 */
public class SourceHolder<T extends Object> {
	//该Map的Put以及Get理论上只有框架内部的Starter会单线程注入
	private Map<String, T> sourceMapper = Maps.newHashMap();
	
	public void put(String name, T source) {
		this.sourceMapper.put(name, source);
	}
	
	public T get(String name) {
		return this.sourceMapper.get(name);
	}
	
	public String get(T t) {
		for (Entry<String, T> entry : this.sourceMapper.entrySet()) {
			if(entry.getValue()==t) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	protected Map<String, T> getSelfSourceMapper(){
		return this.sourceMapper;
	}
	
}
