package com.mybatis.utils;

import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.List;

public class MethodUtils {
    public static Method copy(Method method) {
        Method method1 = new Method(method.getName());
        if (method.getReturnType().isPresent()) {
            method1.setReturnType(method.getReturnType().get());
        }
        method1.setVisibility(method.getVisibility());
        method.getExceptions().forEach(method1::addException);
        method1.setAbstract(method.isAbstract());
        method1.setFinal(method.isFinal());
        method1.setStatic(method.isStatic());
        method1.setNative(method.isNative());
        method1.setSynchronized(method.isSynchronized());
        method1.setConstructor(method.isConstructor());
        method.getJavaDocLines().forEach(method1::addJavaDocLine);
        method.getBodyLines().forEach(method1::addBodyLine);
        method1.setDefault(method.isDefault());
        method.getTypeParameters().forEach(method1::addTypeParameter);
        return method1;
    }

    public static void clearParameterAnnotations(Method method, Method targetMethod) {
        List<Parameter> parameterLists = method.getParameters();
        if (parameterLists != null) {
            parameterLists.forEach(parameter -> {
                Parameter parameter1 = new Parameter(parameter.getType(), parameter.getName(), parameter.isVarargs());
                targetMethod.addParameter(parameter1);
            });
        }
    }
}
