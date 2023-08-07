package com.mybatis.enums;

import com.mybatis.utils.StringUtils;

public enum MethodNameEnums {
    DELETE("delete", 0),
    INSERT("insert", 0),
    UPDATE("update", 0),
    SELECT("select", 0),
    FIND("find", 0),
    BY("By", 0),
    AND("And", 0),
    IN("In", 1),
    NOT_INT("NotIn", 1),
    ALL("All", 2),
    LIKE("Like", 1),
    GROUP_BY("GroupBy", 2),
    GROUP("Group", 2),
    HAVING("Having ", 2),
    OR("Or", 1),
    LESS_THAN("LessThan", 1),
    LESS_THAN_EQUAL("LessThanEqual", 1),
    GREATER_THAN("GreaterThan", 1),
    GREATER_THAN_EQUAL("GreaterThanEqual", 1),
    ORDER("Order", 1),
    //    ORDER_BY_DESC("OrderBy",1),
    DESC("Desc", 1),
    ASC("Asc", 1),
    ORDER_BY("OrderBy", 1);

    private final String key;
    private final int methodType;

    MethodNameEnums(String key, int methodType) {
        this.key = key;
        this.methodType = methodType;
    }

    public static boolean isContain(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        for (MethodNameEnums enums : MethodNameEnums.values()) {
            if (enums.getKey().contains(key)) {
                return true;
            }
        }
        return false;
    }


    public int getType() {
        return methodType;
    }

    public String getKey() {
        return key;
    }

}
