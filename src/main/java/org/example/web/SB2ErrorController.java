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
import org.example.util.DateTimeUtils;
import org.example.util.Result;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 主要处理filter层面抛出的异常
 * @see SB2ControllerAdvice
 * @author Hong Qiaowei
 */
public class SB2ErrorController extends BasicErrorController {

    public SB2ErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
    }

    public SB2ErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorProperties, errorViewResolvers);
    }

    @Override
    @RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity error(HttpServletRequest request) {

        ErrorAttributeOptions errorAttributeOptions = ErrorAttributeOptions.of(
                                                            // ErrorAttributeOptions.Include.STACK_TRACE,
                                                            ErrorAttributeOptions.Include.EXCEPTION,
                                                            ErrorAttributeOptions.Include.MESSAGE,
                                                            ErrorAttributeOptions.Include.BINDING_ERRORS
                                                      );

        Map<String, Object> error = getErrorAttributes(request, errorAttributeOptions);
        HttpStatus status = getStatus(request);

        Date timestamp = (Date) error.get("timestamp");
        String convert = DateTimeUtils.toString(timestamp.getTime(), Const.DP.DP23);
        error.put("timestamp", convert);

        String traceId = (String) request.getAttribute(Const.TRACE_ID);

        Result<Map<String, Object>> body = Result.<Map<String, Object>>build()
                                                                              .code   (HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                                              .traceId(traceId)
                                                                              .data   (error);

        return new ResponseEntity<>(body, status);
    }
}
