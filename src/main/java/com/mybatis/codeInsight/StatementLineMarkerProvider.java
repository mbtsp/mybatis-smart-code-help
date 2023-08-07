package com.mybatis.codeInsight;

import com.google.common.collect.ImmutableSet;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.mybatis.dom.model.*;
import com.mybatis.utils.IconUtils;
import com.mybatis.utils.JavaUtils;
import com.mybatis.utils.MapperUtils;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Optional;

public class StatementLineMarkerProvider extends SimpleLineMarkerProvider<XmlToken, PsiElement> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleLineMarkerProvider.class);
    private static final String MAPPER_CLASS = Mapper.class.getSimpleName().toLowerCase();
    private static final ImmutableSet<String> TARGET_TYPES = ImmutableSet.of(
            Mapper.class.getSimpleName().toLowerCase(),
            Select.class.getSimpleName().toLowerCase(),
            Insert.class.getSimpleName().toLowerCase(),
            Update.class.getSimpleName().toLowerCase(),
            Delete.class.getSimpleName().toLowerCase()
    );

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
        return element instanceof XmlToken
                && isTargetType(((XmlToken) element))
                && MapperUtils.isElementWithinMybatisFile(element);
    }

    @Override
    public Optional<? extends PsiElement[]> apply(@NotNull PsiElement from) {
        DomElement domElement = DomUtil.getDomElement(from);
        if (null == domElement) {
            return Optional.empty();
        }
        // 方法
        else if (domElement instanceof IdDomElement) {
            return JavaUtils.findMethods(from.getProject(),
                    MapperUtils.getNamespace(domElement),
                    MapperUtils.getId((IdDomElement) domElement));
        } else {
            XmlTag xmlTag = domElement.getXmlTag();
            if (xmlTag == null) {
                return Optional.empty();
            }
            String namespace = xmlTag.getAttributeValue("namespace");
            if (StringUtils.isEmpty(namespace)) {
                return Optional.empty();
            }
            return JavaUtils.findClasses(from.getProject(), namespace);
        }
    }

    private boolean isTargetType(@NotNull XmlToken token) {
        Boolean targetType = null;
        if (MAPPER_CLASS.equals(token.getText())) {
            // 判断当前元素是开始节点
            PsiElement nextSibling = token.getNextSibling();
            if (nextSibling instanceof PsiWhiteSpace) {
                targetType = true;
            }
        }
        if (targetType == null) {
            if (TARGET_TYPES.contains(token.getText())) {
                PsiElement parent = token.getParent();
                // 判断当前节点是标签
                if (parent instanceof XmlTag) {
                    // 判断当前元素是开始节点
                    PsiElement nextSibling = token.getNextSibling();
                    if (nextSibling instanceof PsiWhiteSpace) {
                        targetType = true;
                    }
                }
            }
        }
        if (targetType == null) {
            targetType = false;
        }
        return targetType;
    }


    @Override
    public @Nullable("null means disabled") @Nls(capitalization = Nls.Capitalization.Sentence) String getName() {
        return "Statement line marker";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return IconUtils.XML_MYBATIS_ICON;
    }


    @Override
    @NotNull
    public String getTooltip(PsiElement element, @NotNull PsiElement target) {
        String text = null;
        if (element instanceof PsiMethod) {
            PsiMethod psiMethod = (PsiMethod) element;
            PsiClass containingClass = psiMethod.getContainingClass();
            if (containingClass != null) {
                text = containingClass.getName() + "#" + psiMethod.getName();
            }
        }
        if (text == null && element instanceof PsiClass) {
            PsiClass psiClass = (PsiClass) element;
            text = psiClass.getQualifiedName();
        }
        if (text == null) {
            text = target.getContainingFile().getText();
        }
        return "navigation to mapper class:" + text;
    }

}
