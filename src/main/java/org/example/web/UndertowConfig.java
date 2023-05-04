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

// package org.example.config;
//
// import io.undertow.Undertow;
// import io.undertow.UndertowOptions;
// import org.example.util.JacksonUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.boot.autoconfigure.web.ServerProperties;
// import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
// import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
// import org.springframework.boot.web.server.WebServerFactoryCustomizer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// /**
//  * @author Hong Qiaowei
//  */
// @Configuration
// public class UndertowConfig {
//
//     private static final Logger LOGGER = LoggerFactory.getLogger(UndertowConfig.class);
//
//     // TODO: undertow默认超时是多少？官网有无介绍？
//     @Bean
//     public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowCustomizer(ServerProperties serverProperties) {
//
//         LOGGER.info("server properties: " + JacksonUtils.writeValueAsString(serverProperties));
//
//         return new WebServerFactoryCustomizer<UndertowServletWebServerFactory>() {
//
//             @Override
//             public void customize(UndertowServletWebServerFactory factory) {
// //              factory.setPort(9090);
//                 factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
//                     @Override
//                     public void customize(Undertow.Builder builder) {
//                         builder.addHttpListener(8081, "0.0.0.0"); // 同时监听8081端口
// //                      builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
//                     }
//                 });
//             }
//
//         };
//     }
// }
