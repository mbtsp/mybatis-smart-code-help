package com.mybatis.dialog;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.ReferenceEditorComboWithBrowseButton;
import org.jetbrains.annotations.NotNull;

public class MyPackageNameReferenceEditorCombo extends ReferenceEditorComboWithBrowseButton {
    private Module myModule;

    public MyPackageNameReferenceEditorCombo(String text, @NotNull Project project, Module module, String recentsKey, final String chooserTitle, boolean showResource) {
        super(null, text, project, false, recentsKey);
        this.myModule = module;
        this.addActionListener(e -> {
            final MyPackageChooser chooser = new MyPackageChooser(chooserTitle, project, myModule, showResource);
            chooser.selectPackage(getText());
            if (chooser.showAndGet()) {
                final PsiPackage aPackage = chooser.getSelectedPackage();
                if (aPackage != null) {
                    setText(aPackage.getQualifiedName());
                }
            }
        });
    }

    public MyPackageNameReferenceEditorCombo(String text, @NotNull Project project, Module module, String recentsKey, final String chooserTitle) {
        super(null, text, project, false, recentsKey);
        this.myModule = module;
        this.addActionListener(e -> {
            final MyPackageChooser chooser = new MyPackageChooser(chooserTitle, project, myModule);
            chooser.selectPackage(getText());
            if (chooser.showAndGet()) {
                final PsiPackage aPackage = chooser.getSelectedPackage();
                if (aPackage != null) {
                    setText(aPackage.getQualifiedName());
                }
            }
        });
    }

    public void setMyModule(Module module) {
        this.myModule = module;
    }

}
