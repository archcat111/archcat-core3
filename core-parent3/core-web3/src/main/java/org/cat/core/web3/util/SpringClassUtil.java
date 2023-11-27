package org.cat.core.web3.util;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class SpringClassUtil {
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年8月16日 上午10:50:04
	 * @version 1.0
	 * @description 判断class是否存在 
	 * @return
	 */
	public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
		boolean result = ClassUtils.isPresent(className, classLoader);
		return result;
	}
}
