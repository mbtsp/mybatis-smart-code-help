package com.mybatis.action;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.mybatis.utils.DomUtils;
import org.jetbrains.annotations.NotNull;

public class MybatisTypedHandler extends TypedHandlerDelegate {
    private static void autoPopupParameter(Project project, Editor editor) {
        AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, psiFile -> true);
    }

    public TypedHandlerDelegate.Result checkAutoPopup(char charTyped, Project project, Editor editor, PsiFile file) {
        if (charTyped == '.' && DomUtils.isMybatisFile(file)) {
            autoPopupParameter(project, editor);
            return TypedHandlerDelegate.Result.STOP;
        }
        return super.checkAutoPopup(charTyped, project, editor, file);
    }

    public TypedHandlerDelegate.Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        int index = editor.getCaretModel().getOffset() - 2;
        PsiFile topLevelFile = InjectedLanguageManager.getInstance(project).getTopLevelFile(file);
        boolean parameterCase = (c == '{' && index >= 0 && editor.getDocument().getText().charAt(index) == '#' && DomUtils.isMybatisFile(topLevelFile));
        if (parameterCase) {
            autoPopupParameter(project, editor);
            return TypedHandlerDelegate.Result.STOP;
        }
        return super.charTyped(c, project, editor, file);
    }
}
