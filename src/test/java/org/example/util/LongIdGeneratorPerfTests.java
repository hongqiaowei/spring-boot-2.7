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

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Hong Qiaowei
 */
// http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
@State(Scope.Thread) // 每个测试线程有自己的count(下面的类属性)
public class LongIdGeneratorPerfTests {

    private int count = 0;

    @Setup(Level.Iteration) // 每次执行测试方法(Iteration)前都会执行本方法
    public void setup() {
        // count = 0;
        // System.err.println(ThreadContext.globalThreadId() + " set up, count = 0");
    }

    // @Benchmark
    // @BenchmarkMode(Mode.SingleShotTime)
    // @Warmup(iterations = 1, batchSize = 1_000_000)
    // @Measurement(iterations = 2, batchSize = 6_000)
    // @Threads(2)
    // @OutputTimeUnit(TimeUnit.MILLISECONDS)
    // public long test1() {
    //     count++;
    //     return count;
    // }

    @Benchmark // 方法加此注解，才会参与性能测试
    @Threads(200) // 每个测试进程中，测试线程的数量
    @BenchmarkMode(Mode.SingleShotTime)
    @Warmup(iterations = 2/*预热轮数(1轮是1个operation)*/, batchSize = 100_000/*每轮预热(每个operation)调用几次方法*/)
    @Measurement(iterations = 5, batchSize = 100_000)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void test2(Blackhole blackhole) {
        long id = LongIdGenerator.INSTANCE.next();
        // UUID id = UUID.randomUUID();
        blackhole.consume(id);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        // System.err.println(ThreadContext.globalThreadId() + " tear down, count = " + count);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                                          .include(LongIdGeneratorPerfTests.class.getSimpleName())
                                          .forks(1) // fork一个子进程进行测试
                                          .build();
        new Runner(opt).run();
    }
}
