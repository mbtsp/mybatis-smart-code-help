package com.mybatis.generatorSql.appender;

import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.operate.suffix.SuffixOperator;

import java.util.List;

public interface MxParameterChanger extends SuffixOperator {
    /**
     * Gets parameter.
     *
     * @param txParameter the tx parameter
     * @return the parameter
     */
    List<TxParameter> getParameter(TxParameter txParameter);
}
