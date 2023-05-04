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

import io.netty.handler.logging.LogLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.nio.charset.StandardCharsets;

/**
 * 支持http2c交互的org.springframework.web.reactive.function.client.WebClient实例的配置
 * <p><br>
 * unit test: WebClientTests.http2cTest
 * <p>
 * @author Hong Qiaowei
 */
@Configuration
@ConfigurationProperties(prefix = "http2c-webclient")
public class HTTP2CWebClientConfig extends WebClientConfig {

    public static final String HTTP2C_WEB_CLIENT = "http2cWebclient";

    /**
     * @param webClientBuilder
     * @param reactorResourceFactory 它包含spring boot创建/内置的，WebClient运行所需的底层资源，如连接池、event loop等，多个WebClient可共享；
     *                               本方法和 {@link HTTP11WebClientConfig} 创建的WebClient共享reactorResourceFactory
     * @return 支持http2c交互的WebClient
     */
    @Bean(HTTP2C_WEB_CLIENT)
    public WebClient http2cWebClient(WebClient.Builder webClientBuilder, ReactorResourceFactory reactorResourceFactory) {

        HttpClient httpClient = HttpClient.create  (reactorResourceFactory.getConnectionProvider())
                                          .runOn   (reactorResourceFactory.getLoopResources())
                                          .protocol(HttpProtocol.H2C);

        if (isWiretap()) {
            httpClient = httpClient.wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL, StandardCharsets.UTF_8);
        }

        // 其它httpClient配置...

        return webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
                               .build();
    }
}
