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

import com.google.common.collect.Lists;
import okhttp3.Protocol;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Hong Qiaowei
 */
@SpringJUnitConfig(
        classes = {
                JacksonAutoConfiguration.class,
                CodecsAutoConfiguration.class,
                ClientHttpConnectorAutoConfiguration.class,
                WebClientAutoConfiguration.class,
                HTTP11WebClientConfig.class,
                HTTP2CWebClientConfig.class
        }
)
@TestPropertySource("/application.properties")
public class WebClientTests {

    private MockWebServer server;

    @Resource
    private WebClient http11WebClient;

    @Resource(name = HTTP2CWebClientConfig.HTTP2C_WEB_CLIENT)
    private WebClient http2cWebClient;

    @BeforeEach
    void beforeEach() {
        server = new MockWebServer();
    }

    @AfterEach
    void afterEach() throws IOException {
        if (server != null) {
            this.server.shutdown();
        }
    }

    private void prepareResponse(Consumer<MockResponse> consumer) {
        MockResponse response = new MockResponse();
        consumer.accept(response);
        server.enqueue(response);
    }

    @Test
    void http11Test() {
        prepareResponse(
                response -> response
                                    .setHeader("Content-Type", "application/json")
                                    .setBody  ("{'hello':'world'}")
        );

        ResponseEntity<Map<String, String>> response = http11WebClient
                                                                      .get()
                                                                      .uri(server.url("/hello-world").toString())
                                                                      .accept(MediaType.APPLICATION_JSON)
                                                                      .retrieve()
                                                                      .toEntity(
                                                                              new ParameterizedTypeReference<Map<String, String>>() {}
                                                                      )
                                                                      .block();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("world", response.getBody().get("hello"));
    }

    @Test
    void http2cTest() {
        server.setProtocols(Lists.newArrayList(Protocol.H2_PRIOR_KNOWLEDGE));
        prepareResponse(
                response -> response
                                    .setHeader("Content-Type", "application/json")
                                    .setBody  ("{'hello':'world'}")
        );

        ResponseEntity<Map<String, String>> response = http2cWebClient
                                                                      .get()
                                                                      .uri(server.url("/hello-world").toString())
                                                                      .accept(MediaType.APPLICATION_JSON)
                                                                      .retrieve()
                                                                      .toEntity(
                                                                              new ParameterizedTypeReference<Map<String, String>>() {}
                                                                      )
                                                                      .block();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("world", response.getBody().get("hello"));
    }
}
