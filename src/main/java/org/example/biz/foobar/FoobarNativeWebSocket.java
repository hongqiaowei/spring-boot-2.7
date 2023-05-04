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

// package org.example.biz.foobar;
//
// import org.example.util.Const;
// import org.example.util.DateTimeUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
// import org.springframework.stereotype.Component;
//
// import javax.annotation.PostConstruct;
// import javax.annotation.Resource;
// import javax.websocket.OnClose;
// import javax.websocket.OnMessage;
// import javax.websocket.OnOpen;
// import javax.websocket.Session;
// import javax.websocket.server.PathParam;
// import javax.websocket.server.ServerEndpoint;
// import java.io.IOException;
// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
//
// /**
//  * @author Hong Qiaowei
//  */
// @Component
// @ServerEndpoint("/foobar/ws/{clientId}")
// public class FoobarNativeWebSocket {
//
//     private static final Logger LOGGER = LoggerFactory.getLogger(FoobarNativeWebSocket.class);
//
//     private static final Map<String/* session id */, Session>               session_map        = new ConcurrentHashMap<>();
//
//     private static final Map<String/* session id */, String/* client id */> session_client_map = new ConcurrentHashMap<>();
//
//     @Resource
//     private ThreadPoolTaskScheduler threadPoolTaskScheduler;
//
//
//     @PostConstruct
//     public void postConstruct() {
//         threadPoolTaskScheduler.scheduleAtFixedRate(
//                 () -> {
//                     String message = "msg " + DateTimeUtils.convert(LocalDateTime.now(), Const.DP.DP19);
//                     broadcast(message);
//                 },
//                 Duration.ofSeconds(10)
//         );
//     }
//
//     @OnOpen
//     public void onOpen(Session session, @PathParam(value = "clientId") String clientId) {
//         String sessionId = session.getId();
//         session_map.put(sessionId, session);
//         session_client_map.put(sessionId, clientId);
//         LOGGER.info("与客户端 {} 建立连接", clientId);
//     }
//
//     @OnClose
//     public void onClose(Session session) {
//         String sessionId = session.getId();
//         session_map.remove(sessionId);
//         String clientId = session_client_map.remove(sessionId);
//         LOGGER.info("与客户端 {} 断开连接", clientId);
//     }
//
//     @OnMessage
//     // The method may have a non-void return type, in which case the web socket runtime must interpret this as a web socket message to return to the peer.
//     public void onMessage(Session session, String message/*the whole message*/) {
//         String sessionId = session.getId();
//         String clientId = session_client_map.get(sessionId);
//         LOGGER.info("收到客户端 {} 消息：{}", clientId, message);
//     }
//
//     public void broadcast(String message) {
//         session_map.forEach(
//                 (id, session) -> {
//                     if (session.isOpen()) {
//                         String sessionId = session.getId();
//                         String clientId = session_client_map.get(sessionId);
//                         try {
//                             session.getBasicRemote().sendText(message);
//                             LOGGER.info("向客户端 {} 发消息：{}", clientId, message);
//                         } catch (IOException e) {
//                             LOGGER.error("向客户端 {} 发消息：{}", clientId, message, e);
//                         }
//                     }
//                 }
//         );
//     }
//
//     public void send(String message, String clientId) {
//         String sessionId = session_client_map.get(clientId);
//         if (sessionId != null) {
//             Session session = session_map.get(sessionId);
//             if (session.isOpen()) {
//                 session.getAsyncRemote().sendText(message);
//                 LOGGER.info("向客户端 {} 发消息：{}", clientId, message);
//             }
//         }
//     }
//
// }
