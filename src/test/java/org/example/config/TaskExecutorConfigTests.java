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

package org.example.config;

import org.example.util.Const;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.annotation.Resource;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Hong Qiaowei
 */
@SpringJUnitConfig(
        classes = {
                TaskExecutionAutoConfiguration.class,
                TaskExecutorConfig.class,
                TaskExecutorConfigTests.AsyncServiceConfig.class
        }
)
public class TaskExecutorConfigTests {

    @Configuration
    static class AsyncServiceConfig {

        @Service
        static class AsyncService {

            private static final Logger LOGGER = LoggerFactory.getLogger(AsyncService.class);

            @Async
            public Future<String> execute() {
                LOGGER.info("async service do something");
                return new AsyncResult<>(MDC.get(Const.TRACE_ID));
            }
        }
    }

    @Resource(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    AsyncServiceConfig.AsyncService asyncService;

    @Test
    public void test() throws ExecutionException, InterruptedException {
        MDC.put(Const.TRACE_ID, "666");
        Future<String> asyncResult = asyncService.execute();
        Assertions.assertEquals("666", asyncResult.get());

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return MDC.get(Const.TRACE_ID);
            }
        };
        Future<String> submit = threadPoolTaskExecutor.submit(callable);
        Assertions.assertEquals("666", submit.get());
    }
}
