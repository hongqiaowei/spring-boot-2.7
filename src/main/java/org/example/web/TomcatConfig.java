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

// package org.example.web;
//
// import org.example.util.JacksonUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.boot.autoconfigure.web.ServerProperties;
// import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
// import org.springframework.boot.web.server.WebServerFactoryCustomizer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// /**
//  * @author Hong Qiaowei
//  */
// @Configuration
// public class TomcatConfig {
//
//     private static final Logger LOGGER = LoggerFactory.getLogger(TomcatConfig.class);
//
//     @Bean
//     public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer(ServerProperties serverProperties) {
//
//         LOGGER.info("server properties: " + JacksonUtils.writeValueAsString(serverProperties));
//
//         return new WebServerFactoryCustomizer<TomcatServletWebServerFactory>() {
//             @Override
//             public void customize(TomcatServletWebServerFactory factory) {
//                 factory.addConnectorCustomizers(
//                         connector -> {
//                             connector.setPort(8080);
//                             // 4核8G内存单进程调度线程数800-1000，超过这个数，将会花费巨大的时间在CPU调度上
//                             // 1核2G内存，线程数经验值200
//                             connector.setProperty("maxThreads",        "500");
//                             connector.setProperty("minSpareThreads",   "50");
//                             connector.setProperty("connectionTimeout", "10000");
//                             connector.setProperty("keepAliveTimeout",  "120000");
//                         }
//                 );
//             }
//         };
//     }
// }
