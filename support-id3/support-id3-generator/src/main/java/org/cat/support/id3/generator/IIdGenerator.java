/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cat.support.id3.generator;


/**
 * 
 * @author 王云龙
 * @date 2021年10月14日 下午3:08:57
 * @version 1.0
 * @description id生成器的底层接口
 *
 */
public interface IIdGenerator {

	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年10月12日 下午2:30:28
	 * @version 1.0
	 * @description 获取一个唯一的id 
	 * @return
	 */
    long getLongId();
    
    /**
     * 
     * @author wangyunlong
     * @date 2021年10月13日 下午5:28:28
     * @version 1.0
     * @description 获取字符串格式的Id 
     * @return
     */
    String getStrId();

}
