package com.mybatis.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class JavaService {
    public static JavaService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, JavaService.class);
    }

    public void process(@NotNull PsiElement psiElement, @NotNull Processor processor) {
        if (psiElement instanceof PsiMethod) {
            process((PsiMethod) psiElement, processor);
        } else if (psiElement instanceof PsiClass) {
            process((PsiClass) psiElement, processor);
        }
    }

    public void process(PsiClass psiClass, Processor<Mapper> processor) {
        if (processor == null || psiClass == null) {
            return;
        }
        String qualifiedName = psiClass.getQualifiedName();
        for (Mapper mapper : MapperUtils.findMappers(psiClass.getProject())) {
            if (MapperUtils.getNamespace(mapper).equals(qualifiedName)) {
                processor.process(mapper);
            }
        }
    }

    public void process(PsiMethod psiMethod, Processor<IdDomElement> processor) {
        if (psiMethod == null || processor == null) {
            return;
        }
        PsiClass psiClass = psiMethod.getContainingClass();
        if (psiClass == null) {
            return;
        }
        String qualifiedName = psiClass.getQualifiedName() + "." + psiMethod.getName();
        Collection<Mapper> mappers = MapperUtils.findMappers(psiMethod.getProject());
        for (Mapper mapper : mappers) {
            for (IdDomElement idDomElement : mapper.getDaoElements()) {
                if (MapperUtils.getIdSignature(idDomElement).equals(qualifiedName)) {
                    processor.process(idDomElement);
                }
            }
        }
    }

    /**
     * Find with find first processor optional.
     *
     * @param <T>    the type parameter
     * @param target the target
     * @return the optional
     */
    public <T> Optional<T> findWithFindFirstProcessor(@NotNull PsiElement target) {
        CommonProcessors.FindFirstProcessor<T> processor = new CommonProcessors.FindFirstProcessor<T>();
        process(target, processor);
        return Optional.ofNullable(processor.getFoundValue());
    }

    /**
     * Gets reference clazz of psi field.
     *
     * @param field the field
     * @return the reference clazz of psi field
     */
    public Optional<PsiClass> getReferenceClazzOfPsiField(@NotNull PsiElement field) {
        if (!(field instanceof PsiField)) {
            return Optional.empty();
        }
        PsiType type = ((PsiField) field).getType();
        return type instanceof PsiClassReferenceType ? Optional.ofNullable(((PsiClassReferenceType) type).resolve()) :
                Optional.empty();
    }

    public Optional<DomElement> findStatement(@Nullable PsiMethod method) {
        CommonProcessors.FindFirstProcessor<IdDomElement> processor = new CommonProcessors.FindFirstProcessor<>();
        process(method, processor);
        return processor.isFound() ? Optional.ofNullable(processor.getFoundValue()) : Optional.empty();
    }

    public void processClass(@NotNull PsiClass clazz, @NotNull Processor<Mapper> processor) {
        String ns = clazz.getQualifiedName();
        for (Mapper mapper : MapperUtils.findMappers(clazz.getProject())) {
            if (MapperUtils.getNamespace(mapper).equals(ns)) {
                processor.process(mapper);
            }
        }
    }

    /**
     * Process.
     *
     * @param psiMethod the psi method
     * @param processor the processor
     */
    public void processMethod(@NotNull PsiMethod psiMethod, @NotNull Processor<IdDomElement> processor) {
        PsiClass psiClass = psiMethod.getContainingClass();
        if (null == psiClass) {
            return;
        }
        String id = psiClass.getQualifiedName() + "." + psiMethod.getName();
        Collection<Mapper> mappers = MapperUtils.findMappers(psiMethod.getProject());

        mappers.stream()
                .flatMap(mapper -> mapper.getDaoElements().stream())
                .filter(idDom -> MapperUtils.getIdSignature(idDom).equals(id))
                .forEach(processor::process);

    }
}
