package com.mybatis.dom.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.mybatis.dom.model.Association;
import com.mybatis.dom.model.Collection;
import com.mybatis.dom.model.ParameterMap;
import com.mybatis.dom.model.ResultMap;
import com.mybatis.utils.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MapperBacktrackingUtils {

    private MapperBacktrackingUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets property clazz.
     *
     * @param attributeValue the attribute value
     * @return the property clazz
     */
    public static Optional<PsiClass> getPropertyClazz(XmlAttributeValue attributeValue) {
        DomElement domElement = DomUtil.getDomElement(attributeValue);
        if (null == domElement) {
            return Optional.empty();
        }

        Collection collection = DomUtil.getParentOfType(domElement, Collection.class, true);
        if (null != collection && !isWithinSameTag(collection, attributeValue)) {
            PsiClass collectionClass = findLeastParentType(collection, attributeValue.getProject());
            return Optional.ofNullable(collectionClass);
        }

        Association association = DomUtil.getParentOfType(domElement, Association.class, true);
        if (null != association && !isWithinSameTag(association, attributeValue)) {
            PsiClass associationClass = findLeastParentType(association, attributeValue.getProject());
            return Optional.ofNullable(associationClass);
        }

        ParameterMap parameterMap = DomUtil.getParentOfType(domElement, ParameterMap.class, true);
        if (null != parameterMap && !isWithinSameTag(parameterMap, attributeValue)) {
            return Optional.ofNullable(parameterMap.getType().getValue());
        }

        ResultMap resultMap = DomUtil.getParentOfType(domElement, ResultMap.class, true);
        if (null != resultMap && !isWithinSameTag(resultMap, attributeValue)) {
            return Optional.ofNullable(resultMap.getType().getValue());
        }
        return Optional.empty();
    }

    @Nullable
    private static PsiClass findLeastParentType(DomElement domElement, @NotNull Project project) {
        DomElement currentElement = domElement;
        PsiClass currentClass = null;
        while (true) {
            if (currentElement instanceof Association) {
                boolean foundClass = false;
                Association association = (Association) currentElement;
                PsiClass javaType = association.getJavaType().getValue();
                if (javaType != null) {
                    currentClass = javaType;
                    foundClass = true;
                }
                if (currentClass == null) {
                    XmlAttributeValue value = association.getProperty().getValue();
                    if (value != null) {
                        String propertyName = value.getValue();
                        PsiClass leastParentType = findLeastParentType(currentElement.getParent(), project);
                        if (leastParentType != null) {
                            Optional<PsiField> settablePsiField = JavaUtils.findSettablePsiField(leastParentType, propertyName);
                            if (settablePsiField.isPresent()) {
                                PsiField psiField = settablePsiField.get();
                                String fieldTypeClassName = psiField.getType().getCanonicalText();
                                Optional<PsiClass> fieldClassOptional = JavaUtils.findClazz(project, fieldTypeClassName);
                                if (fieldClassOptional.isPresent()) {
                                    currentClass = fieldClassOptional.get();
                                    foundClass = true;
                                }
                            }
                        }
                    }
                }
                if (foundClass) {
                    break;
                }
            } else if (currentElement instanceof Collection) {
                boolean foundClass = false;
                Collection collection = (Collection) currentElement;
                GenericAttributeValue<PsiClass> type = collection.getOfType();
                if (type.getValue() != null) {
                    currentClass = type.getValue();
                    foundClass = true;
                }
                if (currentClass == null) {
                    XmlAttributeValue value = collection.getProperty().getValue();
                    if (value != null) {
                        String propertyName = value.getValue();
                        PsiClass leastParentType = findLeastParentType(currentElement.getParent(), project);
                        if (leastParentType != null) {
                            Optional<PsiField> settablePsiField = JavaUtils.findSettablePsiField(leastParentType, propertyName);
                            if (settablePsiField.isPresent()) {
                                PsiField psiField = settablePsiField.get();
                                Optional<String> genericClassOfListByField = findGenericClassOfListByField(psiField);
                                if (genericClassOfListByField.isPresent()) {
                                    String fieldTypeClassName = genericClassOfListByField.get();
                                    Optional<PsiClass> fieldClassOptional = JavaUtils.findClazz(project, fieldTypeClassName);
                                    if (fieldClassOptional.isPresent()) {
                                        currentClass = fieldClassOptional.get();
                                        foundClass = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (foundClass) {
                    break;
                }
            } else if (currentElement instanceof ResultMap) {
                ResultMap association = (ResultMap) currentElement;
                GenericAttributeValue<PsiClass> type = association.getType();
                currentClass = type.getValue();
            }

            if (currentElement == null) {
                break;
            }
            currentElement = currentElement.getParent();
        }
        return currentClass;
    }

    private static Optional<String> findGenericClassOfListByField(PsiField psiField) {
        PsiTypeElement typeElement = psiField.getTypeElement();
        if (typeElement != null) {
            PsiJavaCodeReferenceElement innermostComponentReferenceElement = typeElement.getInnermostComponentReferenceElement();
            if (innermostComponentReferenceElement != null) {
                PsiType[] typeParameters = innermostComponentReferenceElement.getTypeParameters();
                if (typeParameters.length == 1) {
                    PsiType typeParameter = typeParameters[0];
                    return Optional.of(typeParameter.getCanonicalText());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Is within same tag boolean.
     *
     * @param domElement the dom element
     * @param xmlElement the xml element
     * @return the boolean
     */
    public static boolean isWithinSameTag(@NotNull DomElement domElement, XmlAttributeValue xmlElement) {
        XmlTag xmlTag = PsiTreeUtil.getParentOfType(xmlElement, XmlTag.class);
        return null != xmlElement && domElement.getXmlTag().equals(xmlTag);
    }

    public static Optional<PsiClass> getEntityClass(XmlAttributeValue attributeValue) {
        DomElement domElement = DomUtil.getDomElement(attributeValue);
        if (null == domElement) {
            return Optional.empty();
        }
        ResultMap resultMap = DomUtil.getParentOfType(domElement, ResultMap.class, true);
        if (null != resultMap && !isWithinSameTag(resultMap, attributeValue)) {
            return Optional.ofNullable(resultMap.getType().getValue());
        }
        return Optional.empty();
    }
}
