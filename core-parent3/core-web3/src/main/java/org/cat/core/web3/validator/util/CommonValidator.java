package org.cat.core.web3.validator.util;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.constants.ControllerParamContants;

/**
 * 
 * @author 王云龙
 * @date 2018年1月16日 上午10:53:23
 * @version 1.0
 * @description 公用的数据校验及补偿器
 *
 */
public class CommonValidator {
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2018年1月16日 上午10:53:36
	 * @version 1.0
	 * @description 检验并补偿execute
	 *
	 * @param execute
	 */
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月16日 下午1:33:58
	 * @version 1.0
	 * @description 检验并补偿execute 
	 * @param execute	传入Controller的参数execute，用于表示需要返回基本结果还是复杂结果
	 * 		{@linkplain ControllerParamContants}
	 */
	public static void validExecute(String execute){
		if(StringUtils.isBlank(execute)){
			execute=ControllerParamContants.BASIC;
		}
		if(!execute.equals(ControllerParamContants.BASIC) && !execute.equals(ControllerParamContants.FULL)){
			execute=ControllerParamContants.BASIC;
		}
	}
}
