package com.mybatis.generatorSql.util;

import com.intellij.codeInsight.actions.FileInEditorProcessor;
import com.intellij.codeInsight.actions.LastRunReformatCodeOptionsProvider;
import com.intellij.codeInsight.actions.ReformatCodeRunOptions;
import com.intellij.codeInsight.actions.TextRangeType;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import com.mybatis.utils.StringUtils;

import java.io.IOException;
import java.util.Set;

/**
 * 创建类, 可能有更便捷的方式创建实体类. 有待优化
 */
public class ClassCreator {
    public void createFromAllowedFields(Set<String> allowFields, PsiClass entityClass, String dtoName) {
        PsiDirectory directory = entityClass.getContainingFile().getParent();
        if (directory == null) {

            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package").append(" ");
        stringBuilder.append(((PsiJavaFile) entityClass.getParent()).getPackageName());
        stringBuilder.append(";");
        stringBuilder.append("\n");

        for (PsiField field : entityClass.getAllFields()) {
            if (allowFields.contains(field.getName())) {
                String importType = field.getType().getCanonicalText();
                stringBuilder.append("import").append(" ").append(importType).append(";").append("\n");
            }
        }

        stringBuilder.append("public").append(" ").append("class")
                .append(" ").append(dtoName).append("{").append("\n");
        for (PsiField field : entityClass.getAllFields()) {
            if (allowFields.contains(field.getName())) {
                stringBuilder.append("private").append(" ");
                stringBuilder.append(field.getType().getPresentableText()).append(" ");
                stringBuilder.append(field.getName()).append(";").append("\n");
            }
        }
        for (PsiMethod psiMethod : entityClass.getAllMethods()) {
            if (psiMethod.getName().startsWith("set") &&
                    allowFields.contains(StringUtils.lowerCaseFirstChar(psiMethod.getName().substring(3)))) {
                stringBuilder.append(psiMethod.getText()).append("\n");
            }
        }
        for (PsiMethod psiMethod : entityClass.getAllMethods()) {
            if (psiMethod.getName().startsWith("get") &&
                    allowFields.contains(StringUtils.lowerCaseFirstChar(psiMethod.getName().substring(3)))) {
                stringBuilder.append(psiMethod.getText()).append("\n");
            }
        }
        for (PsiMethod psiMethod : entityClass.getAllMethods()) {
            if (psiMethod.getName().startsWith("is") &&
                    allowFields.contains(StringUtils.lowerCaseFirstChar(psiMethod.getName().substring(2)))) {
                stringBuilder.append(psiMethod).append("\n");
            }
        }

        stringBuilder.append("}");

        WriteAction.run(() -> {
            try {


                PsiFile file = directory.createFile(dtoName + ".java");

                VirtualFile virtualFile = file.getVirtualFile();
                virtualFile.setBinaryContent(stringBuilder.toString().getBytes());

                // 格式化代码
                LastRunReformatCodeOptionsProvider provider = new LastRunReformatCodeOptionsProvider(PropertiesComponent.getInstance());
                provider.saveCodeCleanupState(true);
                provider.saveOptimizeImportsState(true);
                provider.saveRearrangeCodeState(true);

                ReformatCodeRunOptions currentRunOptions = provider.getLastRunOptions(file);

                currentRunOptions.setProcessingScope(TextRangeType.WHOLE_FILE);
                new FileInEditorProcessor(file, null, currentRunOptions).processCode();

            } catch (PsiInvalidElementAccessException | IOException | IncorrectOperationException e) {
            }

        });
    }
}
