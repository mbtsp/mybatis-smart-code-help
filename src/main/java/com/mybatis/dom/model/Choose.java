package com.mybatis.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Choose extends DomElement {
    @Required
    @SubTagList("when")
    @NotNull
    List<When> getWhens();

    @SubTag("otherwise")
    Otherwise getOtherwise();
}
