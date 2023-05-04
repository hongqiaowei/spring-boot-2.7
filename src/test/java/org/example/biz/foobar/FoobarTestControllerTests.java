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

import org.example.redis.RemoveRedis;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;

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
                WebClientAutoConfiguration.class
        }
)
@WebMvcTest(controllers = {FoobarTestController.class})
public class FoobarTestControllerTests {

    @Resource
    private MockMvc mockMvc;

    @Test
    void formTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                                             MockMvcRequestBuilders
                                                                   .post("/foobar/formTest")
                                                                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                                                                   .content("field1=value1")
                                     )
                                     .andExpect(status().isOk())
                                     .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseBodyString = response.getContentAsString();
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        MockHttpInputMessage httpInputMessage = new MockHttpInputMessage(responseBodyString.getBytes());
        MultiValueMap<String, String> body = formHttpMessageConverter.read(null, httpInputMessage);
        Assertions.assertEquals("valuex", body.get("fieldx").get(0));
    }

}
