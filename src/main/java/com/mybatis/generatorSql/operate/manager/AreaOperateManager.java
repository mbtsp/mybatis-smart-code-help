package com.mybatis.generatorSql.operate.manager;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.mapping.model.TypeDescriptor;
import com.mybatis.generatorSql.operate.Generator;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public interface AreaOperateManager {
    /**
     * 将字符串分割成 符号追加器
     *
     * @param splitParam the split param
     * @return linked list
     */
    @NotNull
    LinkedList<SyntaxAppender> splitAppenderByText(String splitParam);

    /**
     * 获取生成的内容
     *
     * @param splitList the split list
     * @return completion content
     */
    List<String> getCompletionContent(LinkedList<SyntaxAppender> splitList);

    /**
     * Gets completion content.
     *
     * @return the completion content
     */
    List<String> getCompletionContent();

    /**
     * Gets parameters.
     *
     * @param entityClass   the entity class
     * @param jpaStringList the jpa string list
     * @return the parameters
     */
    List<TxParameter> getParameters(PsiClass entityClass, LinkedList<SyntaxAppender> jpaStringList);

    /**
     * Gets return wrapper.
     *
     * @param text        the text
     * @param entityClass the entity class
     * @param linkedList  the linked list
     * @return the return wrapper
     */
    TypeDescriptor getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList);

    /**
     * 当前区域是否支持这个操作
     *
     * @param operatorText 操作区文本
     * @return boolean
     */
    boolean support(String operatorText);

    /**
     * Generate mapper xml.
     *
     * @param id                    the id
     * @param jpaList               输入的文本
     * @param entityClass           类
     * @param psiMethod             方法
     * @param tableName             表名
     * @param mybatisXmlGenerator   the mybatis xml generator
     * @param conditionFieldWrapper the condition field wrapper
     * @param resultFields
     */
    void generateMapperXml(String id,
                           LinkedList<SyntaxAppender> jpaList,
                           PsiClass entityClass,
                           PsiMethod psiMethod,
                           String tableName,
                           Generator mybatisXmlGenerator,
                           ConditionFieldWrapper conditionFieldWrapper,
                           List<TxField> resultFields);

}
