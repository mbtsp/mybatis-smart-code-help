package com.mybatis.database.model;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.containers.JBIterable;
import com.mybatis.model.CacheModel.Dbms;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class DataSource extends FakePsiElement implements NavigationItem {
    public final String name;
    private final Dbms dbms;
    private final Project project;
    PresentationData presentation = new PresentationData();

    protected DataSource(String name, Dbms dbms, Project project) {
        this.name = name;
        this.dbms = dbms;
        this.project = project;
    }

    @NotNull
    public abstract JBIterable<? extends PsiElement> iterateChildren();

    @NotNull
    public ItemPresentation getPresentation() {
        this.presentation.clear();
        this.presentation.setIcon(getIcon(false));
        this.presentation.addText(getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
        int theSize = getSize();
        if (theSize > 0) {
            this.presentation.addText("  " + theSize, SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES);
        }
        return this.presentation;
    }


    @NotNull
    public String getName() {
        return this.name;
    }


    public abstract int getSize();


    public void navigate(boolean requestFocus) {
    }


    public boolean canNavigate() {
        return false;
    }


    public boolean canNavigateToSource() {
        return false;
    }


    protected Icon getBaseIcon() {
        return dbms.getIcon();
    }


    @Nullable
    public Icon getIcon(boolean open) {
        return getBaseIcon();
    }


    public int getWeight() {
        return -1;
    }


    public String toString() {
        return this.name;
    }

    public Dbms getDbms() {
        return dbms;
    }

    @Override
    public @NotNull Project getProject() {
        return this.project;
    }

    @Override
    public PsiFile getContainingFile() {
        return null;
    }
}
