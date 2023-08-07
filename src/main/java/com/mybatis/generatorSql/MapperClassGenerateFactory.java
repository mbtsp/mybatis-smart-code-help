package com.mybatis.generatorSql;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TypeDescriptor;
import com.mybatis.generatorSql.util.Importer;
import com.mybatis.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapperClassGenerateFactory {
    private final Project project;
    private final Editor editor;
    private final PsiTypeElement statementElement;
    private final PsiClass mapperClass;
    private final TypeDescriptor parameterDescriptor;
    private final ConditionFieldWrapper conditionFieldWrapper;
    private final TypeDescriptor returnDescriptor;

    public MapperClassGenerateFactory(Project project,
                                      Editor editor,
                                      PsiTypeElement statementElement,
                                      PsiClass mapperClass,
                                      TypeDescriptor parameterDescriptor,
                                      ConditionFieldWrapper conditionFieldWrapper,
                                      TypeDescriptor returnDescriptor) {

        this.project = project;
        this.editor = editor;
        this.statementElement = statementElement;
        this.mapperClass = mapperClass;
        this.parameterDescriptor = parameterDescriptor;
        this.conditionFieldWrapper = conditionFieldWrapper;
        this.returnDescriptor = returnDescriptor;
    }

    public String generateMethodStr(String returnClassName) {
        if (returnClassName != null) {
            String simpleName = null;
            int beginIndex = returnClassName.lastIndexOf(".");
            if (beginIndex != -1) {
                simpleName = returnClassName.substring(beginIndex + 1);
            }
            if (simpleName == null) {
                simpleName = returnClassName;
            }
            returnDescriptor.initResultType(returnClassName, simpleName);
        }
        return returnDescriptor.getContent(conditionFieldWrapper.getDefaultDateList())
                + " "
                + statementElement.getText()
                + parameterDescriptor.getContent(conditionFieldWrapper.getDefaultDateList());

    }

    public void generateMethod() {
        generateMethod(null, Collections.emptyList());
    }

    /**
     * Generate and get method str string.
     */
    public void generateMethod(String prefixParam, List<String> importListParam) {
        String prefix = "";
        if (!StringUtils.isEmpty(prefixParam)) {
            prefix = prefixParam;
        }
        Document document = editor.getDocument();
        String newMethodString = prefix + generateMethodStr(conditionFieldWrapper.getResultType());
        PsiFile containingFile = statementElement.getContainingFile();

        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final PsiMethod psiMethod = factory.createMethodFromText(newMethodString, mapperClass);
        statementElement.replace(psiMethod);
        // reformat
        PsiMethod methodBySignature = mapperClass.findMethodBySignature(psiMethod, false);
        if (methodBySignature != null) {
            TextRange textRange = methodBySignature.getTextRange();
            CodeStyleManager instance = CodeStyleManager.getInstance(project);
            instance.reformatText(containingFile, textRange.getStartOffset(), textRange.getEndOffset());
        }
        // 导入对象
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        psiDocumentManager.doPostponedOperationsAndUnblockDocument(document);
        List<String> importList = new ArrayList<>();
        importList.addAll(importListParam);
        importList.addAll(returnDescriptor.getImportList());
        importList.addAll(parameterDescriptor.getImportList());
        Importer importerReturn = Importer.create(importList);
        importerReturn.addImportToFile(psiDocumentManager, (PsiJavaFile) containingFile, document);


    }

}
