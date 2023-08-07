package com.mybatis.intentionAction;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.mybatis.service.JavaService;
import com.mybatis.utils.JavaUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The type Java file intention chooser.
 */
public abstract class JavaFileIntentionChooser implements IntentionChooser {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        //  不应该判断当前模块是不是已经有了这个mapper. 因为无法判断
        return null != element
                && JavaUtils.isElementWithinInterface(element)
                && isAvailable(element);
    }

    /**
     * Is available boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public abstract boolean isAvailable(@NotNull PsiElement element);

    /**
     * Is position of parameter declaration boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isPositionOfParameterDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiParameter;
    }

    /**
     * Is position of method declaration boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isPositionOfMethodDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiMethod;
    }

    /**
     * Is position of interface declaration boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isPositionOfInterfaceDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiClass;
    }

    /**
     * Is target present in xml boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public boolean isTargetPresentInXml(@NotNull PsiClass element) {
        return JavaService.getInstance(element.getProject()).findWithFindFirstProcessor(element).isPresent();
    }

}
