package com.mybatis.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Configuration extends DomElement {
    @SubTagList("typeAliases")
    @NotNull
    List<TypeAliases> getTypeAliases();
}
