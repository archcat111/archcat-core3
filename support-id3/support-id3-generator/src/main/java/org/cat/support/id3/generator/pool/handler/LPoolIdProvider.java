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
package org.cat.support.id3.generator.pool.handler;

import java.util.List;

/**
 * 
 * @author 王云龙
 * @date 2021年10月14日 下午2:50:28
 * @version 1.0
 * @description Pool Id Provider（支持Lambda），供1秒内的所有ID 
 *
 */
@FunctionalInterface
public interface LPoolIdProvider {

    /**
     * 
     * @author wangyunlong
     * @date 2021年10月14日 下午2:49:30
     * @version 1.0
     * @description 提供1秒内的所有ID 
     * @param currentSecond
     * @return
     */
    List<Long> provideIds(long currentSecond);
}
