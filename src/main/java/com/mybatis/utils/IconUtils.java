package com.mybatis.utils;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ReflectionUtil;

import javax.swing.*;
import java.util.Objects;

public class IconUtils {
    public static final Icon JAVA_MYBATIS_ICON = IconLoader.getIcon("/icon/mybatisClass.svg", Objects.requireNonNull(ReflectionUtil.getGrandCallerClass()));
    public static final Icon XML_MYBATIS_ICON = IconLoader.getIcon("/icon/mybatisXml.svg", ReflectionUtil.getGrandCallerClass());
    public static final Icon JAVA_MIN_MYBATIS_ICON = IconLoader.getIcon("/icon/minMybatis.svg", ReflectionUtil.getGrandCallerClass());
    public static final Icon WX = IconLoader.getIcon("/icon/wx.png", ReflectionUtil.getGrandCallerClass());
    public static final Icon ZFB = IconLoader.getIcon("/icon/zfb.png", ReflectionUtil.getGrandCallerClass());
//    public static final Icon SPRING_INJECTION_ICON = IconLoader.getIcon("/icon/injection.png", ReflectionUtil.getGrandCallerClass());
}
