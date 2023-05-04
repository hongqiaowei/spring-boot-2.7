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
// import org.example.util.Const;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.web.filter.OncePerRequestFilter;
//
// import javax.servlet.FilterChain;
// import javax.servlet.ServletException;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import java.io.IOException;
//
// /**
//  * @author Hong Qiaowei
//  */
// class CORSFilter extends OncePerRequestFilter {
//
//     private static final String options_method = HttpMethod.OPTIONS.name();
//     private static final String asterisk_      = String.valueOf(Const.S.ASTERISK);
//     private static final String allow_methods  = "OPTIONS, POST, GET, PUT, DELETE, HEAD";
//
//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//         if (request.getMethod().equals(options_method)) {
//             response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,  asterisk_);
//             response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allow_methods);
//             response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, asterisk_);
//         }
//         filterChain.doFilter(request, response);
//     }
//
// }