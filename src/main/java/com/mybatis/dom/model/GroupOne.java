package com.mybatis.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GroupOne extends DomElement {
    @SubTagList("include")
    @NotNull
    List<Include> getIncludes();

    @SubTagList("trim")
    @NotNull
    List<Trim> getTrims();

    @SubTagList("where")
    @NotNull
    List<Where> getWheres();

    @SubTagList("set")
    @NotNull
    List<Set> getSets();

    @SubTagList("foreach")
    @NotNull
    List<Foreach> getForeachs();

    @SubTagList("choose")
    @NotNull
    List<Choose> getChooses();

    @SubTagList("if")
    @NotNull
    List<If> getIfs();
}
