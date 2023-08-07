package com.mybatis.generator;

import com.intellij.psi.PsiMethod;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import org.jetbrains.annotations.NotNull;

public class UpdateGenerator extends AbstractStatementGenerator {
    /**
     * Instantiates a new Update generator.
     *
     * @param patterns the patterns
     */
    public UpdateGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @NotNull
    @Override
    protected IdDomElement getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        return mapper.addUpdate();
    }

    @NotNull
    @Override
    public String getId() {
        return "UpdateGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Update Statement";
    }
}
