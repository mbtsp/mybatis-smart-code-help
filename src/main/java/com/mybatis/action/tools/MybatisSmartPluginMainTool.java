package com.mybatis.action.tools;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.mybatis.utils.IconUtils;
import org.jetbrains.annotations.NotNull;

public class MybatisSmartPluginMainTool extends DefaultActionGroup {

    /**
     * Updates the state of the action. Default implementation does nothing.
     * Override this method to provide the ability to dynamically change action's
     * state and(or) presentation depending on the context (For example
     * when your action state depends on the selection you can check for
     * selection and change the state accordingly).<p></p>
     * <p>
     * This method can be called frequently, and on UI thread.
     * This means that this method is supposed to work really fast,
     * no real work should be done at this phase. For example, checking selection in a tree or a list,
     * is considered valid, but working with a file system or PSI (especially resolve) is not.
     * If you cannot determine the state of the action fast enough,
     * you should do it in the {@link #actionPerformed(AnActionEvent)} method and notify
     * the user that action cannot be executed if it's the case.<p></p>
     * <p>
     * If the action is added to a toolbar, its "update" can be called twice a second, but only if there was
     * any user activity or a focus transfer. If your action's availability is changed
     * in absence of any of these events, please call {@code ActivityTracker.getInstance().inc()} to notify
     * action subsystem to update all toolbar actions when your subsystem's determines that its actions' visibility might be affected.
     *
     * @param e Carries information on the invocation place and data available
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setVisible(false);
        e.getPresentation().setIcon(IconUtils.JAVA_MYBATIS_ICON);
    }

    @Override
    public boolean hideIfNoVisibleChildren() {
        return super.hideIfNoVisibleChildren();
    }
}
