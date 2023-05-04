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

package org.example.redis.lock;

import org.example.config.TaskExecutorConfig;
import org.example.redis.RedisConfig;
import org.example.spel.SpELConfig;
import org.example.util.ThreadContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.annotation.Resource;

/**
 * @author Hong Qiaowei
 */
@SpringJUnitConfig(
        classes = {
            JacksonAutoConfiguration.class,
            AopAutoConfiguration.class,
            RedisAutoConfiguration.class,
            TaskExecutionAutoConfiguration.class,
            TaskExecutorConfig.class,
            RedisConfig.class,
            SpELConfig.class,
            RedisLockAnnotationTests.ContextConfig.class
        }
)
@TestPropertySource("/redis.properties")
public class RedisLockAnnotationTests {

    @Configuration
    @ComponentScan(basePackages = "org.example.redis.lock")
    static class ContextConfig {

        @Service
        static class AService {

            @Resource
            StringRedisTemplate stringRedisTemplate;

            @RedisLock(name = "aservice", param = "#p0")
            public void b(String c) {
                String key = "aservice:" + c;
                String value = stringRedisTemplate.opsForValue().get(key);
                String threadGlobalId = ThreadContext.globalThreadId();
                Assertions.assertEquals(threadGlobalId, value);
            }
        }
    }

    @Resource
    ContextConfig.AService aService;

    @Test
    public void test() {
        aService.b("c");
    }
}
