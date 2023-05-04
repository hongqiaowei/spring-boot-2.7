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

package org.example.web;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.redis.RemoveRedis;
import org.example.util.Const;
import org.example.util.JacksonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Hong Qiaowei
 */
@Import(
        {
                RemoveRedis.class,
                JacksonAutoConfiguration.class,
                CodecsAutoConfiguration.class,
                ClientHttpConnectorAutoConfiguration.class,
                WebClientAutoConfiguration.class,
                LogFilterTests.ControllerConfig.class
        }
)
@WebMvcTest
/*
@MockBean to provide mock implementations for required collaborators.
*/

// 也可用下面的注解替换上面的
// @AutoConfigureMockMvc
// @SpringBootTest

public class LogFilterTests {

    @TestConfiguration // 不能用@Configuration，否则filter不会执行
    static class ControllerConfig {

        @Controller
        @RequestMapping("/log-filter-tests")
        static class LogFilterTestsController {

            private static final Logger LOGGER = LoggerFactory.getLogger(LogFilterTestsController.class);

            @GetMapping("/test")
            @ResponseBody
            public Map<String, String> test() {
                LOGGER.info("测试日志追踪id");
                String traceId = MDC.get(Const.TRACE_ID);
                return Collections.singletonMap(Const.TRACE_ID, traceId);
            }
        }
    }

    @Resource
    private MockMvc mockMvc;

    @Test
    void traceIdTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                                                 MockMvcRequestBuilders
                                                                       .get   ("/log-filter-tests/test")
                                                                       .header(Const.TRACE_ID, "1368")
                                                                       .accept(MediaType.APPLICATION_JSON)
                                     )
                                     .andExpect(status().isOk())
                                  // .andDo(print())
                                     .andReturn();

        String responseBodyString = mvcResult.getResponse().getContentAsString();
        Map<String, String> responseBody = JacksonUtils.readValue(responseBodyString, new TypeReference<Map<String, String>>() {
        });

        Assertions.assertEquals("1368", responseBody.get(Const.TRACE_ID));
    }

}
