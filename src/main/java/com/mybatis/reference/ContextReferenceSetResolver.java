package com.mybatis.reference;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.mybatis.utils.MybatisConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public abstract class ContextReferenceSetResolver<F extends PsiElement, K extends PsiElement> {
    private static final Splitter SPLITTER = Splitter.on(MybatisConstants.DOT_SEPARATOR);
    protected Project project;
    protected F element;
    protected List<String> texts;

    public ContextReferenceSetResolver(F element) {
        this.element = element;
        this.project = element.getProject();
        this.texts = Lists.newArrayList(SPLITTER.split(getText()));
    }

    public Optional<K> resolve(int index) {
        Optional<K> startElement = getStartElement();
        return startElement.isPresent() ? ((this.texts.size() > 1) ? parseNext(startElement, this.texts, index) : startElement) : Optional.empty();
    }

    @NotNull
    public abstract String getText();

    @NotNull
    public abstract Optional<K> getStartElement(@Nullable String paramString);

    @NotNull
    public abstract Optional<K> resolve(@NotNull K paramK, @NotNull String paramString);

    public Optional<K> getStartElement() {
        return getStartElement(Iterables.getFirst(this.texts, null));
    }

    private Optional<K> parseNext(Optional<K> current, List<String> texts, int index) {
        int ind = 1;
        while (current.isPresent() && ind <= index) {
            String text = texts.get(ind);
            if (text.contains(" ")) {
                return Optional.empty();
            }
            current = resolve(current.get(), text);
            ind++;
        }
        return current;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public F getElement() {
        return element;
    }

    public void setElement(F element) {
        this.element = element;
    }
}
