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

import org.example.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * 基于RedisTemplate和lua脚本实现的锁服务
 * <p><br>
 * 锁定线程完成工作解锁后，可通过redis channel及时唤醒等待锁的线程（可能在别的服务器，并设置了较长的锁等待时间），让它们尝试锁定，提高系统效率，所以实现了MessageListener接口
 * <p><br>
 * unit test: RedisTemplateLockServiceTests
 * <p>
 * @author Hong Qiaowei
 */
@Service
public class RedisTemplateLockService implements LockService, MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTemplateLockService.class);

    private static final String unlock_channel = "sb2:unlock";


    @Resource
    private RedisScript<Long> lockScript;

    @Resource
    private RedisScript<Long> unlockScript;

    @Resource
    private RedisScript<Long> reentrantLockScript;

    @Resource
    private RedisScript<Long> reentrantUnlockScript;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Resource
    private RedisMessageListenerContainer redisMessageListenerContainer;


    private final Map<Thread, Set<String>>          lockWaiterMap        = new HashMap<>(256);
    private final GenericToStringSerializer<String> argsSerializer       = new GenericToStringSerializer<>(String.class);
    private final GenericToStringSerializer<Long>   longResultSerializer = new GenericToStringSerializer<>(Long.class);


    @PostConstruct
    public void postConstruct() {
        redisMessageListenerContainer.addMessageListener(this, new ChannelTopic(unlock_channel));
    }

    @Override
    public boolean lock(String name, long waitTime, long leaseTime) {
        boolean lock = doLock(name, lockScript, waitTime, leaseTime);
        if (lock) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("lock {} for {}ms", name, leaseTime);
            }
        }
        return lock;
    }

    @Override
    public boolean reentrantLock(String name, long waitTime, long leaseTime) {
        boolean lock = doLock(name, reentrantLockScript, waitTime, leaseTime);
        if (lock) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("reentrant lock {} for {}ms", name, leaseTime);
            }
        }
        return lock;
    }

    private boolean doLock(String name, RedisScript<Long> scritpt, long waitTime, long leaseTime) {
        long begin = 0;
        while (true) {
            Long ttl = redisTemplate.execute(
                                                scritpt,
                                                argsSerializer,
                                                longResultSerializer,
                                                singletonList(name),
                                                ThreadContext.globalThreadId(), leaseTime
                                    );

            if (ttl == null) {
                LOGGER.error("execute lock script for {} and it return null", name);
                return false;
            }
            if (ttl == -1) {
                return true;
            }
            if (waitTime == 0 || ttl > waitTime) {
                return false;
            }
            long now = System.currentTimeMillis();
            if (begin == 0) {
                begin = now;
            }
            if ((now - begin + ttl) > waitTime) {
                return false;
            }

            Thread t = Thread.currentThread();
            Set<String> keys = lockWaiterMap.computeIfAbsent(t, k -> new HashSet<>(4));
            keys.add(name);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("wait {} mills for {}", ttl, name);
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(ttl));

            keys.remove(name);
        }
    }

    @Override
    public boolean unlock(String name) {
        Long result = redisTemplate.execute(
                                                unlockScript,
                                                argsSerializer,
                                                longResultSerializer,
                                                asList(name, unlock_channel),
                                                ThreadContext.globalThreadId()
                                   );

        if (result == null) {
            LOGGER.error("execute unlock script for {} and it return null", name);
            return false;
        }

        if (result == 0 || result == -1) {
            LOGGER.warn("execute unlock script for {} and it return {}", name, result);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("unlock {}", name);
            }
        }

        return true;
    }

    @Override
    public boolean reentrantUnlock(String name) {
        Long result = redisTemplate.execute(
                                                reentrantUnlockScript,
                                                argsSerializer,
                                                longResultSerializer,
                                                asList(name, unlock_channel),
                                                ThreadContext.globalThreadId()
                                   );

        if (result == null) {
            LOGGER.error("execute reentrant unlock script for {} and it return null", name);
            return false;
        }

        if (result < 0) {
            LOGGER.warn("execute reentrant unlock script for {} and it return {}", name, result);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("reentrant unlock {}", name);
            }
        }

        return true;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String channel   = new String(message.getChannel());
        String unlockKey = new String(message.getBody());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("receive unlock {} msg from {}", unlockKey, channel);
        }

        Iterator<Map.Entry<Thread, Set<String>>> it = lockWaiterMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Thread, Set<String>> entry = it.next();
            if (entry.getValue().contains(unlockKey)) {
                Thread thread = entry.getKey();
                LockSupport.unpark(thread);
            }
        }
    }

}
