package com.mybatis.dom.converter;

import com.intellij.util.xml.ConvertContext;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ParameterMapConverter extends IdBasedTagConverter {
    @Override
    public @NotNull Collection<? extends IdDomElement> getComparisons(@Nullable Mapper paramMapper, ConvertContext paramConvertContext) {
        assert paramMapper != null;
        return paramMapper.getParameterMaps();
    }
}
