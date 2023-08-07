package com.mybatis.database.view;


import com.intellij.ide.dnd.aware.DnDAwareTree;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiElement;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.tree.AsyncTreeModel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.EditSourceOnDoubleClickHandler;
import com.intellij.util.containers.ContainerUtil;
import com.mybatis.utils.PopupMenuUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class MybatisGenerateCodeView extends SimpleToolWindowPanel implements Disposable {
    public static final DataKey<PsiElement[]> PSI_ELEMENT_ARRAY = DataKey.create("psi.Element.array");

    public static final DataKey<MybatisGenerateCodeView> DATABASE_VIEW_KEY = DataKey.create("DATABASE_VIEW_KEY");
    private final Project myProject;
    private final DatabaseStructure databaseStructure;
    @NotNull
    private final Tree myTree;


    public MybatisGenerateCodeView(@NotNull Project project) {
        super(true, true);
        this.myProject = project;
        this.databaseStructure = new DatabaseStructure(project);
        AsyncTreeModel asyncTreeModel = new AsyncTreeModel(this.databaseStructure, this);
        this.myTree = new DnDAwareTree(asyncTreeModel);
        ActionToolbar actionToolbar = createToolbar();
        setToolbar(actionToolbar.getComponent());
        setContent(ScrollPaneFactory.createScrollPane(this.myTree));
        EditSourceOnDoubleClickHandler.install(myTree);
        ToolTipManager.sharedInstance().registerComponent(myTree);
        //MybatisToolPopupMenu
        PopupMenuUtil.showPopupMenu(this.myTree, "MybatisToolPopupMenu", "MybatisToolPopup");

    }

    private static <T> T getDataFromPath(@NotNull String dataId, @NotNull Project project, @Nullable TreePath path) {
        if (path == null) {
            return null;
        }
        if (LangDataKeys.PSI_ELEMENT.is(dataId)) {
            return (T) path.getLastPathComponent();
        }
        return null;
    }

    public Tree getMyTree() {
        return myTree;
    }

    public void setupToolWindow(@NotNull ToolWindow toolWindow) {

    }

    @NotNull
    private ActionToolbar createToolbar() {
        ActionGroup group = (ActionGroup) ActionManager.getInstance().getAction("MybatisToolBar");
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("MybatisToolBar", group, true);
        toolbar.setTargetComponent(this.myTree);
        return toolbar;
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {
        if (this.myProject.isDisposed()) {
            return null;
        }
        Object data = super.getData(dataId);
        if (data != null) {
            return data;
        }
        if (CommonDataKeys.PROJECT.is(dataId)) {
            return this.myProject;
        }
        if (DATABASE_VIEW_KEY.is(dataId)) {
            return this;
        }
        if (PSI_ELEMENT_ARRAY.is(dataId)) {
            Collection<PsiElement> c = getDataFromSelectedPaths(LangDataKeys.PSI_ELEMENT);
            return c.toArray(PsiElement.EMPTY_ARRAY);
        }
        return getDataFromPath(dataId, myProject, this.myTree.getSelectionPath());
    }

    @NotNull
    private <T> Collection<T> getDataFromSelectedPaths(@NotNull DataKey<T> key) {
        TreePath[] paths = this.myTree.getSelectionPaths();
        if (paths == null) {
            return Collections.emptyList();
        }
        Set<T> result = new LinkedHashSet<>();

        for (TreePath path : paths) {
            T t = getDataFromPath(key.getName(), this.myProject, path);
            ContainerUtil.addIfNotNull(result, t);
        }
        return result;
    }

    public DatabaseStructure getDatabaseStructure() {
        return databaseStructure;
    }

    @Override
    public void dispose() {
        removeAll();
    }
}
