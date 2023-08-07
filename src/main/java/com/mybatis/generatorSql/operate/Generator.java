package com.mybatis.generatorSql.operate;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.mapping.model.TxField;

import java.util.List;

public interface Generator {
    /**
     * Generate select.
     *
     * @param id           the id
     * @param value        the value
     * @param resultType
     * @param resultMap    the result map
     * @param resultSet    the result set
     * @param resultFields
     * @param entityClass
     */
    void generateSelect(String id,
                        String value,
                        Boolean resultType,
                        String resultMap,
                        String resultSet,
                        List<TxField> resultFields,
                        PsiClass entityClass);

    /**
     * Generate delete.
     *
     * @param id    the id
     * @param value the value
     */
    void generateDelete(String id, String value);

    /**
     * Generate insert.
     *
     * @param id    the id
     * @param value the value
     */
    void generateInsert(String id, String value);

    /**
     * Generate update.
     *
     * @param id    the id
     * @param value the value
     */
    void generateUpdate(String id, String value);
}
