package com.mybatis.tip;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.JavaLookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.util.xml.DomUtil;
import com.mybatis.annotation.Annotation;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import com.mybatis.utils.JavaUtils;
import com.mybatis.utils.JdbcTypeUtils;
import com.mybatis.utils.MapperUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CompositeHashMarkTip {

    public static final String DOT = ".";
    /**
     * 方法上面的注释名称, 例如
     *
     * @param userName 用户名
     */
    public static final String PARAM = "param";
    private static final Logger logger = LoggerFactory.getLogger(CompositeHashMarkTip.class);
    private static List<HashMarkTip> hashMarkTips = new ArrayList<>() {
        {
            add(new JdbcTypeHashMarkTip());
            add(new TypeHandlerHashMarkTip());
            add(new JavaTypeHashMark());
            add(new ResultMapHashMarkTip());
            add(new ModeHashMarkTip());
            add(new NumericScaleHashMarkTip());
            add(new JdbcTypeNameHashMarkTip());
        }
    };
    private Project project;

    public CompositeHashMarkTip(Project project) {
        this.project = project;
    }

    /**
     * Add element for psi parameter.
     * <p>
     * 提示的内容参考:
     * see https://mybatis.org/mybatis-3/zh/sqlmap-xml.html#select
     *
     * @param wrappedText
     * @param editorCaret
     */
    public void addElementForPsiParameter(CompletionResultSet result,
                                          IdDomElement idDomElement,
                                          String wrappedText,
                                          int editorCaret, PsiElement oldPsiElement) {
        Optional<PsiMethod> methodOptional = JavaUtils.findMethod(project, idDomElement);
        if (methodOptional.isEmpty()) {
//            logger.info("the psiMethod is null");
            return;
        }
        PsiMethod psiMethod = methodOptional.get();
//        logger.info("CompletionContributor xml start");
        // 焦点前面的字段名称
        String fieldFrontOfCaret = wrappedText.substring(0, editorCaret);

        boolean hasHashMark = fieldFrontOfCaret.contains(",");
        if (!hasHashMark) {
            PsiParameterList parameterList = psiMethod.getParameterList();
            boolean canUseFields = parameterList.getParametersCount() == 1;
            // 方法参数有注释的情况， 需要提升注释的内容
            PsiDocComment docComment = psiMethod.getDocComment();
            PsiDocTag[] params = null;
            if (docComment != null) {
                params = docComment.findTagsByName(PARAM);
            }

            for (PsiParameter psiParameter : parameterList.getParameters()) {
                String fieldName = findFieldNameByParam(psiParameter);

                promptFields(result, psiParameter, params, fieldFrontOfCaret, fieldName, canUseFields, oldPsiElement);
            }

        }

        CompletionResultSet completionResultSet = result;
        // 已经提示了字段,才能提示二级类型或者一级类型
        // see mybatis : org.apache.ibatis.builder.SqlSourceBuilder#PARAMETER_PROPERTIES
        boolean tipOfSecondLevel = false;
        // tip 7 types
        String latestText = findLatestText(wrappedText, editorCaret);
        if (!StringUtils.isEmpty(latestText)) {
            Mapper mapper = MapperUtils.getMapper(idDomElement);
            for (HashMarkTip hashMarkTip : hashMarkTips) {
                String tipText = "," + hashMarkTip.getName() + "=";
                if (latestText.startsWith(tipText)) {
                    tipOfSecondLevel = true;
                    String prefixMatcher = latestText.substring(tipText.length());
                    completionResultSet = result.withPrefixMatcher(prefixMatcher);
                    hashMarkTip.tipValue(completionResultSet, mapper);
                    break;
                }

            }
        }
        // 存在逗号`,`标记，并且已经是`=`符号
        if (hasHashMark && !tipOfSecondLevel) {
            HashMarkTipInsertHandler hashMarkTipInsertHandler = new HashMarkTipInsertHandler();
            if (!StringUtils.isEmpty(latestText)) {
                completionResultSet = result.withPrefixMatcher(latestText);
            }
            for (HashMarkTip hashMarkTip : hashMarkTips) {
                String tipText = "," + hashMarkTip.getName() + "=";
                LookupElementBuilder element = LookupElementBuilder.create(tipText)
                        .withInsertHandler(hashMarkTipInsertHandler)
                        .withPsiElement(idDomElement.getXmlTag());
                completionResultSet.addElement(element);
            }
        }


//        logger.info("CompletionContributor xml end");

    }

    /**
     * 参考
     * com.intellij.codeInsight.completion.JavaLookupElementBuilder
     * com.intellij.codeInsight.completion.JavaCompletionUtil
     * <p>
     * 提示类的全称: com.intellij.codeInsight.completion.JavaPsiClassReferenceElement (com.baomidou.mybatis3.domain.Blog)
     * 提示类的简称: com.intellij.codeInsight.lookup.PsiTypeLookupItem  (Blog)
     * 提示指定的单个属性: com.intellij.codeInsight.lookup.VariableLookupItem
     *
     * @param result
     * @param psiParameter
     * @param params
     * @param fieldFrontOfCaret
     * @param fieldName
     * @param canUseFields      是否能使用参数的类型的属性名称
     */
    private void promptFields(CompletionResultSet result, PsiParameter psiParameter, PsiDocTag[] params, String fieldFrontOfCaret, String fieldName, boolean canUseFields, PsiElement oldPsiElement) {
        Optional<PsiClass> clazz = JavaUtils.findClazz(psiParameter.getProject(), psiParameter.getType().getCanonicalText());
        boolean isPrimitive = true;
        // 是一个类型:
        if (clazz.isPresent()) {
            PsiClass psiClass = clazz.get();
            // Integer, String 等
            String qualifiedName = psiClass.getQualifiedName();
            if (qualifiedName == null
                    || qualifiedName.startsWith("java.lang")
                    || qualifiedName.startsWith("java.math")) {
                // do nothing
            } else {
                // 如果只有一个字段， 并且字段没有配置 @Param
                if (canUseFields && fieldName == null) {
                    for (PsiField allField : psiClass.getAllFields()) {
                        addFieldTip(result, allField, oldPsiElement);
                    }
                    // 引用类型已经提示了, 基本类型不应该再次提示
                    isPrimitive = false;
                }

                // 如果有@Param注解, 支持二级连续提示
                if (fieldName != null) {
                    // 提示当前类型的所有字段
                    if (fieldFrontOfCaret.startsWith(fieldName + DOT)) {
                        result = result.withPrefixMatcher(fieldFrontOfCaret.substring(fieldName.length() + 1));

                        for (PsiField allField : psiClass.getAllFields()) {
                            addFieldTip(result, allField, oldPsiElement);
                        }
                        // 引用类型已经提示了, 基本类型不应该再次提示
                        isPrimitive = false;
                    }
                }

            }

        }
        // 基本类型不用关心连续提示的问题
        if (isPrimitive && fieldName != null) {
            LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(fieldName);
            // 方法有注释的情况, 如果当前参数有注释, 加入参数注释
            if (params != null) {
                // 方法的参数名称和注释名称一致的情况下
                Optional<PsiDocTag> foundDocTagOfParam = Arrays.stream(params)
                        .filter(p -> {
                            PsiDocTagValue valueElement = p.getValueElement();
                            if (valueElement != null) {
                                return valueElement.getText().equals(psiParameter.getName());
                            }
                            return false;
                        })
                        .findAny();
                if (foundDocTagOfParam.isPresent()) {
                    PsiDocTag psiDocTag = foundDocTagOfParam.get();
                    String tailText = Arrays.stream(psiDocTag.getDataElements())
                            .filter(p -> p instanceof PsiDocToken)
                            .map(PsiElement::getText)
                            .collect(Collectors.joining());
                    String trimmedText = tailText.trim();
                    if (!StringUtils.isEmpty(trimmedText)) {
                        trimmedText = "(" + trimmedText + ")";
                        lookupElementBuilder = lookupElementBuilder.withTailText(trimmedText);
                    }
                }
            }
            result.addElement(lookupElementBuilder);
        }


    }

    private void addFieldTip(CompletionResultSet result, PsiField allField, PsiElement oldPsiElement) {
        AtomicReference<String> lookString = new AtomicReference<>();
        JdbcTypeUtils.findJdbcTypeByJavaType(allField.getType().getCanonicalText(true)).ifPresent(str -> {
            lookString.set(allField.getName() + ",jdbcType=" + str);
        });

        LookupElementBuilder lookupElementBuilderLo = null;
        boolean flag = false;
        LookupElementBuilder lookupElementBuilderLo1 = null;
        if (StringUtils.isNotBlank(lookString.get())) {
            lookupElementBuilderLo = JavaLookupElementBuilder.forField(allField, lookString.get(), allField.getContainingClass());
            String str = lookString.get();
            if (oldPsiElement.getText().contains("\n")) {
                str = str + "}";
                flag = true;
            }
            lookupElementBuilderLo1 = JavaLookupElementBuilder.forField(allField, str, allField.getContainingClass());
        }
        LookupElementBuilder lookupElementBuilder = JavaLookupElementBuilder.forField(allField);
        // 如果字段上面有注释, 把注释加到末尾
        PsiDocComment docComment = allField.getDocComment();
        if (docComment != null) {
            String text = Arrays.stream(docComment.getDescriptionElements())
                    .filter(p -> p instanceof PsiDocToken)
                    .map(PsiElement::getText)
                    .collect(Collectors.joining());
            String trimmedText = text.trim();
            if (!StringUtils.isEmpty(trimmedText)) {
                trimmedText = "(" + trimmedText + ")";
                lookupElementBuilder = lookupElementBuilder.withTailText(trimmedText);
                if (lookupElementBuilderLo != null) {
                    lookupElementBuilderLo = lookupElementBuilderLo.withTailText(trimmedText);
                }
                if (lookupElementBuilderLo1 != null) {
                    lookupElementBuilderLo1 = lookupElementBuilderLo1.withTailText(trimmedText);
                }

            }
        }
        if (lookupElementBuilderLo != null) {
            result.addElement(lookupElementBuilderLo);
        }
        if (lookupElementBuilderLo1 != null && flag) {
            result.addElement(lookupElementBuilderLo1);
        }
        result.addElement(lookupElementBuilder);
    }


    private String findFieldNameByParam(PsiParameter psiParameter) {
        String fieldName = null;
        PsiConstantEvaluationHelper constantEvaluationHelper = JavaPsiFacade.getInstance(project).getConstantEvaluationHelper();
        PsiAnnotation annotation = psiParameter.getAnnotation(Annotation.PARAM.getQualifiedName());
        // 有注解, 优先使用注解名称
        if (annotation != null) {
            PsiAnnotationMemberValue valueAttr = annotation.findAttributeValue("value");
            fieldName = Objects.requireNonNull(constantEvaluationHelper.computeConstantExpression(valueAttr)).toString();
        }

        return fieldName;
    }


    private String findLatestText(String wrappedText, int editorCaret) {
        for (int caret = editorCaret - 1; caret >= 0; caret--) {
            if (',' == wrappedText.charAt(caret)) {
                return wrappedText.substring(caret, editorCaret);
            }
        }
        return wrappedText.substring(0, editorCaret);
    }

    public PsiElement findReference(PsiElement myElement) {
        IdDomElement domElement = DomUtil.findDomElement(myElement, IdDomElement.class);
        if (domElement != null) {
            Object value = domElement.getId().getValue();
            if (!(value instanceof PsiMethod)) {
                return null;
            }
            PsiMethod psiMethod = (PsiMethod) value;
            PsiParameterList parameterList = psiMethod.getParameterList();
            if (parameterList.isEmpty()) {
                return null;
            }
            String fieldName = findFieldName(myElement);
            // 没有字段名称, 不需要查找引用
            PsiConstantEvaluationHelper constantEvaluationHelper = JavaPsiFacade.getInstance(project).getConstantEvaluationHelper();
            int parametersCount = parameterList.getParametersCount();
            @NotNull PsiParameter[] parameters = parameterList.getParameters();
            for (PsiParameter psiParameter : parameters) {
                PsiAnnotation annotation = psiParameter.getAnnotation(Annotation.PARAM.getQualifiedName());
                if (annotation != null) {
                    PsiAnnotationMemberValue paramAnnotationValue = annotation.findAttributeValue("value");
                    String paramValue = (String) constantEvaluationHelper.computeConstantExpression(paramAnnotationValue);
                    // 设置了 @Param 但是内容是空, 这种情况不予处理
                    if (paramValue == null) {
                        continue;
                    }
                    if (paramValue.equals(fieldName)) {
                        return psiMethod;
                    }
                    String prefix = paramValue + ".";
                    if (fieldName.startsWith(prefix)) {
                        String fieldSuffix = fieldName.substring(prefix.length());
                        PsiElement field = getPsiElement(fieldSuffix, psiParameter);
                        if (field != null) {
                            return field;
                        }
                    }
                } else {
                    // @Param 注解一定为空的情况下的跳转处理

                    // 兼容java8开启 -parameters 参数的方式，这种方式不需要@Param参数
                    if (Objects.equals(psiParameter.getName(), fieldName)) {
                        return psiMethod;
                    }

                    if (parametersCount == 1) {
                        PsiElement field = getPsiElement(fieldName, psiParameter);
                        if (field != null) {
                            return field;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private PsiElement getPsiElement(String fieldName, PsiParameter psiParameter) {
        String canonicalText = psiParameter.getType().getCanonicalText();
        Optional<PsiClass> clazzOpt = JavaUtils.findClazz(project, canonicalText);
        // 如果是引用类型, 则找到匹配的字段，并进行跳转
        if (clazzOpt.isPresent()) {
            PsiClass psiClass = clazzOpt.get();
            for (PsiField field : psiClass.getAllFields()) {
                if (Objects.equals(field.getName(), fieldName)) {
                    return field;
                }
            }
        }
        return null;
    }

    @NotNull
    private String findFieldName(PsiElement myElement) {
        String text = myElement.getText();
        int commaIndex = text.indexOf(",");
        int braceIndex = text.indexOf("}");
        int endIndex = -1;
        // 逗号在大括号前面, 字段名字就是逗号前面的内容
        if (commaIndex != -1 && commaIndex < braceIndex) {
            endIndex = commaIndex;
        }
        // 结尾是大括号
        if (endIndex == -1) {
            endIndex = braceIndex;
        }
        return text.substring(2, endIndex);
    }
}
