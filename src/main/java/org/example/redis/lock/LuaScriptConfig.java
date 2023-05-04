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

import org.example.util.ResourcePatternUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author Hong Qiaowei
 */
@Configuration
public class LuaScriptConfig {

    @Bean
    public RedisScript<Long> lockScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(ResourcePatternUtils.getResource("classpath:org/example/redis/lock/lock.lua")));
        return redisScript;
    }

    @Bean
    public RedisScript<Long> unlockScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(ResourcePatternUtils.getResource("classpath:org/example/redis/lock/unlock.lua")));
        return redisScript;
    }

    @Bean
    public RedisScript<Long> reentrantLockScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(ResourcePatternUtils.getResource("classpath:org/example/redis/lock/reentrantLock.lua")));
        return redisScript;
    }

    @Bean
    public RedisScript<Long> reentrantUnlockScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        redisScript.setScriptSource(new ResourceScriptSource(ResourcePatternUtils.getResource("classpath:org/example/redis/lock/reentrantUnlock.lua")));
        return redisScript;
    }
}
