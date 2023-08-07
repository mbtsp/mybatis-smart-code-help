package com.mybatis.handler;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMethodNameHandler implements BaseMethodNameHandler {
    public final List<String> methodNames;
    public List<String> contributorMethodNames;

    protected AbstractMethodNameHandler() {
        methodNames = new ArrayList<>();
        contributorMethodNames = new ArrayList<>();
    }
}
