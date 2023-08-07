package com.mybatis.utils;

import com.intellij.openapi.util.text.StringUtil;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;

public class JdbcUtils {
    protected static final Map<Integer, JavaTypeResolverDefaultImpl.JdbcTypeInformation> typeMap = new HashMap<>();

    static {
        typeMap.put(Types.ARRAY, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("ARRAY", //$NON-NLS-1$
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.BIGINT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BIGINT", //$NON-NLS-1$
                new FullyQualifiedJavaType(Long.class.getName())));
        typeMap.put(Types.BINARY, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BINARY", //$NON-NLS-1$
                new FullyQualifiedJavaType("byte[]"))); //$NON-NLS-1$
        typeMap.put(Types.BIT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BIT", //$NON-NLS-1$
                new FullyQualifiedJavaType(Boolean.class.getName())));
        typeMap.put(Types.BLOB, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BLOB", //$NON-NLS-1$
                new FullyQualifiedJavaType("byte[]"))); //$NON-NLS-1$
        typeMap.put(Types.BOOLEAN, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BOOLEAN", //$NON-NLS-1$
                new FullyQualifiedJavaType(Boolean.class.getName())));
        typeMap.put(Types.CHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("CHAR", //$NON-NLS-1$
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.CLOB, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("CLOB", //$NON-NLS-1$
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.DATALINK, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DATALINK", //$NON-NLS-1$
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.DATE, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DATE", //$NON-NLS-1$
                new FullyQualifiedJavaType(Date.class.getName())));
        typeMap.put(Types.DECIMAL, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DECIMAL", //$NON-NLS-1$
                new FullyQualifiedJavaType(BigDecimal.class.getName())));
        typeMap.put(Types.DISTINCT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DISTINCT", //$NON-NLS-1$
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.DOUBLE, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DOUBLE", //$NON-NLS-1$
                new FullyQualifiedJavaType(Double.class.getName())));
        typeMap.put(Types.FLOAT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("FLOAT", //$NON-NLS-1$
                new FullyQualifiedJavaType(Double.class.getName())));
        typeMap.put(Types.INTEGER, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("INTEGER", //$NON-NLS-1$
                new FullyQualifiedJavaType(Integer.class.getName())));
        typeMap.put(Types.JAVA_OBJECT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("JAVA_OBJECT", //$NON-NLS-1$
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.LONGNVARCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("LONGNVARCHAR", //$NON-NLS-1$
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.LONGVARBINARY, new JavaTypeResolverDefaultImpl.JdbcTypeInformation(
                "LONGVARBINARY", //$NON-NLS-1$
                new FullyQualifiedJavaType("byte[]"))); //$NON-NLS-1$
        typeMap.put(Types.LONGVARCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("LONGVARCHAR", //$NON-NLS-1$
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.NCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NCHAR", //$NON-NLS-1$
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.NCLOB, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NCLOB", //$NON-NLS-1$
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.NVARCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NVARCHAR", //$NON-NLS-1$
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.NULL, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NULL", //$NON-NLS-1$
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.NUMERIC, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NUMERIC", //$NON-NLS-1$
                new FullyQualifiedJavaType(BigDecimal.class.getName())));
        typeMap.put(Types.OTHER, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("OTHER", //$NON-NLS-1$
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.REAL, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("REAL", //$NON-NLS-1$
                new FullyQualifiedJavaType(Float.class.getName())));
        typeMap.put(Types.REF, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("REF", //$NON-NLS-1$
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.SMALLINT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("SMALLINT", //$NON-NLS-1$
                new FullyQualifiedJavaType(Short.class.getName())));
        typeMap.put(Types.STRUCT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("STRUCT", //$NON-NLS-1$
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.TIME, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TIME", //$NON-NLS-1$
                new FullyQualifiedJavaType(Date.class.getName())));
        typeMap.put(Types.TIMESTAMP, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TIMESTAMP", //$NON-NLS-1$
                new FullyQualifiedJavaType(Date.class.getName())));
        typeMap.put(Types.TINYINT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TINYINT", //$NON-NLS-1$
                new FullyQualifiedJavaType(Byte.class.getName())));
        typeMap.put(Types.VARBINARY, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("VARBINARY", //$NON-NLS-1$
                new FullyQualifiedJavaType("byte[]"))); //$NON-NLS-1$
        typeMap.put(Types.VARCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("VARCHAR", //$NON-NLS-1$
                new FullyQualifiedJavaType(String.class.getName())));
        // JDK 1.8 types
        typeMap.put(Types.TIME_WITH_TIMEZONE, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TIME_WITH_TIMEZONE", //$NON-NLS-1$
                new FullyQualifiedJavaType("java.time.OffsetTime"))); //$NON-NLS-1$
        typeMap.put(Types.TIMESTAMP_WITH_TIMEZONE, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TIMESTAMP_WITH_TIMEZONE", //$NON-NLS-1$
                new FullyQualifiedJavaType("java.time.OffsetDateTime"))); //$NON-NLS-1$
    }

    /**
     * Convert type name to jdbc type int.
     *
     * @param jdbcTypeName the jdbc type name
     * @param size         the size
     * @param databaseType the database type
     * @return the int
     */
    public static int convertTypeNameToJdbcType(String jdbcTypeName, int size, String databaseType) {
        if (StringUtil.isEmpty(jdbcTypeName)) {
            return 1111;
        } else {
            String fixed = jdbcTypeName.toUpperCase();
            if (fixed.contains("BIGINT")) {
                return -5;
            } else if (fixed.contains("TINYINT")) {
                return size == 1 ? 16 : -6;
            } else if (fixed.contains("LONGVARBINARY")) {
                return -4;
            } else if (fixed.contains("VARBINARY")) {
                return -3;
            } else if (fixed.contains("LONGVARCHAR") || fixed.contains("LONGTEXT")) {
                return -1;
            } else if (fixed.contains("SMALLINT")) {
                return 5;
            } else if (fixed.contains("DATETIME")) {
                return 93;
            } else if (fixed.equals("DATE") && databaseType.equals("Oracle")) {
                return 93;
            } else if (fixed.contains("NUMBER")) {
                return 3;
            } else if (fixed.contains("BOOLEAN")) {
                return 16;
            } else if (fixed.contains("BINARY")) {
                return -3;
            } else if (fixed.contains("BIT")) {
                return -7;
            } else if (fixed.contains("BOOL")) {
                return 16;
            } else if (fixed.contains("DATE")) {
                return 91;
            } else if (fixed.contains("TIMESTAMP")) {
                return 93;
            } else if (fixed.contains("TIME")) {
                return 92;
            } else if (!fixed.contains("REAL") && !fixed.contains("NUMBER")) {
                if (fixed.contains("FLOAT")) {
                    return 6;
                } else if (fixed.contains("DOUBLE")) {
                    return 8;
                } else if (fixed.equals("CHAR") && !fixed.contains("VAR")) {
                    return 1;
                } else if (fixed.contains("INT") && !fixed.contains("INTERVAL")) {
                    return 4;
                } else if (fixed.contains("DECIMAL")) {
                    return 3;
                } else if (fixed.contains("NUMERIC")) {
                    return 2;
                } else if (!fixed.contains("CHAR") && !fixed.contains("TEXT")) {
                    if (fixed.contains("BLOB")) {
                        return 2004;
                    } else if (fixed.contains("CLOB")) {
                        return 2005;
                    } else {
                        return fixed.contains("REFERENCE") ? 2006 : 1111;
                    }
                } else {
                    return 12;
                }
            } else {
                return 7;
            }
        }
    }

    public static FullyQualifiedJavaType convertJavaType(int jdbcType, int size, boolean userLocalDate) {
        JavaTypeResolverDefaultImpl.JdbcTypeInformation jdbcTypeInformation = typeMap
                .get(jdbcType);
        FullyQualifiedJavaType answer = null;
        if (jdbcTypeInformation != null) {
            answer = jdbcTypeInformation.getFullyQualifiedJavaType();
        }

        answer = getFullyQualifiedJavaType(jdbcType, size, userLocalDate, answer);
        return answer;
    }

    public static JavaTypeResolverDefaultImpl.JdbcTypeInformation convertJdbcTypeInformation(int jdbcType, int size, boolean userLocalDate) {
        JavaTypeResolverDefaultImpl.JdbcTypeInformation jdbcTypeInformation = typeMap
                .get(jdbcType);
        if (jdbcTypeInformation == null) {
            jdbcTypeInformation = typeMap.get(1111);
            return jdbcTypeInformation;
        }
        FullyQualifiedJavaType answer = jdbcTypeInformation.getFullyQualifiedJavaType();

        answer = getFullyQualifiedJavaType(jdbcType, size, userLocalDate, answer);
        jdbcTypeInformation = new JavaTypeResolverDefaultImpl.JdbcTypeInformation(jdbcTypeInformation.getJdbcTypeName(), answer);
        return jdbcTypeInformation;
    }

    private static FullyQualifiedJavaType getFullyQualifiedJavaType(int jdbcType, int size, boolean userLocalDate, FullyQualifiedJavaType answer) {
        switch (jdbcType) {
            case Types.BIT:
                if (size > 1) {
                    answer = new FullyQualifiedJavaType("byte[]");
                }
                break;
            case Types.DATE:
                if (userLocalDate) {
                    answer = new FullyQualifiedJavaType("java.time.LocalDate"); //$NON-NLS-1$
                }
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                if (size > 18) {
                    answer = new FullyQualifiedJavaType(BigDecimal.class.getName());
                } else if (size > 9 && size < 18) {
                    answer = new FullyQualifiedJavaType(Long.class.getName());
                } else if (size > 4 && size < 9) {
                    answer = new FullyQualifiedJavaType(Integer.class.getName());
                } else {
                    answer = new FullyQualifiedJavaType(Short.class.getName());
                }
                break;
            case Types.TIME:
                if (userLocalDate) {
                    answer = new FullyQualifiedJavaType("java.time.LocalTime"); //$NON-NLS-1$
                }
                break;
            case Types.TIMESTAMP:
                if (userLocalDate) {
                    answer = new FullyQualifiedJavaType("java.time.LocalDateTime"); //$NON-NLS-1$
                }
                break;
            default:
                break;
        }
        return answer;
    }


    public static List<String> getJdbcTypes() {
        List<String> jdbcTypes = new ArrayList<>();
        if (typeMap.isEmpty()) {
            return jdbcTypes;
        }
        for (Map.Entry<Integer, JavaTypeResolverDefaultImpl.JdbcTypeInformation> entry : typeMap.entrySet()) {
            jdbcTypes.add(entry.getValue().getJdbcTypeName());
        }
        return jdbcTypes;
    }

    public static List<String> getJavaTypes() {
        List<String> javaTypes = new ArrayList<>();
        javaTypes.add(new FullyQualifiedJavaType("java.time.LocalDate").getFullyQualifiedName());
        javaTypes.add(new FullyQualifiedJavaType("java.time.LocalTime").getFullyQualifiedName());
        javaTypes.add(new FullyQualifiedJavaType("java.time.LocalDateTime").getFullyQualifiedName());
        if (typeMap.isEmpty()) {
            return javaTypes;
        }
        for (Map.Entry<Integer, JavaTypeResolverDefaultImpl.JdbcTypeInformation> entry : typeMap.entrySet()) {
            javaTypes.add(entry.getValue().getFullyQualifiedJavaType().getFullyQualifiedName());
        }
        return javaTypes;
    }
}
