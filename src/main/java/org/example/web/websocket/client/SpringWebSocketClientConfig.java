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

package org.example.web.websocket.client;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;

import javax.servlet.ServletContext;
import java.util.concurrent.Executor;

/**
 * unit test: WebSocketClientTests
 * @author Hong Qiaowei
 */
@Configuration
public class SpringWebSocketClientConfig {

    /**
     * @param servletContext 传进来是为复用其中的Executor
     * @param threadPoolTaskExecutor 复用{@link org.example.config.TaskExecutorConfig}中的ThreadPoolTaskScheduler
     */
    @Bean
    public JettyWebSocketClient jettyWebSocketClient(ServletContext servletContext, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        ServerContainer serverContainer = (ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
        if (serverContainer == null) {
            return new JettyWebSocketClient();
        } else {
            Executor executor = serverContainer.getExecutor();
            HttpClient jettyHttpClient = new HttpClient();
            jettyHttpClient.setExecutor(executor);
            WebSocketClient webSocketClient = new WebSocketClient(jettyHttpClient);
            JettyWebSocketClient client = new JettyWebSocketClient(webSocketClient);
            if (threadPoolTaskExecutor != null) {
                client.setTaskExecutor(threadPoolTaskExecutor);
            }
            return client;
        }
    }

    @Bean
    public WebSocketClientHandler webSocketClientHandler() {
        return new WebSocketClientHandler();
    }

    /**
     * 演示与本机websocket服务端的交互；采用WebSocketConnectionManager，断线重连不好实现
     */
    @Bean
    public WebSocketConnectionManager webSocketConnectionManager(JettyWebSocketClient client, WebSocketClientHandler handler) {
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, handler, "ws://127.0.0.1:8080/websocket?clientId=cx");
        manager.setAutoStartup(true);
        return manager;
    }

}
