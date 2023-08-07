package com.mybatis.locator;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.mybatis.utils.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MapperLocator {

    /**
     * The constant dfltLocateStrategy.
     */
    public static LocateStrategy dfltLocateStrategy = new PackageLocateStrategy();

    /**
     * Gets instance.
     *
     * @param project the project
     * @return the instance
     */
    public static MapperLocator getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MapperLocator.class);
    }

    /**
     * Process boolean.
     *
     * @param method the method
     * @return the boolean
     */
    public boolean process(@Nullable PsiMethod method) {
        return null != method && process(method.getContainingClass());
    }

    /**
     * Process boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
    public boolean process(@Nullable PsiClass clazz) {
        return null != clazz && JavaUtils.isElementWithinInterface(clazz) && dfltLocateStrategy.apply(clazz);
    }

}
