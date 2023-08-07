package com.mybatis.utils;

public class CommonDataTableUtils {
    public static boolean isIU() {
        try {
            Class.forName("com.intellij.database.psi.DbTable");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
