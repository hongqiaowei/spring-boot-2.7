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

import org.example.redis.RedisConfig;
import org.example.spel.SpELConfig;
import org.example.config.TaskExecutorConfig;
import org.example.util.ThreadContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.socket.client.WebSocketConnectionManager;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hong Qiaowei
 */
@SpringJUnitConfig(
        classes = {
                TaskExecutionAutoConfiguration.class,
                JacksonAutoConfiguration.class,
                RedisAutoConfiguration.class,
                TaskExecutorConfig.class,
                RedisConfig.class,
                SpELConfig.class,
                RedisTemplateLockServiceTests.ContextConfig.class
        }
)
@TestPropertySource("/redis.properties")
public class RedisTemplateLockServiceTests {

    @Configuration
    @ComponentScan(basePackages = "org.example.redis.lock")
    static class ContextConfig {
        @MockBean
        private WebSocketConnectionManager webSocketConnectionManager;
    }

    @Resource
    RedisTemplateLockService redisTemplateLockService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 测试锁定和到期锁释放
     */
    @Test
    public void lockTest() {
        String threadGlobalId = ThreadContext.globalThreadId();
        String key = "sb2:lock-test";
        long leaseTime = 5000;
        boolean lock = redisTemplateLockService.lock(key, 0, leaseTime);
        assertTrue(lock);
        String value = stringRedisTemplate.opsForValue().get(key);
        assertEquals(threadGlobalId, value);
        try {
            Thread.sleep(leaseTime);
        } catch (InterruptedException e) {
            fail("thread is interrupted");
        }
        value = stringRedisTemplate.opsForValue().get(key);
        assertNull(value);
    }

    /**
     * 测试当线程锁定key 持续t时长，其它线程在t内不能锁定key
     */
    @Test
    public void cantLockTest() {
        String key = "sb2:cant-lock-test";
        long leaseTime = 15_000;
        final boolean[] lock = {redisTemplateLockService.lock(key, 0, leaseTime)};
        assertTrue(lock[0]);

        Thread mainThread = Thread.currentThread();

        new Thread(
                () -> {
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                    lock[0] = redisTemplateLockService.lock(key, 0, leaseTime);
                    LockSupport.unpark(mainThread);
                }
        ).start();

        LockSupport.park();
        assertFalse(lock[0]);
    }

    /**
     * 测试当线程锁定key 持续t时长，其它线程等待tt(大于t)后，可锁定key
     */
    @Test
    public void waitAndLockTest() {
        String key = "sb2:wait-and-lock-test";
        long leaseTime = 5000;
        Thread mainThread = Thread.currentThread();

        new Thread(
                () -> {
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                    redisTemplateLockService.lock(key, 0, leaseTime);
                    LockSupport.unpark(mainThread);
                }
        ).start();

        LockSupport.park();
        boolean lock = redisTemplateLockService.lock(key, 10_000, leaseTime);
        assertTrue(lock);
    }

    /**
     * 测试当thread1锁定key 持续t时长，thread2在t内尝试锁定并进入等待状态，thread1主动释放锁，然后thread2被及时唤醒，并锁定key
     */
    @Test
    public void unlockTest() {
        String key = "sb2:unlock-test";
        long leaseTime = 8000;
        Thread mainThread = Thread.currentThread();

        new Thread(
                () -> {
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                    redisTemplateLockService.lock(key, 0, leaseTime);
                    LockSupport.unpark(mainThread);
                    LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));
                    redisTemplateLockService.unlock(key);
                }
        ).start();

        LockSupport.park();
        boolean lock = redisTemplateLockService.lock(key, 16_000, leaseTime);
        assertTrue(lock);
    }

    @Test
    public void reentrantLockTest() {
        String key = "sb2:reentrant-lock-test";
        long leaseTime = 10_000;
        String threadGlobalId = ThreadContext.globalThreadId();
        Thread mainThread = Thread.currentThread();

                                redisTemplateLockService.reentrantLock(key, 0, leaseTime);
        boolean reentrantLock = redisTemplateLockService.reentrantLock(key, 0, leaseTime);
        assertTrue(reentrantLock);

        String lockCount = (String) stringRedisTemplate.opsForHash().get(key, threadGlobalId);
        assertEquals(Long.parseLong(lockCount), 2); // 前面锁定同一key两次

        reentrantLock = redisTemplateLockService.reentrantUnlock(key);
        assertTrue(reentrantLock);
        lockCount = (String) stringRedisTemplate.opsForHash().get(key, threadGlobalId);
        assertEquals(Long.parseLong(lockCount), 1);

        new Thread(
                () -> {
                    String tgi = ThreadContext.globalThreadId();
                    boolean result = redisTemplateLockService.reentrantLock(key, 20_000, leaseTime);
                    assertTrue(result);
                    String count = (String) stringRedisTemplate.opsForHash().get(key, tgi);
                    assertEquals(Long.parseLong(count), 1);
                    LockSupport.unpark(mainThread);
                }
        ).start();

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(4));
        reentrantLock = redisTemplateLockService.reentrantUnlock(key);
        assertTrue(reentrantLock);
        LockSupport.park();
    }
}
