package com.mybatis.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TypeAliases extends DomElement {
    @SubTagList("typeAlias")
    @NotNull
    List<TypeAlias> getTypeAlias();

    @SubTagList("package")
    @NotNull
    List<Package> getPackages();
}
