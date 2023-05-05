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

package org.example.web.websocket.server;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.example.util.Const;
import org.example.util.DateTimeUtils;
import org.example.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.*;
import org.springframework.web.socket.adapter.NativeWebSocketSession;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author Hong Qiaowei
 */
public class WebSocketServerHandler implements WebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServerHandler.class);

    private final Map<String/* session id*/, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @PostConstruct
    public void postConstruct() {
        // 演示每10s向所有websocket客户端发消息
        threadPoolTaskScheduler.scheduleAtFixedRate(
                () -> {
                    String message = "msg " + DateTimeUtils.toString(LocalDateTime.now(), Const.DP.DP19);
                    broadcast(message);
                },
                Duration.ofSeconds(10)
        );
    }

    public void broadcast(String message) {
        sessionMap.forEach(
                (id, webSocketSession) -> {
                    if (webSocketSession.isOpen()) {
                        String clientId = (String) webSocketSession.getAttributes().get("clientId");
                        String address = Utils.getIp(webSocketSession.getRemoteAddress());

                        // try {
                        //     webSocketSession.sendMessage(new TextMessage(message));
                        // } catch (IOException e) {
                        //     throw new RuntimeException(e);
                        // }

                        // 通过原生api实现消息的异步发送
                        org.eclipse.jetty.websocket.common.WebSocketSession jettySession = (org.eclipse.jetty.websocket.common.WebSocketSession) ((NativeWebSocketSession) webSocketSession).getNativeSession();
                        RemoteEndpoint remote = jettySession.getRemote();
                        Future<Void> sendFuture = remote.sendStringByFuture(message);
                        LOGGER.info("send ws message to client {} {}: {}", address, clientId, message);
                    }
                }
        );
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        sessionMap.put(sessionId, session);
        String clientId = (String) session.getAttributes().get("clientId");
        String address = Utils.getIp(session.getRemoteAddress());
        LOGGER.info("establish ws connection with client {} {}", address, clientId);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String clientId = (String) session.getAttributes().get("clientId");
            String address = Utils.getIp(session.getRemoteAddress());
            LOGGER.info("receive ws message from client {} {}: {}", address, clientId, textMessage.getPayload());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable throwable) {
        String clientId = (String) session.getAttributes().get("clientId");
        String address = Utils.getIp(session.getRemoteAddress());
        LOGGER.error("ws transport error with client {} {}", address, clientId, throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessionMap.remove(session.getId());
        String clientId = (String) session.getAttributes().get("clientId");
        String address = Utils.getIp(session.getRemoteAddress());
        LOGGER.info("close ws connection with client {} {}, close status: {}", address, clientId, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
