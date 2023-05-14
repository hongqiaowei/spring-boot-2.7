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
public abstract class SqlUtils {

    private SqlUtils() {
    }

    public static String toCamelCase(String column, char separator) {
        // column = column.toLowerCase();
        StringBuilder b = new StringBuilder(column.length());
        boolean toUpperCase = false;
        for (int i = 0; i < column.length(); i++) {
            char c = column.charAt(i);
            if (c == separator) {
                toUpperCase = true;
            } else if (toUpperCase) {
                b.append(Character.toUpperCase(c));
                toUpperCase = false;
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }
}
