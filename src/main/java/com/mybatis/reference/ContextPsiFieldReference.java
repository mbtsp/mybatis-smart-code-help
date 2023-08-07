package com.mybatis.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.mybatis.dom.utils.MapperBacktrackingUtils;
import com.mybatis.service.JavaService;
import com.mybatis.utils.JavaUtils;
import com.mybatis.utils.MybatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ContextPsiFieldReference extends PsiReferenceBase<XmlAttributeValue> {
    protected ContextReferenceSetResolver<XmlAttributeValue, PsiField> resolver;
    protected int index;

    public ContextPsiFieldReference(XmlAttributeValue xmlAttributeValue, TextRange range, int index) {
        super(xmlAttributeValue, range, false);
        this.index = index;
        this.resolver = ReferenceSetResolverFactory.createPsiFieldResolver(xmlAttributeValue);
    }

    @Override
    public @Nullable PsiElement resolve() {
        Optional<PsiField> resolved = resolver.resolve(index);
        return resolved.orElse(null);
    }

    /**
     * Returns the array of String, {@link PsiElement} and/or {@link LookupElement}
     * instances representing all identifiers that are visible at the location of the reference. The contents
     * of the returned array is used to build the lookup list for basic code completion. (The list
     * of visible identifiers may not be filtered by the completion prefix string - the
     * filtering is performed later by the IDE.)
     * <p>
     * This method is default since 2018.3.
     *
     * @return the array of available identifiers.
     */
    @Override
    public Object @NotNull [] getVariants() {
        Optional<PsiClass> clazz = getTargetClazz();
        return clazz.isPresent() ? JavaUtils.findSettablePsiFields(clazz.get()) : PsiReference.EMPTY_ARRAY;
    }

    private Optional<PsiClass> getTargetClazz() {
        if (getElement().getValue().contains(MybatisConstants.DOT_SEPARATOR)) {
            int ind = 0 == index ? 0 : index - 1;
            Optional<PsiField> resolved = resolver.resolve(ind);
            if (resolved.isPresent()) {
                return JavaService.getInstance(myElement.getProject()).getReferenceClazzOfPsiField(resolved.get());
            }
        } else {
            return MapperBacktrackingUtils.getPropertyClazz(myElement);
        }
        return Optional.empty();
    }

    /**
     * Gets resolver.
     *
     * @return the resolver
     */
    public ContextReferenceSetResolver<XmlAttributeValue, PsiField> getResolver() {
        return resolver;
    }

    /**
     * Sets resolver.
     *
     * @param resolver the resolver
     */
    public void setResolver(ContextReferenceSetResolver<XmlAttributeValue, PsiField> resolver) {
        this.resolver = resolver;
    }

    /**
     * Gets index.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets index.
     *
     * @param index the index
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
