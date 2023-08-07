package com.mybatis.alias;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class AliasClassReference extends PsiReferenceBase<XmlAttributeValue> {
    private final Function<AliasDesc, String> function = AliasDesc::getAlias;

    public AliasClassReference(@NotNull XmlAttributeValue element) {
        super(element, true);
    }

    @Override
    public @Nullable PsiElement resolve() {
        XmlAttributeValue attributeValue = getElement();
        AliasFacade aliasFacade = AliasFacade.getInstance(attributeValue.getProject());
        if (aliasFacade == null) {
            return null;
        }
        return aliasFacade.findPsiClass(attributeValue, attributeValue.getValue()).orElse(null);
    }

    @Override
    public Object @NotNull [] getVariants() {
        AliasFacade aliasFacade = AliasFacade.getInstance(getElement().getProject());
        Collection<String> result = Collections2.transform(aliasFacade.getAliasDescs(getElement()), this.function);
        return result.toArray((Object[]) new String[result.size()]);
    }
}
