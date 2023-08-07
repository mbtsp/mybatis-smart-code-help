package com.mybatis.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static String upperCaseFirstChar(String _str) {
        if (_str == null) {
            return null;
        }
        return _str.isEmpty() ? _str : (_str.substring(0, 1).toUpperCase() + _str.substring(1));
    }

    /**
     * 驼峰转下划线
     *
     * @param camelStr
     * @return
     */
    public static String camelToSlash(String camelStr) {
        String[] strings = splitByCharacterType(camelStr, true);
        return Arrays.stream(strings).map(StringUtils::lowerCaseFirstChar).collect(Collectors.joining("_"));
    }

    private static String[] splitByCharacterType(String str, boolean camelCase) {
        if (str == null) {
            return null;
        } else if (str.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            char[] c = str.toCharArray();
            List<String> list = new ArrayList();
            int tokenStart = 0;
            int currentType = Character.getType(c[tokenStart]);

            for (int pos = tokenStart + 1; pos < c.length; ++pos) {
                int type = Character.getType(c[pos]);
                if (type != currentType) {
                    if (camelCase && type == 2 && currentType == 1) {
                        int newTokenStart = pos - 1;
                        if (newTokenStart != tokenStart) {
                            list.add(new String(c, tokenStart, newTokenStart - tokenStart));
                            tokenStart = newTokenStart;
                        }
                    } else {
                        list.add(new String(c, tokenStart, pos - tokenStart));
                        tokenStart = pos;
                    }

                    currentType = type;
                }
            }

            list.add(new String(c, tokenStart, c.length - tokenStart));
            return (String[]) list.toArray(new String[list.size()]);
        }
    }

    public static String lowerCaseFirstChar(String _str) {
        if (_str == null) {
            return null;
        }
        return _str.isEmpty() ? _str : (_str.substring(0, 1).toLowerCase() + _str.substring(1));
    }

    public static String getUpperCamelFromAny(String value) {
        String[] split = value.split("[_\\-]");
        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            boolean allBig = StringIsAllCapital(s);
            if (allBig) {
                builder.append(capitalize(s.toLowerCase()));
            } else {
                builder.append(capitalize(s));
            }
        }
        return builder.toString();
    }

    private static boolean StringIsAllCapital(String s) {
        boolean allBig = true;
        for (int j = 0; j < s.length(); j++) {
            if (s.charAt(j) >= 'a' && s.charAt(j) <= 'z') {
                allBig = false;
                break;
            }
        }
        return allBig;
    }

    public static String dbStringToCamelStyle(String str) {
        if (str != null) {
            str = str.toLowerCase();
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(str.charAt(0)).toUpperCase());
            for (int i = 1; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c != '_') {
                    sb.append(c);
                } else if (i + 1 < str.length()) {
                    sb.append(String.valueOf(str.charAt(i + 1)).toUpperCase());
                    i++;
                }
            }

            return sb.toString();
        }
        return null;
    }

    /**
     * 检测第一字母是不是大写
     *
     * @param name
     * @return
     */
    public static boolean checkFirstCharUpperCase(String name) {
        return Character.isUpperCase(name.charAt(0));
    }
}
