package com.mybatis.dom.converter;

import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public abstract class ConverterAdapter<T> extends ResolvingConverter<T> {
    @Override
    public @Nullable String toString(@Nullable T t, ConvertContext context) {
        return null;
    }

    @Override
    public @NotNull Collection<? extends T> getVariants(ConvertContext context) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable T fromString(@Nullable String text, ConvertContext context) {
        return null;
    }
}
