package com.mybatis.inspection;

import com.google.common.collect.Lists;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.xml.DomElement;
import com.mybatis.annotation.Annotation;
import com.mybatis.dom.model.Select;
import com.mybatis.generator.AbstractStatementGenerator;
import com.mybatis.locator.MapperLocator;
import com.mybatis.service.JavaService;
import com.mybatis.utils.JavaUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GenerateStatementIntention extends AbstractBaseJavaLocalInspectionTool {
    public static final ProblemDescriptor[] EMPTY_ARRAY = new ProblemDescriptor[0];
    private static final Set<String> STATEMENT_PROVIDER_NAMES = new HashSet<>() {
        {
            add("org.apache.ibatis.annotations.SelectProvider");
            add("org.apache.ibatis.annotations.UpdateProvider");
            add("org.apache.ibatis.annotations.InsertProvider");
            add("org.apache.ibatis.annotations.DeleteProvider");
        }
    };
    private static final Set<String> MYBATIS_PLUS_BASE_MAPPER_NAMES = new HashSet<>() {
        {
            // mp3
            add("com.baomidou.mybatisplus.core.mapper.BaseMapper");
            // mp2
            add("com.baomidou.mybatisplus.mapper.BaseMapper");
        }
    };

    @Override
    public ProblemDescriptor @Nullable [] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (!MapperLocator.getInstance(method.getProject()).process(method)
                || JavaUtils.isAnyAnnotationPresent(method, Annotation.STATEMENT_SYMMETRIES)) {
            return EMPTY_ARRAY;
        }
        List<ProblemDescriptor> res = createProblemDescriptors(method, manager, isOnTheFly);
        return res.toArray(new ProblemDescriptor[0]);
    }

    private List<ProblemDescriptor> createProblemDescriptors(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        ArrayList<ProblemDescriptor> res = Lists.newArrayList();
        Optional<ProblemDescriptor> p1 = checkStatementExists(method, manager, isOnTheFly);
        p1.ifPresent(res::add);
        Optional<ProblemDescriptor> p2 = checkResultType(method, manager, isOnTheFly);
        p2.ifPresent(res::add);
        return res;
    }

    private Optional<ProblemDescriptor> checkResultType(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        Optional<DomElement> ele = JavaService.getInstance(method.getProject()).findStatement(method);
        if (ele.isPresent()) {
            DomElement domElement = ele.get();
            if (domElement instanceof Select) {
                Select select = (Select) domElement;
                Optional<PsiClass> target = AbstractStatementGenerator.getSelectResultType(method);
                PsiClass clazz = select.getResultType().getValue();
                PsiIdentifier ide = method.getNameIdentifier();
                if (null != ide && null == select.getResultMap().getValue()) {
                    if (target.isPresent()) {
                        final PsiClass targetClass = target.get();
                        String strValue = null;
                        if (clazz == null && select.getResultType().getXmlAttribute() != null) {
                            strValue = select.getResultType().getXmlAttribute().getValue();
                        }
                        if (!equalsOrInheritor(clazz, targetClass)) {
                            if (StringUtils.isNotBlank(strValue) && strValue.equals(targetClass.getName())) {
                                return Optional.empty();
                            }
                            String srcType = clazz != null ? clazz.getQualifiedName() : "";
                            String targetType = targetClass.getQualifiedName();
                            String descriptionTemplate = "Result type not match for select id=\"#ref\""
                                    + "\n srcType: " + srcType
                                    + "\n targetType: " + targetType;
                            ProblemDescriptor problemDescriptor = manager.createProblemDescriptor(ide,
                                    descriptionTemplate,
                                    (LocalQuickFix) null,
                                    ProblemHighlightType.GENERIC_ERROR,
                                    isOnTheFly);
                            return Optional.of(problemDescriptor);
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private boolean equalsOrInheritor(PsiClass child, PsiClass parent) {
        if (child == null) {
            return false;
        }
        return child.equals(parent) || child.isInheritor(parent, true);
    }

    private Optional<ProblemDescriptor> checkStatementExists(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        PsiIdentifier ide = method.getNameIdentifier();
        // SelectProvider爆红 issue: https://gitee.com/baomidou/MybatisX/issues/I17JQ4
        PsiAnnotation[] annotation = method.getAnnotations();
        if (annotation.length > 0) {
            // 如果存在提供者注解, 就返回验证成功
            for (PsiAnnotation psiAnnotation : annotation) {
                if (STATEMENT_PROVIDER_NAMES.contains(psiAnnotation.getQualifiedName())) {
                    return Optional.empty();
                }
            }
        }
        JavaService instance = JavaService.getInstance(method.getProject());
        if (instance.findStatement(method).isEmpty() && null != ide) {
            if (isMybatisPlusMethod(method)) {
                return Optional.empty();
            }
            // issue https://gitee.com/baomidou/MybatisX/issues/I3IT80
            final boolean isDefaultMethod = method.getModifierList().hasExplicitModifier(PsiModifier.DEFAULT);
            if (isDefaultMethod) {
                return Optional.empty();
            }
            return Optional.of(manager.createProblemDescriptor(ide, "Statement with id=\"#ref\" not defined in mapper xml",
                    new StatementNotExistsQuickFix(method), ProblemHighlightType.GENERIC_ERROR, isOnTheFly));
        }
        return Optional.empty();
    }

    private boolean isMybatisPlusMethod(PsiMethod method) {
        PsiClass parentOfType = PsiTreeUtil.getParentOfType(method, PsiClass.class);
        if (parentOfType == null) {
            return false;
        }
        PsiMethod[] methodsBySignature = parentOfType.findMethodsBySignature(method, true);
        if (methodsBySignature.length > 1) {
            for (int index = methodsBySignature.length; index > 0; index--) {
                PsiClass mapperClass = PsiTreeUtil.getParentOfType(methodsBySignature[index - 1], PsiClass.class);
                if (mapperClass == null) {
                    continue;
                }
                if (MYBATIS_PLUS_BASE_MAPPER_NAMES.contains(mapperClass.getQualifiedName())) {
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    private static class StatementNotExistsQuickFix implements LocalQuickFix {
        private final PsiMethod method;

        private StatementNotExistsQuickFix(PsiMethod method) {
            this.method = method;
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return "Generate Statement to XMl";
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            AbstractStatementGenerator.applyGenerate(method);
        }
    }
}
