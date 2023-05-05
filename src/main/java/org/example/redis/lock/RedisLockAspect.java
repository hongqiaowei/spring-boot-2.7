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

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.spel.SB27CachedExpressionEvaluator;
import org.example.util.Const;
import org.example.util.ThrowableUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * 注解RedisLock的实现
 * <br>
 * unit test: RedisLockAnnotationTests
 * @author Hong Qiaowei
 */
@Aspect
@Component
public class RedisLockAspect implements ApplicationContextAware {

    private ApplicationContext            applicationContext;

    private SB27CachedExpressionEvaluator defaultExpressionEvaluator;

    private RedisTemplateLockService      redisTemplateLockService;


    // redisLock不能换成RedisLock
    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        String lockName = redisLock.name(); // 要锁定的key
        Assert.hasText(lockName, "value(lock name/key) of RedisLock annotation is empty");

        String lockParam = redisLock.param();
        if (StringUtils.isNotBlank(lockParam)) {
            String evalAsText = evalLockParam(joinPoint, lockParam);
            lockName = lockName + Const.S.COLON + evalAsText;
        }

        long waitTime  = redisLock.waitTime();
        long leaseTime = redisLock.leaseTime();

        if (redisLock.type() == LockService.LOCK) {
            return lockAndExecute(joinPoint, lockName, waitTime, leaseTime);
        } else {
            return reentrantLockAndExecute(joinPoint, lockName, waitTime, leaseTime);
        }
    }

    private Object lockAndExecute(ProceedingJoinPoint joinPoint, String name, long waitTime, long leaseTime) throws Throwable {
        try {
            boolean lock = redisTemplateLockService.lock(name, waitTime, leaseTime);
            if (lock) {
                return joinPoint.proceed();
            } else {
                throw ThrowableUtils.runtimeExceptionWithoutStack("fail to lock " + name);
            }
        } finally {
            redisTemplateLockService.unlock(name);
        }
    }

    private Object reentrantLockAndExecute(ProceedingJoinPoint joinPoint, String name, long waitTime, long leaseTime) throws Throwable {
        try {
            boolean lock = redisTemplateLockService.reentrantLock(name, waitTime, leaseTime);
            if (lock) {
                return joinPoint.proceed();
            } else {
                throw ThrowableUtils.runtimeExceptionWithoutStack("fail to reentrant lock " + name);
            }
        } finally {
            redisTemplateLockService.reentrantUnlock(name);
        }
    }

    private String evalLockParam(ProceedingJoinPoint joinPoint, String lockParam) {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = target.getClass();
        EvaluationContext context = defaultExpressionEvaluator.createContext(method, args, target, targetClass, applicationContext);
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        return defaultExpressionEvaluator.evalAsText(lockParam, elementKey, context);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        defaultExpressionEvaluator = this.applicationContext.getBean(SB27CachedExpressionEvaluator.class);
        redisTemplateLockService   = this.applicationContext.getBean(RedisTemplateLockService.class);
    }
}
