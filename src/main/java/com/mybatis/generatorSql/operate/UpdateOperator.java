package com.mybatis.generatorSql.operate;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.mybatis.generator.AbstractStatementGenerator;
import com.mybatis.generatorSql.appender.*;
import com.mybatis.generatorSql.factory.ConditionAppenderFactory;
import com.mybatis.generatorSql.factory.ResultAppenderFactory;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.mapping.model.TxReturnDescriptor;
import com.mybatis.generatorSql.operate.manager.StatementBlock;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateOperator extends BaseOperatorManager {


    /**
     * Instantiates a new Update operator.
     *
     * @param mappingField the mapping field
     * @param entityClass
     */
    public UpdateOperator(final List<TxField> mappingField, PsiClass entityClass) {
        this.setOperatorNameList(AbstractStatementGenerator.UPDATE_GENERATOR.getPatterns());
        this.init(mappingField, entityClass);
    }

    /**
     * Init.
     *
     * @param mappingField the mapping field
     * @param entityClass
     */
    public void init(final List<TxField> mappingField, PsiClass entityClass) {
        TxReturnDescriptor anInt = TxReturnDescriptor.createByOrigin(null, "int");

        for (final String areaName : this.getOperatorNameList()) {
            final ResultAppenderFactory updateFactory = new UpdateResultAppenderFactory(areaName);
            this.initResultAppender(updateFactory, mappingField, areaName);

            StatementBlock statementBlock = new StatementBlock();
            statementBlock.setTagName(areaName);
            statementBlock.setResultAppenderFactory(updateFactory);
            statementBlock.setConditionAppenderFactory(new ConditionAppenderFactory(areaName, mappingField));
            statementBlock.setReturnWrapper(anInt);
            this.registerStatementBlock(statementBlock);
        }

    }

    private void initResultAppender(final ResultAppenderFactory updateFactory, final List<TxField> mappingField, final String areaName) {
        for (final TxField field : mappingField) {
            // field
            // and + field
            final CompositeAppender andAppender = new CompositeAppender(
                    new CustomJoinAppender("And", ",\n", AreaSequence.RESULT),
                    new ResultAppenderFactory.WrapDateCustomFieldAppender(field, AreaSequence.RESULT));
            updateFactory.registerAppender(andAppender);

            // update + field
            final CompositeAppender areaAppender =
                    new CompositeAppender(
                            CustomAreaAppender.createCustomAreaAppender(areaName,
                                    ResultAppenderFactory.RESULT,
                                    AreaSequence.AREA,
                                    AreaSequence.RESULT,
                                    updateFactory),
                            new CustomFieldAppender(field, AreaSequence.RESULT)
                    );
            updateFactory.registerAppender(areaAppender);

        }
    }

    @Override
    public String getTagName() {
        return "update";
    }

    @Override
    public void generateMapperXml(String id,
                                  LinkedList<SyntaxAppender> jpaList,
                                  PsiClass entityClass,
                                  PsiMethod psiMethod,
                                  String tableName,
                                  Generator mybatisXmlGenerator,
                                  ConditionFieldWrapper conditionFieldWrapper,
                                  List<TxField> resultFields) {
        String mapperXml = super.generateXml(jpaList, entityClass, psiMethod, tableName, conditionFieldWrapper);
        mybatisXmlGenerator.generateUpdate(id, mapperXml);
    }

    private class UpdateResultAppenderFactory extends ResultAppenderFactory {

        /**
         * Instantiates a new Update result appender factory.
         *
         * @param areaPrefix the area prefix
         */
        public UpdateResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<TxParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            String operatorXml = "update " + tableName + "\n set ";

            return operatorXml + collector.stream().map(syntaxAppenderWrapper -> {
                return syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
            }).collect(Collectors.joining());
        }

        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {
            return new SyntaxAppenderWrapper(null, jpaStringList).getMxParameter(entityClass);
        }
    }
}
