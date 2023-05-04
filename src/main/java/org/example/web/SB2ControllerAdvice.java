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

import org.example.util.Const;
import org.example.util.Result;
import org.example.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理controller层面抛出的异常
 * @see SB2ErrorController
 * @author Hong Qiaowei
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SB2ControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(SB2ControllerAdvice.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Result<Map<Object, Object>>> throwableHandler(HttpServletRequest request, Throwable throwable) {

        LOGGER.error(Const.S.EMPTY, throwable);
        String traceId = (String) request.getAttribute(Const.TRACE_ID);

        HashMap<Object, Object> map = ThreadContext.getHashMap();
        map.put("exception", throwable.getClass().getCanonicalName());
        map.put("message",   throwable.getMessage());

        HttpStatus internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        Result<Map<Object, Object>> body = Result.<Map<Object, Object>>build()
                                                                              .code   (internalServerError.value())
                                                                              .traceId(traceId)
                                                                              .data   (map);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(body, headers, internalServerError);
    }
}
