package com.mybatis.generatorSql.db.adaptor;

import com.intellij.database.model.DasTable;
import com.intellij.database.model.DasTableKey;
import com.intellij.database.util.DasUtil;
import com.mybatis.utils.OracleGenerateUtil;

import java.util.Optional;

public class DasTableAdaptor implements MxDasTable {
    private DasTable dasTable;

    /**
     * Find sequence name optional.
     *
     * @param tableName the table name
     * @return the optional
     */
    public Optional<String> findSequenceName(String tableName) {
        return OracleGenerateUtil.findSequenceName(dasTable, tableName);
    }

    /**
     * Gets primary key.
     *
     * @return the primary key
     */
    public DasTableKey getPrimaryKey() {
        return DasUtil.getPrimaryKey(dasTable);
    }

    /**
     * Sets das table.
     *
     * @param dasTable the das table
     */
    public void setDasTable(DasTable dasTable) {
        this.dasTable = dasTable;
    }
}
