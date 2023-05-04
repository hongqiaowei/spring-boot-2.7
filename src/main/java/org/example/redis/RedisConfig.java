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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Hong Qiaowei
 */
@Configuration
public class RedisConfig {

    /**
     * @param objectMapper 来自{@link org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration}
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<Object, Object> rt = new RedisTemplate<>();
        RedisSerializer<String> stringSerializer = RedisSerializer.string();
        rt.setDefaultSerializer(stringSerializer);
        rt.setKeySerializer    (stringSerializer);
        rt.setHashKeySerializer(stringSerializer);

        // 不用GenericJackson2JsonRedisSerializer，因为它附加类型信息，加大存储空间，而redis通常是系统中各逻辑共享的资源
        // 转换是，应用要手动把json转为实体，如 Person person = JacksonUtils.readValue(jsonFromRedis, Person.class);
        Jackson2JsonRedisSerializer<?> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jsonSerializer.setObjectMapper(objectMapper);

        rt.setValueSerializer    (jsonSerializer);
        rt.setHashValueSerializer(jsonSerializer);
        rt.setConnectionFactory  (redisConnectionFactory);
        return rt;
    }

    /**
     * @param threadPoolTaskExecutor 来自{@link org.example.config.TaskExecutorConfig}
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
                   RedisConnectionFactory connectionFactory,
                   @Qualifier(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME) ThreadPoolTaskExecutor threadPoolTaskExecutor
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setTaskExecutor(threadPoolTaskExecutor);
        return container;
    }
}
