package com.mybatis.dom.converter;

import com.google.common.collect.Collections2;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import com.mybatis.dom.model.ResultMap;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ResultMapConverter extends IdBasedTagConverter {
    @NotNull
    public Collection<? extends IdDomElement> getComparisons(@Nullable Mapper mapper, ConvertContext context) {
        assert mapper != null;
        DomElement invocationElement = context.getInvocationElement();
        if (isContextElementOfResultMap(mapper, invocationElement)) {
            return doFilterResultMapItself(mapper, (ResultMap) invocationElement.getParent());
        }
        return mapper.getResultMaps();
    }


    private boolean isContextElementOfResultMap(Mapper mapper, DomElement invocationElement) {
        return (MapperUtils.isMapperWithSameNamespace(MapperUtils.getMapper(invocationElement), mapper) && invocationElement
                .getParent() instanceof ResultMap);
    }

    private Collection<? extends IdDomElement> doFilterResultMapItself(Mapper mapper, final ResultMap resultMap) {
        return Collections2.filter(mapper.getResultMaps(), input -> !MapperUtils.getId((IdDomElement) input).equals(MapperUtils.getId((IdDomElement) resultMap)));
    }

}
