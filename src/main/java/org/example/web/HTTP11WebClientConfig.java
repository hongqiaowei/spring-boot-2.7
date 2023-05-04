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

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactorNettyHttpClientMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 支持http1.1交互的org.springframework.web.reactive.function.client.WebClient实例的配置
 * <p><br>
 * unit test: WebClientTests.http11Test
 * <p>
 * @author Hong Qiaowei
 */
@Configuration
@ConfigurationProperties(prefix = "http11-webclient")
public class HTTP11WebClientConfig extends WebClientConfig {

    public static final String HTTP11_WEB_CLIENT = "http11Webclient";

    @Bean
    public ReactorNettyHttpClientMapper reactorNettyHttpClientMapper() {

        return
                httpClient -> {

                    if (isWiretap()) { // 开启reactor.netty.http的调试日志，可看到请求&响应的报文等
                        httpClient = httpClient.wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL, StandardCharsets.UTF_8);
                        // 需同时配置logger reactor.netty.http.client的level为DEBUG
                    }

                    if (isTrustInsecureSSL()) {
                        try {
                            SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                            httpClient = httpClient.secure(t -> t.sslContext(sslContext));
                            LOGGER.warn("http client ssl insecure trust");
                        } catch (SSLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return httpClient.doOnConnected(
                                             connection -> {
                                                 connection.addHandlerLast(new ReadTimeoutHandler (getReadTimeout(),  TimeUnit.MILLISECONDS));
                                                 connection.addHandlerLast(new WriteTimeoutHandler(getWriteTimeout(), TimeUnit.MILLISECONDS));
                                             }
                                     )
                                  // .responseTimeout(Duration.ofMillis(responseTimeout))              // the time waiting to receive a response after sending a request
                                     .option  (ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnTimeout()) // netty sets it to 30 seconds by default
                                     .option  (ChannelOption.TCP_NODELAY,            isTcpNodeLay())
                                     .option  (ChannelOption.SO_KEEPALIVE,           isKeepAlive())
                                     .compress(isCompress());
                };
    }

    /**
     * 因为系统还会创建支持http2c交互的WebClient，但http1.1应用普遍，所以标注@Primary
     * @param webClientBuilder
     * @return 支持http1.1交互的WebClient
     */
    @Primary
    @Bean(HTTP11_WEB_CLIENT)
    public WebClient http11WebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

}
