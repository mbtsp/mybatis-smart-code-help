package com.mybatis.generatorSql.operate.manager;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.db.adaptor.DasTableAdaptor;
import com.mybatis.generatorSql.db.adaptor.DbmsAdaptor;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.operate.dialect.mysql.MysqlManager;
import com.mybatis.generatorSql.operate.dialect.oracle.OracleManager;

import java.util.List;

public class AreaOperateManagerFactory {

    /**
     * Gets by dbms.
     *
     * @param dbms         the dbms
     * @param mappingField the mapping field
     * @param entityClass  the entity class
     * @param dasTable     the das table
     * @param tableName    the table name
     * @return the by dbms
     */
    public static AreaOperateManager getAreaOperateManagerByDbms(DbmsAdaptor dbms,
                                                                 List<TxField> mappingField,
                                                                 PsiClass entityClass,
                                                                 DasTableAdaptor dasTable,
                                                                 String tableName) {
        if (dbms == DbmsAdaptor.ORACLE) {
            return new OracleManager(mappingField, entityClass, dasTable, tableName);
        }
        return new MysqlManager(mappingField, entityClass);
    }
}
