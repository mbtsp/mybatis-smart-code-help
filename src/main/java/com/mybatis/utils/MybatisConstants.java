package com.mybatis.utils;

import com.intellij.psi.util.ReferenceSetBase;

public class MybatisConstants {
    public static final String DOT_SEPARATOR = String.valueOf(ReferenceSetBase.DOT_SEPARATOR);
    public static final double PRIORITY = 400.0D;

    private MybatisConstants() {
        throw new UnsupportedOperationException();
    }
}
