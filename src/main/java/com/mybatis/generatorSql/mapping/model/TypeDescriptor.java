package com.mybatis.generatorSql.mapping.model;

import java.util.List;

/**
 * The interface Type descriptor.
 */
public interface TypeDescriptor {
    /**
     * 导入的内容
     *
     * @return import list
     */
    List<String> getImportList();

    /**
     * 实际的内容
     *
     * @param defaultDateList
     * @return content
     */
    String getContent(List<String> defaultDateList);

    default void initResultType(String qualifiedName, String simpleName) {

    }
}
