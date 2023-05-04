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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.*;

/**
 * @author Hong Qiaowei
 */
public class WebSocketClientHandler implements WebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClientHandler.class);

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String uri = session.getUri().toString();
        LOGGER.info("establish ws connection with server {}", uri);
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            String uri = session.getUri().toString();
            LOGGER.info("receive ws message from server {}: {}", uri, textMessage.getPayload());
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable throwable) throws Exception {
        String uri = session.getUri().toString();
        LOGGER.error("ws transport error with server {}", uri, throwable);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
        String uri = session.getUri().toString();
        LOGGER.info("close ws connection with server {}, close status: {}", uri, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}