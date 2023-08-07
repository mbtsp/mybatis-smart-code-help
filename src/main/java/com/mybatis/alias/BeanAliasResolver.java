package com.mybatis.alias;

import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.model.SpringModelSearchParameters;
import com.intellij.spring.model.utils.SpringModelUtils;
import com.intellij.spring.model.utils.SpringPropertyUtils;
import com.mybatis.utils.JavaUtils;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BeanAliasResolver extends PackageAliasResolver {
    private static final String MAPPER_ALIAS_PROPERTY = "typeAliasesPackage";
    private static final List<String> MAPPER_ALIAS_PACKAGE_CLASSES = new ArrayList<String>() {
        {
            // default
            add("org.mybatis.spring.SqlSessionFactoryBean");
            // mybatis-plus3
            add("com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean");
            // mybatis-plus2
            add("com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean");
        }
    };

    public BeanAliasResolver(@NotNull Project project) {
        super(project);
    }

    @Override
    protected @NotNull Collection<String> getPackages(@NotNull PsiElement psiElement) {
        CommonSpringModel commonSpringModel = SpringModelUtils.getInstance().getSpringModel(psiElement);
        Set<PsiClass> psiClassSet = findSqlSessionFactories();
        return determinePackages(psiClassSet, commonSpringModel);
    }

    private Collection<String> determinePackages(Set<PsiClass> classes, CommonSpringModel springModel) {
        Set<String> packages = Sets.newHashSet();
        for (PsiClass sqlSessionFactoryClass : classes) {
            SpringModelSearchParameters.BeanClass beanClass = SpringModelSearchParameters.BeanClass.byClass(sqlSessionFactoryClass);
            springModel.processByClass(beanClass, springBeanPointer -> {
                String propertyStringValue = SpringPropertyUtils.getPropertyStringValue(springBeanPointer.getSpringBean(), MAPPER_ALIAS_PROPERTY);
                if (!StringUtils.isEmpty(propertyStringValue)) {
                    packages.add(propertyStringValue);
                    return true;
                }
                return false;
            });
        }
        return packages;
    }

    private Set<PsiClass> findSqlSessionFactories() {
        Set<PsiClass> sqlSessionFactorySet = new HashSet<>();
        for (String mapperAliasPackageClass : BeanAliasResolver.MAPPER_ALIAS_PACKAGE_CLASSES) {
            Optional<PsiClass> clazz = JavaUtils.findClazz(project, mapperAliasPackageClass);
            clazz.ifPresent(sqlSessionFactorySet::add);
        }
        return sqlSessionFactorySet;
    }
}
