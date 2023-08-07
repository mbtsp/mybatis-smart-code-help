package com.mybatis.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import com.intellij.util.xml.DomUtil;
import com.mybatis.enums.MethodNameEnums;
import com.mybatis.model.MybatisXmlInfo;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DomUtils {

    /**
     * 获取Mybatis xml 文件 当前光标所在位置，对应的方法xml Tag
     * getName() 获取方法名称
     * getAttribute("id").getValue() 获取 id 值
     * getAttribute("parameterType") 获取参数类型
     */
    public static Optional<MybatisXmlInfo> getCurrentMybatisXmlType(Editor editor, PsiFile psiFile) {
        if (!isMybatisFile(psiFile)) {
            return Optional.empty();
        }
        PsiElement psiElement = getCurrentTextDocument(editor, psiFile);
        if (psiElement == null) {
            return Optional.empty();
        }
        DomElement domElement = getCurrentDomElement(psiElement);
        if (domElement == null) {
            return Optional.empty();
        }
        XmlTag xmlTag = domElement.getXmlTag();
        if (xmlTag == null) {
            return Optional.empty();
        }
        MybatisXmlInfo mybatisXmlInfo = new MybatisXmlInfo();
        String name = xmlTag.getName();
        if (StringUtils.isNotBlank(name)) {
            if (name.equals(MethodNameEnums.SELECT.getKey())) {
                mybatisXmlInfo.setTagType(MethodNameEnums.SELECT);
            } else if (name.equals(MethodNameEnums.UPDATE.getKey())) {
                mybatisXmlInfo.setTagType(MethodNameEnums.UPDATE);
            } else if (name.equals(MethodNameEnums.DELETE.getKey())) {
                mybatisXmlInfo.setTagType(MethodNameEnums.DELETE);
            } else if (name.equals(MethodNameEnums.INSERT.getKey())) {
                mybatisXmlInfo.setTagType(MethodNameEnums.INSERT);
            }
        }
        mybatisXmlInfo.setId(getValue("id", xmlTag));
        mybatisXmlInfo.setParameterType(getValue("parameterType", xmlTag));
        mybatisXmlInfo.setResultType(getValue("resultType", xmlTag));
        mybatisXmlInfo.setResultMap(getValue("resultMap", xmlTag));
        if (domElement.getParent() != null) {
            DomElement rootDomElement = domElement.getParent().getParent();
            if (rootDomElement != null) {
                mybatisXmlInfo.setNamespace(getValue("namespace", rootDomElement.getXmlTag()));
                XmlTag rootXmlTag = rootDomElement.getXmlTag();
                if (rootXmlTag != null) {
                    XmlTag[] mapperChild = rootXmlTag.getSubTags();
                    Map<String, XmlTag> resultMaps = new HashMap<>();
                    for (XmlTag tag : mapperChild) {
                        if (tag.getName().equals("resultMap")) {
                            String id = getValue("id", tag);
                            if (StringUtils.isNotBlank(id)) {
                                resultMaps.put(id, tag);
                            }

                        }
                    }
                    mybatisXmlInfo.setResultMaps(resultMaps);
                }

            }

        }
        return Optional.of(mybatisXmlInfo);
    }

    public static Optional<MybatisXmlInfo> getCurrentMybatisXmlType(XmlTag xmlTag) {
        if (xmlTag == null) {
            return Optional.empty();
        }
        MybatisXmlInfo mybatisXmlInfo = new MybatisXmlInfo();
        String name = xmlTag.getName();
        if (StringUtils.isNotBlank(name)) {
            if (name.equals(MethodNameEnums.SELECT.getKey())) {
                mybatisXmlInfo.setTagType(MethodNameEnums.SELECT);
            } else if (name.equals(MethodNameEnums.UPDATE.getKey())) {
                mybatisXmlInfo.setTagType(MethodNameEnums.UPDATE);
            } else if (name.equals(MethodNameEnums.DELETE.getKey())) {
                mybatisXmlInfo.setTagType(MethodNameEnums.DELETE);
            } else if (name.equals(MethodNameEnums.INSERT.getKey())) {
                mybatisXmlInfo.setTagType(MethodNameEnums.INSERT);
            }
        }
        mybatisXmlInfo.setId(getValue("id", xmlTag));
        mybatisXmlInfo.setParameterType(getValue("parameterType", xmlTag));
        mybatisXmlInfo.setResultType(getValue("resultType", xmlTag));
        mybatisXmlInfo.setResultMap(getValue("resultMap", xmlTag));
        if (xmlTag.getParentTag() != null) {
            XmlTag rootXmlTag = xmlTag.getParentTag();
            if (rootXmlTag != null) {
                mybatisXmlInfo.setNamespace(getValue("namespace", rootXmlTag));
                XmlTag[] mapperChild = rootXmlTag.getSubTags();
                List<String> models = new ArrayList<>();
                Map<String, XmlTag> resultMaps = new HashMap<>();
                Set<XmlAttributeValue> columns = ConcurrentHashMap.newKeySet();
                Set<XmlAttributeValue> propertyList = ConcurrentHashMap.newKeySet();
                Set<XmlAttributeValue> jdbcTypes = ConcurrentHashMap.newKeySet();
                for (XmlTag tag : mapperChild) {
                    if (tag.getName().equals("resultMap")) {
                        String id = getValue("id", tag);
                        if (StringUtils.isNotBlank(id)) {
                            resultMaps.put(id, tag);
                        }
                        String type = getValue("type", tag);
                        if (StringUtils.isNotBlank(type)) {
                            models.add(type);
                        }
                        XmlTag[] childTags = tag.getSubTags();
                        for (XmlTag childTag : childTags) {
                            Optional<XmlAttributeValue> columnXmlAttributeValue = getValueElement(childTag, "column");
                            columnXmlAttributeValue.ifPresent(columns::add);
                            Optional<XmlAttributeValue> propertyXmlAttributeValue = getValueElement(childTag, "property");
                            propertyXmlAttributeValue.ifPresent(propertyList::add);
                            Optional<XmlAttributeValue> jdbcTypeXmlAttributeValue = getValueElement(childTag, "jdbcType");
                            jdbcTypeXmlAttributeValue.ifPresent(jdbcTypes::add);
                        }

                    }
                }
                mybatisXmlInfo.setColumns(columns);
                mybatisXmlInfo.setJdbcTypes(jdbcTypes);
                mybatisXmlInfo.setPropertyList(propertyList);
                mybatisXmlInfo.setResultMaps(resultMaps);
                mybatisXmlInfo.setModels(models);
            }
        }
        return Optional.of(mybatisXmlInfo);
    }

    public static Optional<XmlAttributeValue> getValueElement(XmlTag tag, String key) {
        if (tag == null || StringUtils.isBlank(key)) {
            return Optional.empty();
        }
        XmlAttribute xmlAttribute = tag.getAttribute(key);
        if (xmlAttribute == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(xmlAttribute.getValueElement());
    }

    public static String getValue(String key, XmlTag xmlTag) {
        if (xmlTag == null) {
            return null;
        }
        if (StringUtils.isBlank(key)) {
            return null;
        }
        XmlAttribute xmlAttribute = xmlTag.getAttribute(key);
        if (xmlAttribute == null) {
            return null;
        }
        return xmlAttribute.getValue();
    }

    public static DomElement getCurrentDomElement(PsiElement psiElement) {
        return DomUtil.getDomElement(psiElement);
    }

    public static PsiElement getCurrentTextDocument(Editor editor, PsiFile file) {
        if (editor == null || file == null) {
            return null;
        }
        return file.findElementAt(editor.getCaretModel().getOffset());
    }


    @NotNull
    @NonNls
    public static <T extends DomElement> Collection<T> findDomElements(@NotNull Project project, Class<T> clazz) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        List<DomFileElement<T>> elements = DomService.getInstance().getFileElements(clazz, project, scope);
        return elements.stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
    }


    public static boolean isMybatisFile(@Nullable PsiFile file) {
        Boolean mybatisFile = null;
        if (file == null) {
            mybatisFile = false;
        }
        if (mybatisFile == null) {
            if (!isXmlFile(file)) {
                mybatisFile = false;
            }
        }
        if (mybatisFile == null) {
            XmlTag rootTag = ((XmlFile) file).getRootTag();
            if (rootTag == null) {
                mybatisFile = false;
            }
            if (mybatisFile == null) {
                if (!"mapper".equals(rootTag.getName())) {
                    mybatisFile = false;
                }
            }
        }
        if (mybatisFile == null) {
            mybatisFile = true;
        }
        return mybatisFile;
    }


    public static boolean isMybatisConfigurationFile(@NotNull PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return (null != rootTag && rootTag.getName().equals("configuration"));
    }


    public static boolean isBeansFile(@NotNull PsiFile file) {
        if (!isXmlFile(file)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        return (null != rootTag && rootTag.getName().equals("beans"));
    }


    public static boolean isXmlFile(@NotNull PsiFile file) {
        return file instanceof XmlFile;
    }
}
