package com.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Mapper extends DomElement {
    @SubTagsList({"insert", "update", "delete", "select"})
    @NotNull
    List<IdDomElement> getDaoElements();

    @Required
    @NameValue
    @Attribute("namespace")
    @Convert(PsiClassConverter.class)
    @NotNull
    GenericAttributeValue<PsiClass> getNamespace();

    @SubTagList("resultMap")
    @NotNull
    List<ResultMap> getResultMaps();

    @SubTagList("parameterMap")
    @NotNull
    List<ParameterMap> getParameterMaps();

    @SubTagList("sql")
    @NotNull
    List<Sql> getSqls();

    @SubTagList("insert")
    @NotNull
    List<Insert> getInserts();

    @SubTagList("update")
    @NotNull
    List<Update> getUpdates();

    @SubTagList("delete")
    @NotNull
    List<Delete> getDeletes();

    @SubTagList("select")
    @NotNull
    List<Select> getSelects();

    @SubTagList("select")
    Select addSelect();

    @SubTagList("update")
    Update addUpdate();

    @SubTagList("insert")
    Insert addInsert();

    @SubTagList("delete")
    Delete addDelete();
}
