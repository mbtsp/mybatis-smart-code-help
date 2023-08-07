package com.mybatis.utils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcTypeUtils {
    /**
     * 后续提取类， 先用静态的
     *
     * @return
     */
    private static Map<String, String> fieldJdbcType = new ConcurrentHashMap<>();

    static {
        fieldJdbcType.put("int", "INTEGER");
        fieldJdbcType.put("long", "BIGINT");
        fieldJdbcType.put("boolean", "BOOLEAN");
        fieldJdbcType.put("float", "FLOAT");
        fieldJdbcType.put("byte", "TINYINT");
        fieldJdbcType.put("double", "DOUBLE");
        fieldJdbcType.put("char", "VARCHAR");
        fieldJdbcType.put("short", "NUMERIC");

        fieldJdbcType.put("java.lang.Byte", "TINYINT");
        fieldJdbcType.put("java.lang.Short", "NUMERIC");
        fieldJdbcType.put("java.lang.Integer", "INTEGER");
        fieldJdbcType.put("java.lang.Long", "BIGINT");
        fieldJdbcType.put("java.lang.Float", "FLOAT");
        fieldJdbcType.put("java.lang.Double", "DOUBLE");
        fieldJdbcType.put("java.lang.Boolean", "BOOLEAN");
        fieldJdbcType.put("java.lang.String", "VARCHAR");
        fieldJdbcType.put("java.util.Date", "TIMESTAMP");
        fieldJdbcType.put("java.math.BigDecimal", "DECIMAL");
    }

    /**
     * Wrapper field string.
     *
     * @param paramName     the param name
     * @param canonicalText the canonical text
     * @return the string
     */
    public static String wrapperField(String paramName, @NotNull String canonicalText) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#{").append(paramName);
        String jdbcType = fieldJdbcType.get(canonicalText);
        if (StringUtils.isNotBlank(jdbcType)) {
            stringBuilder.append(",jdbcType=").append(jdbcType);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public static Optional<String> findJdbcTypeByJavaType(String javaType) {
        return Optional.ofNullable(fieldJdbcType.get(javaType));
    }
}
