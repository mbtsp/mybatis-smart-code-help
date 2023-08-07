package com.mybatis.utils;

public enum MybatisParameterType {
    INT("int", "java.lang.Integer"),
    LONG("long", "java.lang.Long"),
    STRING("string", "java.lang.String"),
    DATE("date", "java.util.Date"),
    BOOLEAN("boolean", "java.lang.Boolean"),
    MAP("map", "java.util.Map<String,Object>");
    private final String key;
    private final String value;

    MybatisParameterType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static boolean contain(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        for (MybatisParameterType mybatisParameterType : MybatisParameterType.values()) {
            if (key.equals(mybatisParameterType.key)) {
                return true;
            }
        }
        return false;
    }

    public static MybatisParameterType getInstance(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        for (MybatisParameterType mybatisParameterType : MybatisParameterType.values()) {
            if (key.equals(mybatisParameterType.key)) {
                return mybatisParameterType;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
}
