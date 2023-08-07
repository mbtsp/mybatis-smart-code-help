package com.mybatis.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.CustomHighlighterTokenType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.mybatis.dom.model.Mapper;
import com.mybatis.dom.model.ResultMap;
import com.mybatis.handler.BaseMethodNameHandler;
import com.mybatis.handler.MethodNameFactory;
import com.mybatis.utils.JavaUtils;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class JavaMapperNameContributor extends CompletionContributor {
    private static final Logger logger = LoggerFactory.getLogger(JavaMapperNameContributor.class);

    private static boolean inCommentOrLiteral(CompletionParameters parameters) {
        HighlighterIterator iterator = ((EditorEx) parameters.getEditor()).getHighlighter().createIterator(parameters.getOffset());
        if (iterator.atEnd()) {
            return false;
        }

        IElementType elementType = iterator.getTokenType();
        if (elementType == CustomHighlighterTokenType.WHITESPACE) {
            iterator.retreat();
            elementType = iterator.getTokenType();
        }
        return elementType == CustomHighlighterTokenType.LINE_COMMENT ||
                elementType == CustomHighlighterTokenType.MULTI_LINE_COMMENT ||
                elementType == CustomHighlighterTokenType.STRING ||
                elementType == CustomHighlighterTokenType.SINGLE_QUOTED_STRING;
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet completionResultSet) {
        super.fillCompletionVariants(parameters, completionResultSet);
        if (!parameters.getCompletionType().equals(CompletionType.BASIC)) {
            return;
        }
        if (inCommentOrLiteral(parameters)) {
            return;
        }
        String prefix = CompletionUtil.findJavaIdentifierPrefix(parameters);
        //获取当前光标所在的字符串
//        parameters.getOriginalPosition().getText()
        // 验证当前类必须是接口
        PsiElement psiElement = parameters.getOriginalPosition();
        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(psiElement, PsiMethod.class);
        if (psiMethod != null) {
            return;
        }
        PsiClass psiClass = PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
        if (psiClass == null || !psiClass.isInterface()) {
            return;
        }

        Collection<Mapper> mapperCollection = MapperUtils.findMappers(psiClass.getProject(), psiClass);
        if (mapperCollection.isEmpty()) {
            return;
        }
//        String tableName =MapperUtils.getTableName(psiClass.getProject(),psiClass);


        List<ResultMap> resultMapList = mapperCollection.stream().findFirst().get().getResultMaps();
        if (resultMapList.isEmpty()) {
            return;
        }
        Set<String> classList = new HashSet<>();
        for (ResultMap resultMap : resultMapList) {
            XmlAttributeValue xmlAttributeValue = resultMap.getType().getXmlAttributeValue();
            if (xmlAttributeValue == null) {
                continue;
            }
            classList.add(xmlAttributeValue.getValue());
        }
        List<String> methodNames = new ArrayList<>();
        List<String> contributorList = new ArrayList<>();
        for (String cls : classList) {
            PsiClass psiClass1 = JavaUtils.findClazz(psiClass.getProject(), cls).orElse(null);
            if (psiClass1 == null) {
                continue;
            }
            MethodNameFactory methodNameFactory = new MethodNameFactory();
            BaseMethodNameHandler baseMethodNameHandler = methodNameFactory.searchMethodName(psiClass1.getProject(), psiClass1, parameters);
            if (baseMethodNameHandler.getContributorNames() != null && !baseMethodNameHandler.getContributorNames().isEmpty()) {
                contributorList.addAll(baseMethodNameHandler.getContributorNames());
            }
            if (baseMethodNameHandler.getMethodNames() != null && !baseMethodNameHandler.getMethodNames().isEmpty()) {
                methodNames.addAll(baseMethodNameHandler.getMethodNames());
            }

        }
//        logger.info("prefix:{},method names:{}", prefix, methodNames);
        completionResultSet = JavaUtils.buildNewCompletionResult(completionResultSet, prefix, methodNames);
        // 通用字段
        List<LookupElement> lookupElementList1 = contributorList.stream().map(str -> JavaUtils.buildLookupElement(str, psiClass.getProject(), parameters.getEditor())).collect(Collectors.toList());
        completionResultSet.addAllElements(lookupElementList1);

    }


}
