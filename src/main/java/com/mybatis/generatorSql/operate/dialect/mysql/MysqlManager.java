package com.mybatis.generatorSql.operate.dialect.mysql;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.operate.*;
import com.mybatis.generatorSql.operate.dialect.BaseDialectManager;

import java.util.List;

public class MysqlManager extends BaseDialectManager {


    /**
     * Instantiates a new Mysql manager.
     *
     * @param mappingField the mapping field
     * @param entityClass  the entity class
     */
    public MysqlManager(List<TxField> mappingField, PsiClass entityClass) {
        super();
        init(mappingField, entityClass);
    }

    @Override
    protected void init(List<TxField> mappingField, PsiClass entityClass) {
        this.registerManagers(new SelectOperator(mappingField, entityClass));
        this.registerManagers(new CountOperator(mappingField, entityClass));


        this.registerManagers(new InsertOperator(mappingField) {
            @Override
            protected void initCustomArea(String areaName, List<TxField> mappingField) {
                super.initCustomArea(areaName, mappingField);
                MysqlInsertBatch customStatement = new MysqlInsertBatch();
                customStatement.initInsertBatch(areaName, mappingField);
                this.registerStatementBlock(customStatement.getStatementBlock());
                this.addOperatorName(customStatement.operatorName());
            }

        });

        this.registerManagers(new UpdateOperator(mappingField, entityClass));
        this.registerManagers(new DeleteOperator(mappingField));
    }
}
