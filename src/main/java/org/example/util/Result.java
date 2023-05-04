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

package org.example.util;

import java.util.Collections;
import java.util.Map;

/**
 * @author Hong Qiaowei
 */
public class Result<D> {

    public static final int SUCCESS = 1;
    public static final int FAIL    = 0;


    private int                 code        =  SUCCESS;

    private String              msg;

    private D                   data;

    private Throwable           throwable;

    private Map<Object, Object> context     =  Collections.emptyMap();

    private String              traceId;


    private Result(int code, String msg, D data, Throwable throwable, Map<Object, Object> context, String traceId) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.throwable = throwable;
        this.context = context;
        this.traceId = traceId;
    }

    /**
     * 构建Result的方法：Result&lt;String&gt; result = Result.&lt;String&gt;build().code(6).msg("8").data("9");
     */
    public static <D> Result<D> build() {
        return new Result<D>(SUCCESS, null, null, null, null, null);
    }
    public Result<D> code(int c) {
        code = c;
        return this;
    }
    public Result<D> msg(String m) {
        msg = m;
        return this;
    }
    public Result<D> data(D d) {
        data = d;
        return this;
    }
    public Result<D> throwable(Throwable t) {
        throwable = t;
        return this;
    }
    public Result<D> context(Map<Object, Object> c) {
        context = c;
        return this;
    }
    public Result<D> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Map<Object, Object> getContext() {
        return context;
    }

    public void setContext(Map<Object, Object> context) {
        this.context = context;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return JacksonUtils.writeValueAsString(this);
    }
}
