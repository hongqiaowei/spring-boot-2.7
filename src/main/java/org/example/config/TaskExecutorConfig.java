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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.SB27ScheduledBeanLazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * @author Hong Qiaowei
 */
@Configuration
public class TaskExecutorConfig {

    /**
     * 把任务交由另外一个线程异步执行时，把当前线程的上下文(org.slf4j.MDC，其中包含日志追踪号traceId)也传递过去
     */
    @Bean
    public TaskDecorator taskDecorator() {
        return runnable -> {
            Map<String, String> map = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    if (map != null) {
                        MDC.setContextMap(map);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        };
    }

    @Configuration
    @EnableConfigurationProperties(TaskExecutionProperties.class)
    static class SB27TaskExecutionConfig {

        static final String APPLICATION_TASK_EXECUTOR_BEAN_NAME = "applicationTaskExecutor";

        @Primary
        @Bean
        @ConditionalOnMissingBean
        public TaskExecutorBuilder taskExecutorBuilder(TaskExecutionProperties                properties,
                                                       ObjectProvider<TaskExecutorCustomizer> taskExecutorCustomizers,
                                                       ObjectProvider<TaskDecorator>          taskDecorator) {
            TaskExecutionProperties.Pool pool = properties.getPool();

            TaskExecutorBuilder builder = new TaskExecutorBuilder();
            builder = builder.queueCapacity         (pool.getQueueCapacity());
            builder = builder.corePoolSize          (pool.getCoreSize());
            builder = builder.maxPoolSize           (pool.getMaxSize());
            builder = builder.allowCoreThreadTimeOut(pool.isAllowCoreThreadTimeout());
            builder = builder.keepAlive             (pool.getKeepAlive());

            TaskExecutionProperties.Shutdown shutdown = properties.getShutdown();
            builder = builder.awaitTermination      (shutdown.isAwaitTermination());
            builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
            builder = builder.threadNamePrefix      (properties.getThreadNamePrefix());
            builder = builder.customizers           (taskExecutorCustomizers.orderedStream()::iterator);
            builder = builder.taskDecorator         (taskDecorator.getIfUnique());
            return builder;
        }

        @Primary
        @Bean(name = { APPLICATION_TASK_EXECUTOR_BEAN_NAME, AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME })
        @ConditionalOnMissingBean(Executor.class)
        public ThreadPoolTaskExecutor applicationTaskExecutor(TaskExecutorBuilder builder) {
            return builder.build();
        }
    }

    @Configuration
    @EnableAsync
    static class SB27AsyncConfig extends AsyncConfigurerSupport {

        @Resource(name = TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
        private ThreadPoolTaskExecutor threadPoolTaskExecutor;

        @Override
        public Executor getAsyncExecutor() {
            return threadPoolTaskExecutor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {

            return new AsyncUncaughtExceptionHandler() {

                private final Logger LOGGER = LoggerFactory.getLogger(AsyncUncaughtExceptionHandler.class);

                @Override
                public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
                    LOGGER.error("execute method {} args {}", method.getName(), Arrays.toString(params), throwable);
                }
            };
        }
    }

    @Configuration
    @EnableConfigurationProperties(TaskSchedulingProperties.class)
    @EnableScheduling
    static class SB27TaskSchedulingConfig {

        @Primary
        @Bean
        @ConditionalOnBean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
        public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
            return builder.build();
        }

        @Bean
        @ConditionalOnBean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
        public static LazyInitializationExcludeFilter scheduledBeanLazyInitializationExcludeFilter() {
            return new SB27ScheduledBeanLazyInitializationExcludeFilter();
        }

        @Primary
        @Bean
        public TaskSchedulerBuilder taskSchedulerBuilder(TaskSchedulingProperties properties,
                                                         ObjectProvider<TaskSchedulerCustomizer> taskSchedulerCustomizers) {
            TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
            builder = builder.poolSize              (properties.getPool().getSize());
            TaskSchedulingProperties.Shutdown shutdown = properties.getShutdown();
            builder = builder.awaitTermination      (shutdown.isAwaitTermination());
            builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
            builder = builder.threadNamePrefix      (properties.getThreadNamePrefix());
            builder = builder.customizers           (taskSchedulerCustomizers);
            return builder;
        }
    }

}
