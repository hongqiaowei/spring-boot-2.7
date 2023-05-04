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

package org.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 类雪花算法实现的long id生成器，id由时间、服务器id、序号3部分顺序组成
 * <p><br>
 * 支持8191个服务器节点(服务器id取值范围:[0,8191])，每秒可生成131071个id
 * <p><br>
 * 关于时间回拨，应在运维层面处理，比如独立的时间同步服务器
 * <p><br>
 * jmh test: LongIdGeneratorPerfTests
 * <p>
 * @author Hong Qiaowei
 */
public class LongIdGenerator {

//  private static final Logger LOGGER = LoggerFactory.getLogger(LongIdGenerator.class);

    private static final int  bound             = 131071;

    private static final int  timestamp_offset  = 31;

    private static final int  server_id_offset  = 18;

    private static final long server_id         = NetworkUtils.getServerId();

    private static final long server_id_segment = server_id << server_id_offset;


    public static final LongIdGenerator INSTANCE = new LongIdGenerator();


    private AtomicInteger       counter   = new AtomicInteger(0);


//  private boolean             reseting  = false;

    private final ReentrantLock lock      = new ReentrantLock(true);

//  private final Condition     condition = lock.newCondition();


    public AtomicInteger getCounter() {
        return counter;
    }

    public long next() {
        /*
        if (reseting) {
            try {
                condition.await();
            } catch (InterruptedException e) {
                LOGGER.warn("condition is interrupted, counter={}", counter.intValue());
            }
        }
        */
        int c = counter.incrementAndGet();
        if (c < 0) {
            try {
                lock.lock();
                c = counter.incrementAndGet();
                if (c < 0) {
                    // reseting = true;
                    c = 0;
                    counter.set(c);
                    // reseting = false;
                    // condition.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }
        return ((System.currentTimeMillis() / 1000) << timestamp_offset) | server_id_segment | (c % bound);
    }

    public String nextStr() {
        return String.valueOf(next());
    }

}
