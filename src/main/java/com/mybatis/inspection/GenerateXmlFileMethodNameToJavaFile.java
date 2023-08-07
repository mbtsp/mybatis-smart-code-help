package com.mybatis.inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.htmlInspections.HtmlLocalInspectionTool;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.profile.codeInspection.ProjectInspectionProfileManager;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.mybatis.enums.MethodNameEnums;
import com.mybatis.model.MybatisXmlInfo;
import com.mybatis.notifier.MybatisConfigNotification;
import com.mybatis.utils.DomUtils;
import com.mybatis.utils.JavaUtils;
import com.mybatis.utils.MybatisParameterType;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GenerateXmlFileMethodNameToJavaFile extends HtmlLocalInspectionTool {
    private static Logger logger = LoggerFactory.getLogger(GenerateXmlFileMethodNameToJavaFile.class);
    private MybatisXmlInfo mybatisXmlInfo;

    private static String methodStr(MybatisXmlInfo mybatisXmlInfo, PsiType psiType) {
        StringBuilder stringBuilder = new StringBuilder();
        if (psiType != null) {
            stringBuilder.append(psiType.getCanonicalText());
        }
        stringBuilder.append(" ");
        stringBuilder.append(mybatisXmlInfo.getId());
        stringBuilder.append("(");
        //构造参数

        //
        if (StringUtils.isNotBlank(mybatisXmlInfo.getParameterType()) && MybatisParameterType.contain(mybatisXmlInfo.getParameterType())) {
            MybatisParameterType mybatisParameterType = MybatisParameterType.getInstance(mybatisXmlInfo.getParameterType());
            if (mybatisParameterType != null && !mybatisParameterType.getKey().equals(MybatisParameterType.MAP.getKey())) {
                stringBuilder.append(mybatisParameterType.getValue()).append(" ").append(mybatisParameterType.getKey());
            } else if (mybatisParameterType != null) {
                stringBuilder.append(mybatisParameterType.getValue()).append(" ").append("map");
            }
            stringBuilder.append(")");
        } else if (StringUtils.isNotBlank(mybatisXmlInfo.getParameterType())) {
            List<String> models = mybatisXmlInfo.getModels();
            if (models != null) {
                for (String string : models) {
                    if (mybatisXmlInfo.getParameterType().equals(string)) {
                        String[] strings = string.split("\\.");
                        stringBuilder.append(string).append(" ").append(StringUtils.upperCaseFirstChar(strings[strings.length - 1]));
                        break;
                    }
                }
            }
            stringBuilder.append(")");
        } else {
            stringBuilder.append(")");
        }
        return stringBuilder.append(";").toString();
    }

    @Override
    protected void checkTag(@NotNull XmlTag tag, @NotNull ProblemsHolder holder, boolean isOnTheFly) {
        PsiFile psiFile = tag.getContainingFile();
        if (!DomUtils.isMybatisFile(psiFile)) {
            logger.info("is not mybatis file");
            return;
        }
        if (!(tag.getName().equals("select") || tag.getName().equals("insert") || tag.getName().equals("delete") || tag.getName().equals("update"))) {
            return;
        }
        Project project = tag.getProject();
        mybatisXmlInfo = DomUtils.getCurrentMybatisXmlType(tag).orElse(null);
        if (mybatisXmlInfo == null) {
            return;
        }
        if (StringUtils.isBlank(mybatisXmlInfo.getNamespace())) {
            return;
        }
        Optional<PsiClass[]> psiClasses = JavaUtils.findClasses(project, mybatisXmlInfo.getNamespace());
        if (psiClasses.isEmpty()) {
            return;
        }
        boolean flag = false;
        for (PsiClass psiClass : psiClasses.get()) {
            PsiMethod[] psiMethods = psiClass.getMethods();
            for (PsiMethod psiMethod : psiMethods) {
                String name = psiMethod.getName();
                if (StringUtils.isNotBlank(mybatisXmlInfo.getId()) && mybatisXmlInfo.getId().equals(name)) {
                    flag = true;
                    break;
                }
            }
        }
        XmlAttribute xmlAttribute = tag.getAttribute("id");
        if (xmlAttribute == null) {
            return;
        }
        XmlAttributeValue xmlAttributeValue = xmlAttribute.getValueElement();
        if (!flag && xmlAttributeValue != null) {
            holder.registerProblem(xmlAttributeValue, "Generate Java method", ProblemHighlightType.LIKE_UNUSED_SYMBOL, ElementManipulators.getValueTextRange(xmlAttributeValue), new GenerateMethodFix(mybatisXmlInfo));
        }

    }

    private static class GenerateMethodFix implements LocalQuickFix {
        private final MybatisXmlInfo mybatisXmlInfo;

        private GenerateMethodFix(MybatisXmlInfo mybatisXmlInfo) {
            this.mybatisXmlInfo = mybatisXmlInfo;
        }

        /**
         * @return text to appear in "Apply Fix" popup when multiple Quick Fixes exist (in the results of batch code inspection). For example,
         * if the name of the quickfix is "Create template &lt;filename&gt", the return value of getFamilyName() should be "Create template".
         * If the name of the quickfix does not depend on a specific element, simply return {@link #getName()}.
         */
        @Override
        public @NotNull String getFamilyName() {
            return "GenerateJavaMethod";
        }

        /**
         * Called to apply the fix.
         * <p>
         * Please call {@link ProjectInspectionProfileManager#fireProfileChanged()} if inspection profile is changed as result of fix.
         *
         * @param project    {@link Project}
         * @param descriptor problem reported by the tool which provided this quick fix action
         */
        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            Optional<PsiClass[]> psiClasses = JavaUtils.findClasses(project, mybatisXmlInfo.getNamespace());
            if (psiClasses.isEmpty() || psiClasses.get().length == 0) {
                MybatisConfigNotification.notifyError(project, mybatisXmlInfo.getNamespace() + " Class not find");
                return;
            }
            if (StringUtils.isBlank(mybatisXmlInfo.getId())) {
                MybatisConfigNotification.notifyError(project, "Xml id value is null");
                return;
            }
            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiClass[] psiClassList = psiClasses.get();
                PsiElementFactory psiElementFactory = PsiElementFactory.getInstance(project);
                String methodName = mybatisXmlInfo.getId();
                PsiType resultType = null;
                if (!mybatisXmlInfo.getTagType().equals(MethodNameEnums.SELECT)) {
                    resultType = psiElementFactory.createTypeFromText("int", null);
                } else {
                    if (mybatisXmlInfo.getResultType() != null && MybatisParameterType.contain(mybatisXmlInfo.getResultType())) {
                        MybatisParameterType mybatisParameterType = MybatisParameterType.getInstance(mybatisXmlInfo.getResultType());
                        if (mybatisParameterType != null) {
                            resultType = psiElementFactory.createTypeFromText(mybatisParameterType.getValue(), null);
                        }
                    } else {
                        if (StringUtils.isNotBlank(mybatisXmlInfo.getResultType())) {
                            String resultClass = mybatisXmlInfo.getModels() == null || mybatisXmlInfo.getModels().isEmpty() ? "int" : mybatisXmlInfo.getModels().get(0);
                            if (methodName.contains("ByPrimaryKey") || methodName.contains("ById")) {
                                resultType = psiElementFactory.createTypeFromText(resultClass, null);
                            } else {
                                if (resultClass.equals("int")) {
                                    resultClass = "java.lang.Integer";
                                }
                                resultType = psiElementFactory.createTypeFromText("java.util.List<" + resultClass + ">", null);
                            }
                        } else {
                            //Result type 没有配置，查找resultMap
                            if (mybatisXmlInfo.getResultMap() != null && MybatisParameterType.contain(mybatisXmlInfo.getResultMap())) {
                                MybatisParameterType mybatisParameterType = MybatisParameterType.getInstance(mybatisXmlInfo.getResultType());
                                if (mybatisParameterType != null) {
                                    resultType = psiElementFactory.createTypeFromText(mybatisParameterType.getValue(), null);
                                }
                            } else {
                                Map<String, XmlTag> resultMaps = mybatisXmlInfo.getResultMaps();
                                if (resultMaps != null && resultMaps.containsKey(mybatisXmlInfo.getResultMap())) {
                                    XmlTag xmlTag = resultMaps.get(mybatisXmlInfo.getResultMap());
                                    if (xmlTag == null) {
                                        resultType = psiElementFactory.createTypeFromText("int", null);
                                    } else {
                                        XmlAttribute xmlAttribute = xmlTag.getAttribute("type");
                                        if (xmlAttribute == null) {
                                            resultType = psiElementFactory.createTypeFromText("int", null);
                                        } else {
                                            String type = xmlAttribute.getValue();
                                            if (StringUtils.isBlank(type)) {
                                                resultType = psiElementFactory.createTypeFromText("int", null);
                                            } else {
                                                if (methodName.contains("ByPrimaryKey") || methodName.contains("ById")) {
                                                    resultType = psiElementFactory.createTypeFromText(type, null);
                                                } else {
                                                    resultType = psiElementFactory.createTypeFromText("java.util.List<" + type + ">", null);
                                                }
                                            }
                                        }

                                    }
                                } else {
                                    String resultClass = mybatisXmlInfo.getModels() == null || mybatisXmlInfo.getModels().isEmpty() ? "object" : mybatisXmlInfo.getModels().get(0);
                                    if (resultClass.equals("object")) {
                                        resultClass = "java.lang.Object";
                                    }
                                    resultType = psiElementFactory.createTypeFromText("java.util.List<" + resultClass + ">", null);
                                }
                            }
                        }
                    }

                }
                PsiMethod lastMethod = null;
                PsiClass lastClass = null;
                for (PsiClass psiClass : psiClassList) {
                    lastMethod = psiElementFactory.createMethodFromText(methodStr(mybatisXmlInfo, resultType), psiClass);
                    lastClass = psiClass;
                    PsiElement psiElement = lastClass.getRBrace();
                    PsiElement psiElement1 = lastClass.addBefore(lastMethod, psiElement);
                    PsiMethod psiMethod = (PsiMethod) psiElement1;
                    Editor editor = CodeInsightUtil.positionCursor(project, psiClass.getContainingFile(), psiMethod.getParameterList());
                    JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiElement1);
                    PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
                    CodeStyleManager.getInstance(project).reformat(psiClass);
                    editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() + 1);
                }
            });


        }
    }

    @Override
    public @NotNull HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
    }
}
