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

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.jetty.JettyWebSocketClient;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * @author Hong Qiaowei
 */
@SpringJUnitConfig(
        classes = {
                WebSocketClientTests.Config.class,
                SpringWebSocketClientConfig.class
        }
)
public class WebSocketClientTests {

    static class Config {

        @MockBean
        WebSocketConnectionManager webSocketConnectionManager;

        @MockBean
        ServletContext servletContext;

        @Bean
        public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
            return new ThreadPoolTaskExecutor();
        }
    }

    private MockWebServer server;

    private volatile boolean flag = false;

    @Resource
    private JettyWebSocketClient jettyWebSocketClient;

    private void prepareResponse(Consumer<MockResponse> consumer) {

        MockResponse response = new MockResponse().withWebSocketUpgrade(new WebSocketListener() {

            private final Logger LOGGER = LoggerFactory.getLogger("WS-SERVER");

            @Override
            public void onOpen(final WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                LOGGER.info("establish ws connection \n request: {} \n response: {}", webSocket.request(), response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                LOGGER.info("receive client ws message: {}", text);
                Assertions.assertEquals("client msg 1", text);
                webSocket.send("server msg 1");
                flag = true;
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                LOGGER.error("close ws connection error: code = {}, reason = {}", code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable throwable, Response response) {
                super.onFailure(webSocket, throwable, response);
                LOGGER.error("ws transport", throwable);
            }
        });
        consumer.accept(response);
        server.enqueue(response);
    }

    @Test
    void test() throws ExecutionException, InterruptedException, IOException {

        server = new MockWebServer();
        jettyWebSocketClient.start();
        prepareResponse(
                response -> response.setHeader("h1", "v1")
        );

        String host = server.getHostName();
        int port = server.getPort();

        ListenableFuture<WebSocketSession> webSocketSessionListenableFuture = jettyWebSocketClient.doHandshake(
                new WebSocketHandler() {

                    private final Logger LOGGER = LoggerFactory.getLogger("WS-CLIENT");

                    @Override
                    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                        LOGGER.info("establish ws connection with {}", session.getUri());
                    }

                    @Override
                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                        String payload = (String) message.getPayload();
                        LOGGER.info("receive ws message from server {}: {}", session.getUri(), payload);
                        Assertions.assertEquals("server msg 1", payload);
                    }

                    @Override
                    public void handleTransportError(WebSocketSession session, Throwable throwable) throws Exception {
                        LOGGER.error("ws transport error with server {}", session.getUri(), throwable);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        LOGGER.info("close ws connection with server {}", session.getUri());
                    }

                    @Override
                    public boolean supportsPartialMessages() {
                        return false;
                    }
                },
                "ws://" + host + ":" + port + "/");

        WebSocketSession webSocketSession = webSocketSessionListenableFuture.get();
        webSocketSession.sendMessage(new TextMessage("client msg 1"));

        while (true) {
            if (flag) {
                jettyWebSocketClient.stop();
                break;
            }
        }
    }

}
