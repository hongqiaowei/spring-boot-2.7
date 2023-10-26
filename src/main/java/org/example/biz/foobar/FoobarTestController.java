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

package org.example.biz.foobar;

import org.example.util.Const;
import org.example.util.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Hong Qiaowei
 */
@Controller
@RequestMapping("/foobar")
public class FoobarTestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoobarTestController.class);

    @PostMapping(value = "/httpTest")
    public ResponseEntity<Map<String, String>> httpTest(HttpServletRequest request, @RequestHeader(name = "h1") String header1, @RequestBody Map<String, String> body) {
        String traceId = (String) request.getAttribute(Const.TRACE_ID);
        String protocol = request.getProtocol();
        body.put(Const.TRACE_ID, traceId);
        body.put("protocol", protocol);
        body.put("h1", header1);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/formTest", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<MultiValueMap<String, String>> formTest(@RequestBody MultiValueMap<String, String> body) {
        LinkedMultiValueMap<String, String> responseBody = new LinkedMultiValueMap<>();
        responseBody.putAll(body);
        responseBody.add("fieldx", "valuex");
        return ResponseEntity.of(Optional.of(responseBody));
    }

    @GetMapping("/xxx")
    @ResponseBody
    public Map<String, String> xxx() {
        Map<String, String> m = new HashMap<>();
        m.put("111", "222");
        return m;
    }

    @GetMapping(value = "/yyy", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String yyy() {
        Map<String, String> m = new HashMap<>();
        m.put("111", "222");
        return JacksonUtils.writeValueAsString(m);
    }

    @GetMapping(value = "/zzz")
    public ResponseEntity<String> zzz() {
        Map<String, String> m = new HashMap<>();
        m.put("111", "222");
        String body = JacksonUtils.writeValueAsString(m);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
