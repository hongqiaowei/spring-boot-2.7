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

package org.example.biz.foobar;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Hong Qiaowei
 */
@SpringJUnitConfig(classes = {FoobarServiceTests.ContextConfig.class, AopAutoConfiguration.class})
public class FoobarServiceTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoobarServiceTests.class);

    @Configuration
    @ComponentScan(basePackages = "org.example.biz.foobar")
    static class ContextConfig {
    }

    @Resource
    ApplicationContext applicationContext;

    @Test
    public void test() {
        FoobarService foobarService = applicationContext.getBean(FoobarService.class);
        assertTrue(AopUtils.isAopProxy(foobarService));
        assertTrue(AopUtils.isCglibProxy(foobarService));
        int i = foobarService.incrementAndGet();
        assertEquals(99, i);
        LOGGER.info("test end.");
    }
}
