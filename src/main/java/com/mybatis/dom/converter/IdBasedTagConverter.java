package com.mybatis.dom.converter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.*;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import com.mybatis.utils.MapperUtils;
import com.mybatis.utils.MybatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class IdBasedTagConverter extends ConverterAdapter<XmlAttributeValue> implements CustomReferenceConverter<XmlAttributeValue> {
    private final boolean crossMapperSupported;

    public IdBasedTagConverter() {
        this(true);
    }

    protected IdBasedTagConverter(boolean crossMapperSupported) {
        this.crossMapperSupported = crossMapperSupported;
    }

    @Override
    public @Nullable XmlAttributeValue fromString(@Nullable String value, ConvertContext context) {
        return matchIdDomElement(selectStrategy(context).getValue(), value, context).orElse(null);
    }

    private Optional<XmlAttributeValue> matchIdDomElement(Collection<? extends IdDomElement> idDomElements, String value, ConvertContext context) {
        Mapper contextMapper = MapperUtils.getMapper(context.getInvocationElement());
        for (IdDomElement idDomElement : idDomElements) {
            if (MapperUtils.getIdSignature(idDomElement).equals(value) ||
                    MapperUtils.getIdSignature(idDomElement, contextMapper).equals(value)) {
                return Optional.ofNullable(idDomElement.getId().getXmlAttributeValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public @Nullable String toString(@Nullable XmlAttributeValue xmlAttributeValue, ConvertContext context) {
        if (xmlAttributeValue == null) {
            return null;
        }
        DomElement domElement = DomUtil.getDomElement(xmlAttributeValue.getParent().getParent());
        if (!(domElement instanceof IdDomElement)) {
            return null;
        }
        Mapper contextMapper = MapperUtils.getMapper(context.getInvocationElement());
        return MapperUtils.getIdSignature((IdDomElement) domElement, contextMapper);
    }

    @NotNull
    public PsiReference[] createReferences(GenericDomValue<XmlAttributeValue> value, PsiElement element, ConvertContext context) {
        return PsiClassConverter.createJavaClassReferenceProvider(value, null, new ValueReferenceProvider(context)).getReferencesByElement(element);
    }

    @NotNull
    public abstract Collection<? extends IdDomElement> getComparisons(@Nullable Mapper paramMapper, ConvertContext paramConvertContext);

    private TraverseStrategy selectStrategy(ConvertContext context) {
        return this.crossMapperSupported ? new CrossMapperStrategy(context) : new InsideMapperStrategy(context);
    }

    private class ValueReference extends PsiReferenceBase<PsiElement> {
        private final ConvertContext context;
        private final String text;

        public ValueReference(PsiElement element, TextRange rng, ConvertContext context, String text) {
            super(element, rng, false);
            this.context = context;
            this.text = text;
        }

        @Override
        public @Nullable PsiElement resolve() {
            return IdBasedTagConverter.this.fromString(this.text, this.context);
        }

        @Override
        public @NotNull Object[] getVariants() {
            Set<String> res = getElement().getText().contains(MybatisConstants.DOT_SEPARATOR) ? setupContextIdSignature() : setupGlobalIdSignature();
            return res.toArray((Object[]) new String[res.size()]);
        }

        private Set<String> setupContextIdSignature() {
            Set<String> res = Sets.newHashSet();
            String ns = this.text.substring(0, this.text.lastIndexOf(MybatisConstants.DOT_SEPARATOR));
            for (IdDomElement ele : IdBasedTagConverter.this.selectStrategy(this.context).getValue()) {
                if (MapperUtils.getNamespace(ele).equals(ns)) {
                    res.add(MapperUtils.getId(ele));
                }
            }
            return res;
        }

        private Set<String> setupGlobalIdSignature() {
            Mapper contextMapper = MapperUtils.getMapper(this.context.getInvocationElement());
            Collection<? extends IdDomElement> idDomElements = IdBasedTagConverter.this.selectStrategy(this.context).getValue();
            Set<String> res = new HashSet<>(idDomElements.size());
            for (IdDomElement ele : idDomElements) {
                res.add(MapperUtils.getIdSignature(ele, contextMapper));
            }
            return res;
        }
    }

    private abstract class TraverseStrategy {
        protected ConvertContext context;

        public TraverseStrategy(ConvertContext context) {
            this.context = context;
        }

        public abstract Collection<? extends IdDomElement> getValue();
    }


    private class InsideMapperStrategy extends TraverseStrategy {
        public InsideMapperStrategy(ConvertContext context) {
            super(context);
        }


        public Collection<? extends IdDomElement> getValue() {
            return getComparisons(null, this.context);
        }
    }

    private class CrossMapperStrategy extends TraverseStrategy {
        public CrossMapperStrategy(@NotNull ConvertContext context) {
            super(context);
        }

        @Override
        public Collection<? extends IdDomElement> getValue() {
            List<IdDomElement> result = Lists.newArrayList();
            for (Mapper mapper : MapperUtils.findMappers(context.getProject())) {
                result.addAll(getComparisons(mapper, context));
            }
            return result;
        }
    }

    private class ValueReferenceProvider extends JavaClassReferenceProvider {
        private final ConvertContext context;

        public ValueReferenceProvider(ConvertContext context) {
            this.context = context;
        }

        @Override
        public @Nullable GlobalSearchScope getScope(@NotNull Project project) {
            return GlobalSearchScope.allScope(project);
        }

        @Override
        public @NotNull PsiReference[] getReferencesByString(String text, @NotNull PsiElement position, int offsetInPosition) {
            PsiReference[] javaClassReferences = super.getReferencesByString(text, position, offsetInPosition);
            List<PsiReference> refs = Lists.newArrayList(javaClassReferences);
            IdBasedTagConverter.ValueReference vr = new IdBasedTagConverter.ValueReference(position, getTextRange(position), this.context, text);
            if (!refs.isEmpty() && 0 != (vr.getVariants()).length) {
                refs.remove(refs.size() - 1);
                refs.add(vr);
            }
            return refs.toArray(new PsiReference[0]);
        }

        private TextRange getTextRange(PsiElement element) {
            String text = element.getText();
            int index = text.lastIndexOf(MybatisConstants.DOT_SEPARATOR);
            return (-1 == index) ? ElementManipulators.getValueTextRange(element) : TextRange.create(text.substring(0, index).length() + 1, text.length() - 1);
        }
    }
}
