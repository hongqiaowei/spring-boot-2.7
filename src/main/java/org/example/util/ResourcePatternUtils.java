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

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Hong Qiaowei
 */
public abstract class ResourcePatternUtils extends org.springframework.core.io.support.ResourcePatternUtils {

    private static final PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

    private ResourcePatternUtils() {
    }

    public static Resource getResource(String location) {
        return pathMatchingResourcePatternResolver.getResource(location);
    }

    public static Properties resource2properties(String location) throws IOException {
        Resource resource = getResource(location);
        InputStream is = resource.getInputStream();
        Properties p = new Properties();
        p.load(is);
        return p;
    }
}
