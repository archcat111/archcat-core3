package org.cat.support.id3.generator.snowflake;

import org.cat.support.id3.generator.IIdGenerator;

/**
 * 
 * @author 王云龙
 * @date 2021年10月12日 下午3:16:17
 * @version 1.0
 * @description Snowflake的Id生成标准接口
 *
 */
public interface IIdSnowflakeGenerator extends IIdGenerator{
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月12日 下午3:15:00
	 * @version 1.0
	 * @description 将id解析为形成该id的各个元素的json
	 * @param id
	 * @return
	 */
	public String parseId(long id);
	
}
