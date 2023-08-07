package com.mybatis.generatorSql.mapping;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.mapping.model.TxField;

import java.util.List;

/**
 * @author liushang@zsyjr.com
 */
public class EntityMappingHolder {
    private PsiClass entityClass;

    private String tableName;

    private List<TxField> fields;

    public PsiClass getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(PsiClass entityClass) {
        this.entityClass = entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TxField> getFields() {
        return fields;
    }

    public void setFields(List<TxField> fields) {
        this.fields = fields;
    }
}
