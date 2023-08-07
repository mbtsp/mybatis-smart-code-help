package com.mybatis.refactoring;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import com.mybatis.dom.model.Mapper;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;

public class MapperRefactoringProvider implements RefactoringElementListenerProvider {
    @Nullable
    public RefactoringElementListener getListener(final PsiElement element) {
        if (!(element instanceof PsiClass)) return null;
        return new RefactoringElementListener() {
            public void elementMoved(@NotNull PsiElement newElement) {

            }

            public void elementRenamed(@NotNull final PsiElement newElement) {
                if (newElement instanceof PsiClass) {
                    ApplicationManager.getApplication().runWriteAction(() -> MapperRefactoringProvider.this.renameMapperXml((PsiClass) element, (PsiClass) newElement));
                }
            }
        };
    }

    private void renameMapperXml(@NotNull PsiClass oldClazz, @NotNull PsiClass newClazz) {
        Collection<Mapper> mappers = MapperUtils.findMappers(oldClazz.getProject(), oldClazz);
        try {
            for (Mapper mapper : mappers) {
                XmlTag xmlTag = mapper.getXmlTag();
                if (xmlTag == null) {
                    continue;
                }
                VirtualFile vf = xmlTag.getOriginalElement().getContainingFile().getVirtualFile();
                if (null != vf) {
                    vf.rename(this, newClazz.getName() + "." + vf.getExtension());
                }
            }
        } catch (IOException ignored) {
        }
    }
}
