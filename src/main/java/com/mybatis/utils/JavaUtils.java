package com.mybatis.utils;

import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.CodeCompletionHandlerBase;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.mybatis.annotation.Annotation;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.handler.BaseMethodNameHandler;
import com.mybatis.handler.MethodNameFactory;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class JavaUtils {
    private static final Logger logger = LoggerFactory.getLogger(JavaUtils.class);

    public static void completionAddElements(@NotNull CompletionResultSet completionResultSet, BaseMethodNameHandler baseMethodNameHandler, final Project project, final Editor editor, CompletionType completionType) {
        if (baseMethodNameHandler.getContributorNames() == null || baseMethodNameHandler.getContributorNames().isEmpty()) {
            return;
        }
        // 通用字段
        List<LookupElement> lookupElementList1 = baseMethodNameHandler.getContributorNames().stream().map(str -> buildLookupElement(str, project, editor)).collect(Collectors.toList());
        completionResultSet.addAllElements(lookupElementList1);
    }

    public static void completionAddElements(@NotNull CompletionResultSet completionResultSet, BaseMethodNameHandler baseMethodNameHandler, final Project project, final Editor editor) {
        completionAddElements(completionResultSet, baseMethodNameHandler, project, editor, CompletionType.BASIC);
    }

    public static LookupElement buildLookupElement(final String str) {
        return LookupElementBuilder.create(str).withIcon(IconUtils.JAVA_MYBATIS_ICON);
    }

    public static LookupElement buildLookupElement(final String str, Project project, Editor editor) {
        return buildLookupElement(str, project, editor, CompletionType.BASIC);

    }

    public static LookupElement buildLookupElement(final String str, Project project, Editor editor, CompletionType completionType) {
        return LookupElementBuilder.create(str)
                .withIcon(IconUtils.JAVA_MYBATIS_ICON)
                .bold()
                .withInsertHandler((context, item) -> context.setLaterRunnable(() -> {
                    CodeCompletionHandlerBase handler = CodeCompletionHandlerBase.createHandler(completionType);
                    handler.invokeCompletion(project, editor, 1, true);
                }));

    }

    public static CompletionResultSet buildNewCompletionResult(@NotNull CompletionResultSet completionResultSet, String prefix, List<String> splitString) {
        if (splitString == null || splitString.isEmpty()) {
            return completionResultSet;
        }
        if (com.mybatis.utils.StringUtils.isBlank(prefix)) {
            return completionResultSet;
        }
        for (String str : splitString) {
//            if (prefix.length() < str.length()) {
//                continue;
//            }
            prefix = prefix.replace(str, "").trim();
        }
//        logger.info("withPrefixMatcher prefix:{}", prefix);
        return completionResultSet.withPrefixMatcher(prefix);
    }

    public static BaseMethodNameHandler buildMethodName(String text, String prefix, List<String> fields) {
        MethodNameFactory methodNameFactory = new MethodNameFactory();
        return methodNameFactory.searchMethodName(text, prefix, fields);
    }


    public static boolean isModelClazz(@Nullable PsiClass clazz) {
        return (null != clazz && !clazz.isAnnotationType() && !clazz.isInterface() && !clazz.isEnum() && clazz.isValid());
    }


    public static Optional<PsiField> findSettablePsiField(@NotNull PsiClass clazz, @Nullable String propertyName) {
        return Optional.ofNullable(PropertyUtil.findPropertyField(clazz, propertyName, false));
    }


    @NotNull
    public static PsiField[] findSettablePsiFields(@NotNull PsiClass clazz) {
        PsiMethod[] methods = clazz.getAllMethods();
        List<PsiField> fields = Lists.newArrayList();
        for (PsiMethod method : methods) {
            if (PropertyUtil.isSimplePropertySetter(method)) {
                Optional<PsiField> psiField = findSettablePsiField(clazz, PropertyUtil.getPropertyName(method));
                Objects.requireNonNull(fields);
                psiField.ifPresent(fields::add);
            }
        }
        return fields.toArray(new PsiField[0]);
    }


    public static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return (Optional.ofNullable(type).isPresent() && type.isInterface());
    }


    public static Optional<PsiClass> findClazz(@NotNull Project project, @NotNull String clazzName) {
        return Optional.ofNullable(JavaPsiFacade.getInstance(project).findClass(clazzName, GlobalSearchScope.allScope(project)));
    }


    public static Optional<PsiClass[]> findClasses(@NotNull Project project, @NotNull String clazzName) {
        return Optional.ofNullable(JavaPsiFacade.getInstance(project).findClasses(clazzName, GlobalSearchScope.allScope(project)));
    }


    public static Optional<PsiMethod> findMethod(@NotNull Project project, @Nullable String clazzName, @Nullable String methodName) {
        if (StringUtils.isBlank(clazzName) && StringUtils.isBlank(methodName)) {
            return Optional.empty();
        }
        Optional<PsiClass> clazz = findClazz(project, clazzName);
        if (clazz.isPresent()) {
            PsiMethod[] methods = clazz.get().findMethodsByName(methodName, true);
            return ArrayUtils.isEmpty(methods) ? Optional.empty() : Optional.of(methods[0]);
        }
        return Optional.empty();
    }


    public static Optional<PsiMethod[]> findMethods(@NotNull Project project, @Nullable String clazzName, @Nullable String methodName) {
        if (StringUtils.isBlank(clazzName) && StringUtils.isBlank(methodName)) {
            return Optional.empty();
        }
        Optional<PsiClass[]> classes = findClasses(project, clazzName);
        if (classes.isPresent()) {
            List<PsiMethod> collect = Arrays.stream(classes.get()).map(psiClass -> psiClass.findMethodsByName(methodName, true)).flatMap(Arrays::stream).collect(Collectors.toList());
            return collect.isEmpty() ? Optional.empty() : Optional.of(collect.toArray(new PsiMethod[0]));
        }

        return Optional.empty();
    }


    public static Optional<PsiMethod> findMethod(@NotNull Project project, @NotNull IdDomElement element) {
        return findMethod(project, MapperUtils.getNamespace(element), MapperUtils.getId(element));
    }


    public static boolean isAnnotationPresent(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return (null != modifierList && null != modifierList.findAnnotation(annotation.getQualifiedName()));
    }


    public static Optional<PsiAnnotation> getPsiAnnotation(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        PsiModifierList modifierList = target.getModifierList();
        return (null == modifierList) ? Optional.empty() : Optional.ofNullable(modifierList.findAnnotation(annotation.getQualifiedName()));
    }


    public static Optional<PsiAnnotationMemberValue> getAnnotationAttributeValue(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation, @NotNull String attrName) {
        if (!isAnnotationPresent(target, annotation)) {
            return Optional.empty();
        }
        Optional<PsiAnnotation> psiAnnotation = getPsiAnnotation(target, annotation);
        return psiAnnotation.map(value -> value.findAttributeValue(attrName));
    }


    public static Optional<PsiAnnotationMemberValue> getAnnotationValue(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        return getAnnotationAttributeValue(target, annotation, "value");
    }


    public static Optional<String> getAnnotationValueText(@NotNull PsiModifierListOwner target, @NotNull Annotation annotation) {
        Optional<PsiAnnotationMemberValue> annotationValue = getAnnotationValue(target, annotation);
        return annotationValue.map(psiAnnotationMemberValue -> psiAnnotationMemberValue.getText().replaceAll("\"", ""));
    }


    public static boolean isAnyAnnotationPresent(@NotNull PsiModifierListOwner target, @NotNull Set<Annotation> annotations) {
        for (Annotation annotation : annotations) {
            if (isAnnotationPresent(target, annotation)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isAllParameterWithAnnotation(@NotNull PsiMethod method, @NotNull Annotation annotation) {
        PsiParameter[] parameters = method.getParameterList().getParameters();
        for (PsiParameter parameter : parameters) {
            if (!isAnnotationPresent(parameter, annotation)) {
                return false;
            }
        }
        return true;
    }


    public static boolean hasImportClazz(@NotNull PsiJavaFile file, @NotNull String clazzName) {
        PsiImportList importList = file.getImportList();
        if (null == importList) {
            return false;
        }
        PsiImportStatement[] statements = importList.getImportStatements();
        for (PsiImportStatement tmp : statements) {
            if (null != tmp && Objects.equals(tmp.getQualifiedName(), clazzName)) {
                return true;
            }
        }
        return false;
    }
}
