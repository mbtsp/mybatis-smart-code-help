package com.mybatis.generatorSql.operate.dialect.oracle;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.db.adaptor.DasTableAdaptor;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.operate.*;
import com.mybatis.generatorSql.operate.dialect.BaseDialectManager;

import java.util.List;

public class OracleManager extends BaseDialectManager {
    private DasTableAdaptor dasTable;
    private String tableName;

    /**
     * Instantiates a new Oracle manager.
     *
     * @param mappingField the mapping field
     * @param entityClass  the entity class
     * @param dasTable     the das table
     * @param tableName    the table name
     */
    public OracleManager(List<TxField> mappingField, PsiClass entityClass, DasTableAdaptor dasTable, String tableName) {
        this.dasTable = dasTable;
        this.tableName = tableName;
        init(mappingField, entityClass);
    }

    @Override
    protected void init(List<TxField> mappingField, PsiClass entityClass) {
        registerManagers(new SelectOperator(mappingField, entityClass));
        this.registerManagers(new CountOperator(mappingField, entityClass));
        this.registerManagers(new InsertOperator(mappingField) {
            @Override
            protected void initCustomArea(String areaName, List<TxField> mappingField) {
                super.initCustomArea(areaName, mappingField);
                // insert into 的方式批量插入
                OracleInsertBatchWithUnion oracleInsertBatch = new OracleInsertBatchWithUnion(dasTable, tableName);
                oracleInsertBatch.initInsertBatch(areaName, mappingField);
                this.registerStatementBlock(oracleInsertBatch.getStatementBlock());
                this.addOperatorName(oracleInsertBatch.operatorName());

                // insert all into table 的方式批量插入
                OracleInsertBatchWithAll oracleInsertBatchWithAll = new OracleInsertBatchWithAll(dasTable, tableName);
                oracleInsertBatchWithAll.initInsertBatch(areaName, mappingField);
                this.registerStatementBlock(oracleInsertBatchWithAll.getStatementBlock());
                this.addOperatorName(oracleInsertBatchWithAll.operatorName());
            }
        });
        registerManagers(new UpdateOperator(mappingField, entityClass));
        registerManagers(new DeleteOperator(mappingField));
    }
}
