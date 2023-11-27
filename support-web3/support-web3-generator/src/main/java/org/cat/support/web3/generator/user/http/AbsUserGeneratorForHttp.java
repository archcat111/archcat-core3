package org.cat.support.web3.generator.user.http;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.cat.core.web3.user.IUserGenerator;

import com.google.common.collect.Maps;

public abstract class AbsUserGeneratorForHttp implements IUserGenerator {

	//key：userCode
	//value：header(code,userCode,user-code),cookie(code,userCode),session(userCode),requestParam(code,userCode)
	protected Map<String, List<ParamHttpParser>> holder = Maps.newHashMap();
		
	public String get(String paramName) {
		List<ParamHttpParser> paramHttpParserList = this.holder.get(paramName);
		if(paramHttpParserList == null) {
			return null;
		}
		String result = null;
		for (ParamHttpParser paramHttpParser : paramHttpParserList) {
			String tempResult = paramHttpParser.get();
			if(StringUtils.isNotBlank(tempResult)) {
				result = tempResult;
				break;
			}
		}
		return result;
	}

}
