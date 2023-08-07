package com.mybatis.intentionAction;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.iftest.NeverContainsFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.utils.IconUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * * 在mapper类中通过名字生成方法和xml内容
 */
public class GenerateCodeByMethodNameIntentionAction extends GenerateCodeIFTestByMethodNameIntentionAction {
    private static final Logger logger = LoggerFactory.getLogger(GenerateCodeByMethodNameIntentionAction.class);


    @NotNull
    @Override
    public String getText() {
        return "[mybatis smart code help] generate mybatis sql";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        String name = element.getContainingFile().getFileType().getName();
        if (!JavaFileType.INSTANCE.getName().equals(name)) {
            return false;
        }
        PsiMethod parentMethodOfType = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (parentMethodOfType != null) {
            return false;
        }
        PsiClass parentClassOfType = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (parentClassOfType == null || (!parentClassOfType.isInterface())) {
            return false;
        }

        // 查找最近的有效节点
        PsiTypeElement statementElement = PsiTreeUtil.getParentOfType(element, PsiTypeElement.class);
        if (statementElement == null) {
            statementElement = PsiTreeUtil.getPrevSiblingOfType(element, PsiTypeElement.class);
        }
        // 当前节点的父类不是mapper类就返回
        PsiClass mapperClass = PsiTreeUtil.getParentOfType(statementElement, PsiClass.class);
        return mapperClass != null;
    }

    /**
     * Returns the name of the family of intentions. It is used to externalize
     * "auto-show" state of intentions. When the user clicks on a light bulb in intention list,
     * all intentions with the same family name get enabled/disabled.
     * The name is also shown in settings tree.
     *
     * @return the intention family name.
     */
    @Override
    public @NotNull String getFamilyName() {
        return "Mybatis smart code help";
    }


    private List<TxField> getResultTxFields(List<TxField> allFields, List<String> resultFields) {
        return allFields.stream().filter(field -> resultFields.contains(field.getTipName())).collect(Collectors.toList());
    }


    /**
     * 创建 条件字段包装器， 用于if,where 这样的标签
     *
     * @param project         the project
     * @param defaultDateWord defaultDateWord
     * @param allFields       allFields
     * @param resultFields    resultFields
     * @param conditionFields conditionFields
     * @param entityClass     entityClass
     * @param isSelect        isSelect
     * @return the condition field wrapper
     */
    protected Optional<ConditionFieldWrapper> getConditionFieldWrapper(@NotNull Project project,
                                                                       String defaultDateWord,
                                                                       List<TxField> allFields,
                                                                       List<String> resultFields,
                                                                       List<String> conditionFields,
                                                                       PsiClass entityClass,
                                                                       boolean isSelect) {
        return Optional.of(new NeverContainsFieldWrapper(project, allFields));
    }


    @Override
    public Icon getIcon(int flags) {
        return IconUtils.JAVA_MYBATIS_ICON;
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
