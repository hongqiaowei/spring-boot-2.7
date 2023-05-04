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

import org.apache.commons.lang3.StringUtils;
import org.example.util.Const;
import org.example.util.LongIdGenerator;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * unit test: LogFilterTests
 * @author Hong Qiaowei
 */
class LogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // traceId为业务/日志追踪id，可与交互方约定通过请求/响应头传递，尽量不要通过请求/响应体，那样可能与业务耦合
        String traceId = request.getHeader(Const.TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            traceId = (String) request.getAttribute(Const.TRACE_ID);
            if (traceId == null) {
                traceId = LongIdGenerator.INSTANCE.nextStr();
                request.setAttribute(Const.TRACE_ID, traceId);
            }
        }
        response.setHeader(Const.TRACE_ID, traceId);
        MDC.put(Const.TRACE_ID, traceId); // 放入当前线程上下文
        filterChain.doFilter(request, response);
        MDC.remove(Const.TRACE_ID);
    }
}
