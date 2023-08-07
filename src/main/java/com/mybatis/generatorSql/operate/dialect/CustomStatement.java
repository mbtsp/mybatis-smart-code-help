package com.mybatis.generatorSql.operate.dialect;

import com.mybatis.generatorSql.operate.manager.StatementBlock;

public interface CustomStatement {
    /**
     * Gets statement block.
     *
     * @return the statement block
     */
    StatementBlock getStatementBlock();

    /**
     * Operator name string.
     *
     * @return the string
     */
    String operatorName();
}
