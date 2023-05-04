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

package org.example;

import org.example.biz.foobar.FoobarTestControllerTests;
import org.example.biz.foobar.FoobarServiceTests;
import org.example.config.TaskExecutorConfigTests;
import org.example.jpa.UserRepositoryTests;
import org.example.redis.lock.RedisLockAnnotationTests;
import org.example.redis.lock.RedisTemplateLockServiceTests;
import org.example.web.LogFilterTests;
import org.example.web.WebClientTests;
import org.example.web.WebMvcTests;
import org.example.web.websocket.client.WebSocketClientTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Hong Qiaowei
 */
@Suite
@SelectClasses(
        value = {
                FoobarServiceTests.class,
                FoobarTestControllerTests.class,
                WebMvcTests.class,
                LogFilterTests.class,
                TaskExecutorConfigTests.class,
                RedisTemplateLockServiceTests.class,
                RedisLockAnnotationTests.class,
                WebClientTests.class,
                UserRepositoryTests.class,
                WebSocketClientTests.class
        }
)
public class TestSuite {
}
