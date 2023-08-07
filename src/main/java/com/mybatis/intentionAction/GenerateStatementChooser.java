package com.mybatis.intentionAction;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.mybatis.annotation.Annotation;
import com.mybatis.utils.JavaUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The type Generate statement chooser.
 */
public class GenerateStatementChooser extends JavaFileIntentionChooser {

    /**
     * The constant INSTANCE.
     */
    public static final JavaFileIntentionChooser INSTANCE = new GenerateStatementChooser();

    @Override
    public boolean isAvailable(@NotNull PsiElement element) {
        if (!isPositionOfInterfaceDeclaration(element)) {
            return false;
        }
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        //  不应该判断当前模块是不是已经有了这个mapper. 因为无法判断
        // !isTargetPresentInXml(method) &&
        return null != method && null != clazz &&
                !JavaUtils.isAnyAnnotationPresent(method, Annotation.STATEMENT_SYMMETRIES) &&
                isTargetPresentInXml(clazz);
    }
}
