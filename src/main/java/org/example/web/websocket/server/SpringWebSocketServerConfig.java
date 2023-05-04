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

import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.example.util.Const;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.ServletWebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Hong Qiaowei
 */
@Configuration
@EnableWebSocket
public class SpringWebSocketServerConfig implements WebSocketConfigurer {

    @Bean
    public DefaultHandshakeHandler defaultHandshakeHandler(ServletContext servletContext) {
        WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        // jetty websocket server的配置
        // policy.setInputBufferSize(8192);
        // policy.setIdleTimeout    (600_000);
        WebSocketServerFactory      webSocketServerFactory = new WebSocketServerFactory     (servletContext, policy);
        JettyRequestUpgradeStrategy requestUpgradeStrategy = new JettyRequestUpgradeStrategy(webSocketServerFactory);
        return new DefaultHandshakeHandler(requestUpgradeStrategy);
    }

    @Bean
    public WebSocketServerHandler webSocketServerHandler() {
        return new WebSocketServerHandler();
    }

    @Bean
    public WebSocketServerHandshakeInterceptor webSocketServerHandshakeInterceptor() {
        return new WebSocketServerHandshakeInterceptor();
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {

        // 支持通过/websocket前缀建立websocket连接
        if (registry instanceof ServletWebSocketHandlerRegistry) {
            ((ServletWebSocketHandlerRegistry) registry).setUrlPathHelper(
                    new UrlPathHelper() {
                        @Override
                        public @NonNull String resolveAndCacheLookupPath(@NonNull HttpServletRequest request) {
                            String prefix = "/websocket";
                            String path = super.resolveAndCacheLookupPath(request);
                            if (path.startsWith(prefix)) {
                                return prefix + "/**";
                            }
                            return path;
                        }
                    }
            );
        }

        registry.addHandler       (webSocketServerHandler(), "/websocket/**")
                .addInterceptors  (webSocketServerHandshakeInterceptor())
                .setAllowedOrigins(Const.S.ASTERISK_STR);
    }
}
