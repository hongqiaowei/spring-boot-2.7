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

/**
 * @author Hong Qiaowei
 */
public interface LockService {

    // 锁类型
    static final int LOCK           = 1;
    static final int REENTRANT_LOCK = 2;

    static final long DEFAULT_WAIT_TIME  = 10_000;
    static final long DEFAULT_LEASE_TIME = 20_000;


    default boolean lock(String name) {
        return lock(name, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME);
    }

    default boolean lock(String name, long waitTime) {
        return lock(name, waitTime, DEFAULT_LEASE_TIME);
    }

    /**
     * unfair lock
     *
     * @param name      锁名
     * @param waitTime  若为0，则相当于tryLock
     * @param leaseTime 获取锁多长时间后自动释放
     * @return          如果锁定返回true，其它情况返回false
     */
    boolean lock(String name, long waitTime, long leaseTime);

    boolean unlock(String name);

    default boolean reentrantLock(String name) {
        return reentrantLock(name, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME);
    }

    default boolean reentrantLock(String name, long waitTime) {
        return reentrantLock(name, waitTime, DEFAULT_LEASE_TIME);
    }

    /**
     * 可重入锁
     *
     * @param name      锁名
     * @param waitTime  若为0，则相当于tryLock
     * @param leaseTime 获取锁多长时间后自动释放
     * @return          如果锁定返回true，其它情况返回false
     */
    boolean reentrantLock(String name, long waitTime, long leaseTime);

    boolean reentrantUnlock(String name);
}
