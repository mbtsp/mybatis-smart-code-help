package com.mybatis.dialog;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MyStringsCompletionProvider extends TextFieldWithAutoCompletionListProvider<String> {
    protected MyStringsCompletionProvider(@Nullable Collection<String> variants) {
        super(variants);
    }

    @Override
    protected @NotNull String getLookupString(@NotNull String item) {
        return item;
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull String prefix, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, prefix, result);
    }

    @Override
    public @NotNull CompletionResultSet applyPrefixMatcher(@NotNull CompletionResultSet result, @NotNull String prefix) {
        return super.applyPrefixMatcher(result, prefix);
    }
}
