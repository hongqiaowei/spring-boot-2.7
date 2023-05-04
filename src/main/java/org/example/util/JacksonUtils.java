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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * @author Hong Qiaowei
 */
public abstract class JacksonUtils {

    private static final ObjectMapper mapper;

    static {
        JsonFactory f = new JsonFactory();
        f.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,        true);
        f.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        mapper = new ObjectMapper(f);

        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.configure( SerializationFeature.  WRITE_ENUMS_USING_TO_STRING,            true);
        mapper.configure( SerializationFeature.  WRITE_EMPTY_JSON_ARRAYS,                true); // FIXME
        mapper.configure( SerializationFeature.  WRITE_NULL_MAP_VALUES,                  true);
        mapper.configure( DeserializationFeature.READ_ENUMS_USING_TO_STRING,             true);
        mapper.configure( DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,              true);
        mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,             false);
//      mapper.configure( JsonParser.Feature.    ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure( JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapper.registerModule(javaTimeModule);
    }

    private JacksonUtils() {
    }

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    public static TypeFactory getTypeFactory() {
        return mapper.getTypeFactory();
    }

    public static <T> T readValue(byte[] bytes, Class<T> clz) {
        try {
            return mapper.readValue(bytes, clz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(byte[] bytes, TypeReference<T> typeRef) {
        try {
            return mapper.readValue(bytes, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(byte[] bytes, JavaType javaType) {
        try {
            return mapper.readValue(bytes, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(byte[] bytes, int offset, int length, Class<T> clz) {
        try {
            return mapper.readValue(bytes, offset, length, clz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(String json, Class<T> clz) {
        try {
            return mapper.readValue(json, clz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(String json, TypeReference<T> typeRef) {
        try {
            return mapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(String json, JavaType javaType) {
        try {
            return mapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] writeValueAsBytes(Object value) {
        try {
            return mapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String writeValueAsString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
