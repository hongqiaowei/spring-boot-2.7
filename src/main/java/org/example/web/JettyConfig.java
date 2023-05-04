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

import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.example.util.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hong Qiaowei
 */
@Configuration
public class JettyConfig {

    // 自定义http2c监听的端口
    @Value("${server.http2.port:9090}")
    private int h2cPort;

    private static final Logger LOGGER = LoggerFactory.getLogger(JettyConfig.class);

    @Bean
    public WebServerFactoryCustomizer<JettyServletWebServerFactory> jettyCustomizer(ServerProperties serverProperties) {

        LOGGER.info("server properties: " + JacksonUtils.writeValueAsString(serverProperties));

        return new WebServerFactoryCustomizer<JettyServletWebServerFactory>() {
            @Override
            public void customize(JettyServletWebServerFactory serverFactory) {

                // 自定义处理请求的线程池
                // QueuedThreadPool queuedThreadPool = new QueuedThreadPool(6, 6);
                // serverFactory.setThreadPool(queuedThreadPool);

                JettyServerCustomizer serverCustomizer = new JettyServerCustomizer() {
                    @Override
                    public void customize(Server server) {

                        /*
                            若server.http2.enabled为true，则spring boot创建的http2服务监听（复用）server.port（http1.1）
                            然后资源和配置也是共享的，比如http响应头是否包含http容器版本信息的配置

                            http1.1和http2机制不同，实际业务中也可能存在http1.1的响应头不需要附带http容器版本，而http2需要的情况，同时为了系统逻辑清晰
                            所以这里http1.1和http2各自独立配置，但共享相关资源，比如接收、处理请求的线程池
                            所以自定义server.http2.port且不开启server.http2.enabled
                         */

                        // http1.1
                        ServerConnector http11Connector = server.getBean(ServerConnector.class);
                        // http11Connector.setPort(6060);
                        // http11Connector.setIdleTimeout(10_000);
                        HttpConnectionFactory http11ConnectionFactory = http11Connector.getConnectionFactory(HttpConnectionFactory.class);
                        HttpConfiguration http11Configuration = http11ConnectionFactory.getHttpConfiguration();
                        http11Configuration.setSendServerVersion(false);
                        http11Configuration.setSendDateHeader(false);

                        // http2c
                        HttpConfiguration http2cConfig = new HttpConfiguration();
                        http2cConfig.setSendServerVersion(false);
                        http2cConfig.setSendDateHeader(false);
                        HTTP2CServerConnectionFactory http2CServerConnectionFactory = new HTTP2CServerConnectionFactory(http2cConfig);
                        ServerConnector http2cConnector = new ServerConnector(server, http2CServerConnectionFactory);
                        http2cConnector.setPort(h2cPort);
                        server.addConnector(http2cConnector);
                    }
                };
                serverFactory.addServerCustomizers(serverCustomizer);
            }
        };
    }

}
