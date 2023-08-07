package com.mybatis.alias;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AliasFacade {
    private final Project project;
    private final JavaPsiFacade javaPsiFacade;
    private final List<AliasResolver> resolvers;

    public AliasFacade(Project project) {
        this.project = project;
        this.resolvers = new ArrayList<>();
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        initResolvers();
    }

    public static AliasFacade getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, AliasFacade.class);
    }

    private void initResolvers() {
        try {
            Class.forName("com.intellij.spring.model.utils.SpringModelUtils");
            registerResolver(AliasResolverFactory.createBeanResolver(this.project));

            registerResolver(AliasResolverFactory.createSpringBootResolver(this.project));
        } catch (ClassNotFoundException ignored) {
        }

        registerResolver(AliasResolverFactory.createSingleAliasResolver(this.project));
        registerResolver(AliasResolverFactory.createConfigPackageResolver(this.project));
        registerResolver(AliasResolverFactory.createAnnotationResolver(this.project));
        registerResolver(AliasResolverFactory.createInnerAliasResolver(this.project));
    }


    public Optional<PsiClass> findPsiClass(@Nullable PsiElement element, @NotNull String shortName) {
        PsiClass clazz = this.javaPsiFacade.findClass(shortName, GlobalSearchScope.allScope(this.project));
        if (null != clazz) {
            return Optional.of(clazz);
        }
        for (AliasResolver resolver : this.resolvers) {
            for (AliasDesc desc : resolver.getClassAliasDescriptions(element)) {
                if (desc.getAlias().equals(shortName)) {
                    return Optional.of(desc.getPsiClass());
                }
            }
        }
        return Optional.empty();
    }


    @NotNull
    public Collection<AliasDesc> getAliasDescs(@Nullable PsiElement element) {
        ArrayList<AliasDesc> result = new ArrayList<>();
        for (AliasResolver resolver : this.resolvers) {
            result.addAll(resolver.getClassAliasDescriptions(element));
        }
        return result;
    }


    public Optional<AliasDesc> findAliasDesc(@Nullable PsiClass clazz) {
        if (null == clazz) {
            return Optional.empty();
        }
        for (AliasResolver resolver : this.resolvers) {
            for (AliasDesc desc : resolver.getClassAliasDescriptions(clazz)) {
                if (desc.getPsiClass().equals(clazz)) {
                    return Optional.of(desc);
                }
            }
        }
        return Optional.empty();
    }


    public void registerResolver(@NotNull AliasResolver resolver) {
        this.resolvers.add(resolver);
    }
}
