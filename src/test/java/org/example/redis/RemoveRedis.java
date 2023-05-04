/*
 *  Copyright (C) 2023 Hong Qiaowei <hongqiaowei@163.com>. All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.example.redis;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 用于在单元测试中屏蔽redis相关功能
 * @author Hong Qiaowei
 */
@TestConfiguration
public class RemoveRedis {

    @MockBean
    RedisConnectionFactory redisConnectionFactory;

    @MockBean
    RedisTemplate<Object, Object> redisTemplate;

    @MockBean
    StringRedisTemplate stringRedisTemplate;

    /**
     * 替换RedisConfig中的redisMessageListenerContainer，那个会真的连redis server
     */
    @MockBean
    RedisMessageListenerContainer redisMessageListenerContainer;
}
