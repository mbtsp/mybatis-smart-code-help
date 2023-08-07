package com.mybatis.alias;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

public class AliasDesc {
    private PsiClass psiClass;
    private String alias;

    public AliasDesc() {
    }

    public AliasDesc(PsiClass psiClass, String alias) {
        this.alias = alias;
        this.psiClass = psiClass;
    }

    public static AliasDesc create(@NotNull PsiClass psiClass, @NotNull String alias) {
        return new AliasDesc(psiClass, alias);
    }

    public PsiClass getPsiClass() {
        return psiClass;
    }

    public void setPsiClass(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
