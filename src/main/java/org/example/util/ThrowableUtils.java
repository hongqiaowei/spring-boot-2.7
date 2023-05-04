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

/**
 * @author Hong Qiaowei
 */
public abstract class ThrowableUtils {

    private ThrowableUtils() {
        // zEzMuasv+SRjA==
    }

    public static RuntimeException runtimeExceptionWithoutStack(String msg) {
        return new RuntimeException(msg, null, false, false) {
        };
    }

    public static Exception exceptionWithoutStack(String msg) {
        return new Exception(msg, null, false, false) {
        };
    }

    public static Error errorWithoutStack(String msg) {
        return new Error(msg, null, false, false) {
        };
    }

    public static Throwable throwableWithoutStack(String msg) {
        return new Throwable(msg, null, false, false) {
        };
    }

}
