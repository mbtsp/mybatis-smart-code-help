package com.mybatis.generatorSql.appender.changer;

import com.mybatis.generatorSql.appender.MxParameterChanger;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InParameterChanger implements MxParameterChanger {
    private static final Logger logger = LoggerFactory.getLogger(InParameterChanger.class);

    @Override
    public List<TxParameter> getParameter(TxParameter txParameter) {
        TxParameter collectionParameter = TxParameter.createCollectionByTxParameter(txParameter);
        return Collections.singletonList(collectionParameter);
    }

    @Override
    public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
        final TxParameter parameter = parameters.poll();
        if (parameter == null) {
            logger.info("parameter is null, can not getTemplateText");
            return "";
        }
        final String collectionName = parameter.getName();
        String itemName = "item";
        String itemContent = "#{" + itemName + "}";
        // 如果集合的泛型不是空的, 就给遍历的内容加入 jdbcType
        if (parameter.getItemContent(itemName) != null) {
            itemContent = parameter.getItemContent(itemName);
        }
        return fieldName + " " + getIn() + "\n" +
                "<foreach collection=\"" +
                collectionName +
                "\" item=\"item\" open=\"(\" close=\")\" separator=\",\">" + "\n" +
                itemContent + "\n" +
                "</foreach>";
    }

    /**
     * Gets in.
     *
     * @return the in
     */
    @NotNull
    protected String getIn() {
        return "in";
    }


}
