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

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Hong Qiaowei
 */
public abstract class IOUtils {

    private IOUtils() {
    }

    /**
     * 方法会关闭is
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String toString(InputStream is) throws IOException {
        ByteSource byteSource = new ByteSource() {
            @Override
            public InputStream openStream() {
                return is;
            }
        };
        return byteSource.asCharSource(Charsets.UTF_8).read();
    }
}
