package com.mybatis.generatorSql.operate;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.mybatis.generatorSql.MapperClassGenerateFactory;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TypeDescriptor;

import java.util.List;

public interface PlatformGenerator {
    String getDefaultDateWord();

    /**
     * 获取参数
     *
     * @return parameter
     */
    TypeDescriptor getParameter();

    /**
     * 返回值描述符
     *
     * @return return
     */
    TypeDescriptor getReturn();

    /**
     * 生成mapper方法
     *
     * @param mapperClassGenerateFactory PSI 方法描述
     * @param psiMethod
     * @param conditionFieldWrapper      the condition field wrapper
     * @param resultFields
     */
    void generateMapperXml(MapperClassGenerateFactory mapperClassGenerateFactory,
                           PsiMethod psiMethod,
                           ConditionFieldWrapper conditionFieldWrapper,
                           List<TxField> resultFields);

    /**
     * Gets condition fields.
     *
     * @return the condition fields
     */
    List<String> getConditionFields();

    /**
     * Gets all fields.
     *
     * @return the all fields
     */
    List<TxField> getAllFields();

    /**
     * Gets entity class.
     *
     * @return the entity class
     */
    PsiClass getEntityClass();

    List<String> getResultFields();
}
