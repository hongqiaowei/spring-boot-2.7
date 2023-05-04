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

import org.example.util.Const;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author Hong Qiaowei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RedisLock {

    /**
     * 分布式锁的名字
     */
    @AliasFor("value")
    String name()    default Const.S.EMPTY;

    @AliasFor("name")
    String value()   default Const.S.EMPTY;

    int type()       default LockService.LOCK;

    /**
     * 锁参数，可选，支持 spring el # 读取方法参数和 @ 读取 spring bean
     */
    String param()   default Const.S.EMPTY;

    /**
     * 等待锁超时时间，默认10s
     */
    long waitTime()  default LockService.DEFAULT_WAIT_TIME;

    /**
     * 锁过期时间，默认20s
     */
    long leaseTime() default LockService.DEFAULT_LEASE_TIME;
}
