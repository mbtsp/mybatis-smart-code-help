package com.mybatis.generator;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.CommonProcessors;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import com.mybatis.generator.ui.ListSelectionListener;
import com.mybatis.generator.ui.UiComponentFacade;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.service.EditorService;
import com.mybatis.service.JavaService;
import com.mybatis.utils.JavaUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractStatementGenerator {

    /**
     * The constant UPDATE_GENERATOR.
     */
    public static final AbstractStatementGenerator UPDATE_GENERATOR = new UpdateGenerator("update", "modify", "set");

    /**
     * The constant SELECT_GENERATOR.
     */
    public static final AbstractStatementGenerator SELECT_GENERATOR = new SelectGenerator("select", "get", "look", "find", "list", "search", "query");

    /**
     * The constant DELETE_GENERATOR.
     */
    public static final AbstractStatementGenerator DELETE_GENERATOR = new DeleteGenerator("del", "delete", "cancel");

    /**
     * The constant INSERT_GENERATOR.
     */
    public static final AbstractStatementGenerator INSERT_GENERATOR = new InsertGenerator("insert", "add", "new");

    /**
     * The constant ALL.
     */
    public static final Set<AbstractStatementGenerator> ALL = ImmutableSet.of(UPDATE_GENERATOR, SELECT_GENERATOR, DELETE_GENERATOR, INSERT_GENERATOR);

    private static final Function<Mapper, String> FUN = mapper -> {
        XmlTag xmlTag = mapper.getXmlTag();
        if (xmlTag == null) {
            return "";
        }
        VirtualFile vf = xmlTag.getContainingFile().getVirtualFile();
        if (null == vf) {
            return "";
        }
        return vf.getCanonicalPath();
    };
    private Set<String> patterns;

    /**
     * Instantiates a new Abstract statement generator.
     *
     * @param patterns the patterns
     */
    public AbstractStatementGenerator(@NotNull String... patterns) {
        this.patterns = Sets.newHashSet(patterns);
    }

    /**
     * 获取方法的返回类型
     *
     * @param method the method
     * @return select result type
     */
    public static Optional<PsiClass> getSelectResultType(@Nullable PsiMethod method) {
        if (null == method) {
            return Optional.empty();
        }
        PsiType returnType = method.getReturnType();
        // 是基本类型, 并且不是 void
        if (returnType instanceof PsiPrimitiveType && !PsiType.VOID.equals(returnType)) {
            return JavaUtils.findClazz(method.getProject(), Objects.requireNonNull(((PsiPrimitiveType) returnType).getBoxedTypeName()));
        } else if (returnType instanceof PsiClassReferenceType) {
            PsiClassReferenceType type = (PsiClassReferenceType) returnType;
            if (type.hasParameters()) {
                PsiType[] parameters = type.getParameters();
                // 处理是 List 的返回结果, 将List<T> 的泛型拿出来
                if (parameters.length == 1) {
                    PsiType parameter = parameters[0];
                    // 通常情况 List<?> 这里一定是引用类型, 但是进入这里的来源还有检查方法,
                    // 不仅仅只是生成xml,  所以这里加一个判断.
                    if (parameter instanceof PsiClassReferenceType) {
                        type = (PsiClassReferenceType) parameter;
                    }
                }
            }
            return Optional.ofNullable(type.resolve());
        }
        return Optional.empty();
    }

    /**
     * Apply generate.
     *
     * @param method the method
     */
    public static void applyGenerate(@Nullable final PsiMethod method) {
        if (null == method) {
            return;
        }
        final Project project = method.getProject();
        final AbstractStatementGenerator[] generators = getGenerators(method);
        if (1 == generators.length) {
            generators[0].execute(method);
        } else {
            BaseListPopupStep<AbstractStatementGenerator> step = new BaseListPopupStep<>("[ Statement type for method: " + method.getName() + "]", generators) {
                @Override
                public PopupStep<?> onChosen(AbstractStatementGenerator selectedValue, boolean finalChoice) {
                    return this.doFinalStep(() -> WriteCommandAction.writeCommandAction(project).run(() -> selectedValue.execute(method)));
                }
            };
            JBPopupFactory.getInstance().createListPopup(step).showInFocusCenter();
        }
    }

    /**
     * Get generators abstract statement generator [ ].
     *
     * @param method the method
     * @return the abstract statement generator [ ]
     */
    @NotNull
    public static AbstractStatementGenerator[] getGenerators(@NotNull PsiMethod method) {
        String target = method.getName();
        List<AbstractStatementGenerator> result = Lists.newArrayList();
        for (AbstractStatementGenerator generator : ALL) {
            for (String pattern : generator.getPatterns()) {
                // 一定是以关键字开头
                if (target.startsWith(pattern)) {
                    result.add(generator);
                }
            }
        }
        return CollectionUtils.isNotEmpty(result) ? result.toArray(new AbstractStatementGenerator[0]) : ALL.toArray(new AbstractStatementGenerator[0]);
    }

    /**
     * Execute.
     *
     * @param method the method
     */
    public void execute(@NotNull final PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        if (null == psiClass) {
            return;
        }
        CommonProcessors.CollectProcessor<Mapper> processor = new CommonProcessors.CollectProcessor<>();
        JavaService.getInstance(method.getProject()).process(psiClass, processor);
        final List<Mapper> mappers = Lists.newArrayList(processor.getResults());
        if (1 == mappers.size()) {
            setupTag(method, (Mapper) Iterables.getOnlyElement(mappers, (Object) null));
        } else if (mappers.size() > 1) {
            Collection<String> paths = Collections2.transform(mappers, FUN);
            UiComponentFacade.getInstance(method.getProject())
                    .showListPopup("Choose target mapper xml to generate", new ListSelectionListener() {
                        @Override
                        public void selected(int index) {
                            // 修复多模块生成标签, 修改xml内容不允许在用户线程操作的BUG
                            WriteCommandAction.runWriteCommandAction(method.getProject(), () -> setupTag(method, mappers.get(index)));

                        }

                        @Override
                        public boolean isWriteAction() {
                            return true;
                        }
                    }, paths.toArray(new String[0]));
        } else {
            Messages.showErrorDialog(method.getProject(), MybatisSmartCodeHelpBundle.message("not.found.xml.file"), "Error");
        }
    }

    private void setupTag(PsiMethod method, Mapper mapper) {
        IdDomElement target = getTarget(mapper, method);
        target.getId().setStringValue(method.getName());
        target.setValue(" ");
        XmlTag tag = target.getXmlTag();
        assert tag != null;
        int offset = tag.getTextOffset() + tag.getTextLength() - tag.getName().length() + 1;
        if (offset > 2) {
            offset = offset - 2;
        }
        EditorService editorService = EditorService.getInstance(method.getProject());
        editorService.format(tag.getContainingFile(), tag);
        editorService.scrollTo(tag, offset);
    }

    @Override
    public String toString() {
        return this.getDisplayText();
    }

    /**
     * Gets target.
     *
     * @param mapper the mapper
     * @param method the method
     * @return the target
     */
    @NotNull
    protected abstract IdDomElement getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method);

    /**
     * Gets id.
     *
     * @return the id
     */
    @NotNull
    public abstract String getId();

    /**
     * Gets display text.
     *
     * @return the display text
     */
    @NotNull
    public abstract String getDisplayText();

    /**
     * Gets patterns.
     *
     * @return the patterns
     */
    public Set<String> getPatterns() {
        return patterns;
    }

    /**
     * Sets patterns.
     *
     * @param patterns the patterns
     */
    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

}
