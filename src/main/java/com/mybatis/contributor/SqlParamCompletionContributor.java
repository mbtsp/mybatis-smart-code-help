package com.mybatis.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.model.MybatisXmlInfo;
import com.mybatis.tip.CompositeHashMarkTip;
import com.mybatis.utils.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SqlParamCompletionContributor extends CompletionContributor {

    private static void tip(IdDomElement idDomElement, @NotNull PsiElement position, @NotNull CompletionResultSet result) {
        if (idDomElement == null) {
            return;
        }
        String xmlType = idDomElement.getXmlElementName();
        String currentText = position.getText() == null ? null : position.getText().trim();
        String text = position.getContainingFile().getText();
        Optional<MybatisXmlInfo> mybatisXmlInfo = DomUtils.getCurrentMybatisXmlType(idDomElement.getXmlTag());
        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();
        lookupElementBuilders.add(LookupElementBuilder.create(xmlType));
        lookupElementBuilders.add(LookupElementBuilder.create("form"));
        mybatisXmlInfo.ifPresent(xmlInfo -> tipColumn(position, lookupElementBuilders, xmlType, text, xmlInfo));

        if (StringUtils.isBlank(text) || (StringUtils.isNotBlank(text) && xmlType.contains(text))) {
            lookupElementBuilders.add(LookupElementBuilder.create(xmlType));
//            mybatisXmlInfo.ifPresent(xmlInfo -> tipColumn(position, lookupElementBuilders, xmlType, currentText, xmlInfo));
            result = result.withPrefixMatcher(currentText == null ? "" : currentText);
            result.addAllElements(lookupElementBuilders);
            return;
        }

        if (mybatisXmlInfo.isEmpty()) {
//            result= result.withPrefixMatcher("");
            result.addAllElements(lookupElementBuilders);
            return;
        }
        Map<String, XmlTag> resultMaps = mybatisXmlInfo.get().getResultMaps();
        if ((text.contains("from") && !text.contains("where")) || (xmlType.equals("update") && !text.contains("set")) || (xmlType.equals("insert") && text.contains("into") && !text.contains("("))) {
            for (Map.Entry<String, XmlTag> entry : resultMaps.entrySet()) {
                XmlTag xmlTag = entry.getValue();
                if (xmlTag != null) {
                    XmlAttribute xmlAttribute = xmlTag.getAttribute("type");
                    if (xmlAttribute != null) {
                        String value = xmlAttribute.getValue();
                        if (StringUtils.isNotBlank(value)) {
                            String[] types = value.split("\\.");
                            String type = types[types.length - 1];
                            if (StringUtils.isNotBlank(type)) {
                                lookupElementBuilders.add(LookupElementBuilder.create(StringUtils.camelToSlash(type)));
                            }
                        }
                    }
                }
            }
        }

        result = result.withPrefixMatcher(currentText == null ? "" : currentText);
        result.addAllElements(lookupElementBuilders);


//        if(StringUtils.isBlank(currentText) || (StringUtils.isNotBlank(currentText) && xmlType.contains(currentText))){
//
//            lookupElementBuilders.add(LookupElementBuilder.create(xmlType));
//            mybatisXmlInfo.ifPresent(xmlInfo -> tipColumn(position, lookupElementBuilders, xmlType, currentText, xmlInfo));
//            result= result.withPrefixMatcher("");
//            result.addAllElements(lookupElementBuilders);
//            return;
//        }
//
//        if(mybatisXmlInfo.isEmpty()){
//            return;
//        }
//        Map<String, XmlTag> resultMaps=  mybatisXmlInfo.get().getResultMaps();
//        if(currentText.endsWith("from") || xmlType.equals("update") ||(xmlType.equals("insert") && currentText.endsWith("into"))){
//            //提示表名
//            for(Map.Entry<String, XmlTag> entry:resultMaps.entrySet()){
//                XmlTag xmlTag =entry.getValue();
//                if(xmlTag!=null){
//                    XmlAttribute xmlAttribute= xmlTag.getAttribute("type");
//                    if(xmlAttribute!=null){
//                        String value =xmlAttribute.getValue();
//                        if(StringUtils.isNotBlank(value)){
//                            String[] types =value.split("\\.");
//                            String type=types[types.length-1];
//                            if(StringUtils.isNotBlank(type)){
//                                lookupElementBuilders.add(LookupElementBuilder.create(StringUtils.camelToSlash(type)));
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        result= result.withPrefixMatcher("");
//        result.addAllElements(lookupElementBuilders);
    }

    private static void tipColumn(@NotNull PsiElement position, @NotNull List<LookupElementBuilder> lookupElementBuilders, String xmlType, String text, MybatisXmlInfo mybatisXmlInfo) {
        //提示字段
        if (text.contains("select") || (xmlType.equals("update") && text.contains("set")) || (xmlType.equals("insert") && text.contains("("))) {
            lookupElementBuilders.add(LookupElementBuilder.create(","));
            Set<XmlAttributeValue> columns = mybatisXmlInfo.getColumns();
            if (columns != null) {
                columns.forEach(xmlAttributeValue -> lookupElementBuilders.add(LookupElementBuilder.create(xmlAttributeValue.getValue())));
            }
        }

//        if(currentText.endsWith("select") || (xmlType.equals("update") && currentText.endsWith("set")) ||(xmlType.equals("insert") && currentText.endsWith("("))){

//            for(Map.Entry<String, XmlTag> entry: resultMaps.entrySet()){
//                XmlTag xmlTag =entry.getValue();
//                if(xmlTag!=null){
//                    XmlAttribute xmlAttribute= xmlTag.getAttribute("type");
//                    if(xmlAttribute!=null){
//                        String value =xmlAttribute.getValue();
//                        if(StringUtils.isNotBlank(value)){
//                            Optional<PsiClass[]>  psiClasses= JavaUtils.findClasses(position.getProject(),value);
//                            if(psiClasses.isPresent()){
//                                PsiClass[] pscls=psiClasses.get();
//                                for (PsiClass psiClass : pscls) {
//                                    PsiField[] psiFields = psiClass.getFields();
//                                    for (PsiField psiField : psiFields) {
//                                        result.addElement(LookupElementBuilder.create(psiField));
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public void fillCompletionVariants(CompletionParameters parameters, final @NotNull CompletionResultSet result) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return;
        }
        PsiElement position = parameters.getOriginalPosition();
        if (position == null) {
            return;
        }
        Editor editor = parameters.getEditor();
        Project project = editor.getProject();
        if (project == null) {
            return;
        }
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(project);
        PsiFile topLevelFile = injectedLanguageManager.getTopLevelFile(position);
        if (DomUtils.isMybatisFile(topLevelFile)) {
            if (shouldAddElement(position.getContainingFile(), parameters.getOffset())) {
                int editorCaret = parameters.getOffset() - position.getTextOffset();
                // 根据SQL语言的位置找到XML语言的位置， 获取当前提示的CRUD节点
                int offset = injectedLanguageManager.injectedToHost(position, position.getTextOffset());
                PsiElement oldPsiElement = topLevelFile.findElementAt(offset);
                Optional<IdDomElement> idDomElement = MapperUtils.findParentIdDomElement(oldPsiElement);
                // 如果当前的内容在CRUD节点内
                idDomElement.ifPresent(domElement -> new CompositeHashMarkTip(position.getProject())
                        .addElementForPsiParameter(result, domElement, position.getText(), editorCaret,oldPsiElement));
                // 如果在#{}里面输入字符, 则阻断原生SQL提示
                result.stopHere();
            }else{
                //构建所有的#{xx,jdbc}
                int editorCaret = parameters.getOffset() - position.getTextOffset();
                // 根据SQL语言的位置找到XML语言的位置， 获取当前提示的CRUD节点
                int offset = injectedLanguageManager.injectedToHost(position, position.getTextOffset());
                Optional<IdDomElement> idDomElement = MapperUtils.findParentIdDomElement(topLevelFile.findElementAt(offset));
                if(idDomElement.isEmpty()){
                    return;
                }
                Optional<PsiMethod> methodOptional = JavaUtils.findMethod(project, idDomElement.get());
                if (methodOptional.isEmpty()) {
                    return;
                }
                PsiMethod psiMethod = methodOptional.get();
                PsiParameterList parameterList = psiMethod.getParameterList();
                for (PsiParameter psiParameter : parameterList.getParameters()) {
                    Optional<PsiClass> clazz = JavaUtils.findClazz(position.getProject(), psiParameter.getType().getCanonicalText());
                    if(clazz.isPresent()){
                        PsiClass psiClass = clazz.get();
                        // Integer, String 等
                        String qualifiedName = psiClass.getQualifiedName();
                        if (qualifiedName == null
                                || qualifiedName.startsWith("java.lang")
                                || qualifiedName.startsWith("java.math")) {
                            // do nothing
                        }else{
                            for (PsiField allField : psiClass.getAllFields()) {
                                JdbcTypeUtils.findJdbcTypeByJavaType(allField.getType().getCanonicalText(true)).ifPresent(str->{
                                    String lookString=null;
                                    if(haveJiangStr(position.getContainingFile(), parameters.getOffset())){
                                        lookString="{"+allField.getName()+",jdbcType="+ str+"}";
                                    }else{
                                        lookString="#{"+allField.getName()+",jdbcType="+ str+"}";
                                    }
                                    LookupElementBuilder lookupElementBuilder=JavaLookupElementBuilder.forField(allField,lookString,allField.getContainingClass());
                                    // 如果字段上面有注释, 把注释加到末尾
                                    PsiDocComment docComment = allField.getDocComment();
                                    if (docComment != null) {
                                        String text = Arrays.stream(docComment.getDescriptionElements())
                                                .filter(p -> p instanceof PsiDocToken)
                                                .map(PsiElement::getText)
                                                .collect(Collectors.joining());
                                        String trimmedText = text.trim();
                                        if (!org.apache.commons.lang3.StringUtils.isEmpty(trimmedText)) {
                                            trimmedText = "(" + trimmedText + ")";
                                            lookupElementBuilder = lookupElementBuilder.withTailText(trimmedText);
                                        }
                                    }
                                    result.addElement(lookupElementBuilder);

                                });

                            }
                        }
                    }
                }

            }
        }
    }

    private boolean shouldAddElement(PsiFile file, int offset) {
        String text = file.getText();
        for (int i = offset - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c == '{' && text.charAt(i - 1) == '#') {
                return true;
            }
        }
        return false;
    }

    private boolean haveJiangStr(PsiFile file, int offset){
        String text = file.getText();
        for (int i = offset - 1; i > 0; i--) {
            char c = text.charAt(i);
            if (c == '#') {
                return true;
            }
        }
        return false;
    }
}
