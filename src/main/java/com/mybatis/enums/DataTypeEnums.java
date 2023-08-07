package com.mybatis.enums;

import java.math.BigDecimal;
import java.math.BigInteger;

public enum DataTypeEnums {

    Byte("(Byte)", Byte.class),
    Short("(Short)", Short.class),
    Integer("(Integer)", Integer.class),
    Float("(Float)", Float.class),
    Double("(Double)", Double.class),
    Long("(Long)", Long.class),
    Boolean("(Boolean)", Boolean.class),
    BigInteger("(BigInteger)", java.math.BigInteger.class),
    BigDecimal("(BigDecimal)", java.math.BigDecimal.class),
    String("(String)", String.class),
    Date("(Date)", java.util.Date.class),
    Timestamp("(Timestamp)", java.security.Timestamp.class),
    Time("(Time)", java.sql.Time.class);

    private final String dataType;

    private final Class<?> dataObjClass;

    public static Object getDataType(String data) {
        DataTypeEnums[] typeEnums = values();
        for (DataTypeEnums typeEnum : typeEnums) {
            if (data.contains(typeEnum.getDataType())) {
                data = data.replace(typeEnum.getDataType(), "");
                if (Byte.getDataType().equals(typeEnum.getDataType())) {
                    return java.lang.Byte.valueOf(data);
                }
                if (Short.getDataType().equals(typeEnum.getDataType())) {
                    return java.lang.Short.valueOf(data);
                }
                if (Integer.getDataType().equals(typeEnum.getDataType())) {
                    return java.lang.Integer.valueOf(data);
                }
                if (Float.getDataType().equals(typeEnum.getDataType())) {
                    return java.lang.Float.valueOf(data);
                }
                if (Double.getDataType().equals(typeEnum.getDataType())) {
                    return java.lang.Double.valueOf(data);
                }
                if (Long.getDataType().equals(typeEnum.getDataType())) {
                    return java.lang.Long.valueOf(data);
                }
                if (Boolean.getDataType().equals(typeEnum.getDataType())) {
                    return java.lang.Boolean.valueOf(data);
                }
                if (BigInteger.getDataType().equals(typeEnum.getDataType())) {
                    return new BigInteger(data);
                }
                if (BigDecimal.getDataType().equals(typeEnum.getDataType())) {
                    return new BigDecimal(data);
                }
                if (String.getDataType().equals(typeEnum.getDataType())) {
                    return data;
                }
                if (Date.getDataType().equals(typeEnum.getDataType())) {
                    return data;
                }
                if (Timestamp.getDataType().equals(typeEnum.getDataType())) {
                    return data;
                }
                if (Time.getDataType().equals(typeEnum.getDataType())) {
                    return data;
                }
            }
        }
        return null;
    }



    public Class getDataObjClass() { return this.dataObjClass; }



    public String getDataType() { return this.dataType; }


    DataTypeEnums(String dataType, Class dataObjClass) {
        this.dataType = dataType;
        this.dataObjClass = dataObjClass;
    }
}
