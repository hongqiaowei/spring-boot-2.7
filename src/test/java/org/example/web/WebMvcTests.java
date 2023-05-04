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

import org.example.redis.RemoveRedis;
import org.example.util.JacksonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

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
                WebMvcTests.ControllerConfig.class
        }
)
@WebMvcTest
public class WebMvcTests {

    @TestConfiguration
    static class ControllerConfig {

        @Controller
        @RequestMapping("/web-mvc")
        static class TestController {

            @PostMapping("/test")
            @ResponseBody
            public Person test(@RequestBody Person person, Pageable pageable) {
                Assertions.assertEquals(16, pageable.getPageSize());
                person.setAge(168);
                return person;
            }
        }
    }

    @Resource
    private MockMvc mockMvc;

    @Test
    void argumentResolverAndMessageConverterTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                                             MockMvcRequestBuilders
                                                                   .post   ("/web-mvc/test?pageSize=16&pageNo=3")
                                                                   .header (HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                                                                   .content(" {'name':'person0', 'age':18} ")
                                     )
                                     .andExpect(status().isOk())
                                     .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBodyString = response.getContentAsString();
        Person person = JacksonUtils.readValue(responseBodyString, Person.class);
        Assertions.assertEquals(168, person.getAge());
    }

}
