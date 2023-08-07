package com.mybatis.generatorSql.operate.dialect.mysql;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.appender.CustomAreaAppender;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.factory.ResultAppenderFactory;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.mapping.model.TxReturnDescriptor;
import com.mybatis.generatorSql.operate.dialect.CustomStatement;
import com.mybatis.generatorSql.operate.dialect.oracle.InsertCustomSuffixAppender;
import com.mybatis.generatorSql.operate.manager.StatementBlock;
import com.mybatis.generatorSql.operate.suffix.SuffixOperator;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;
import com.mybatis.utils.JdbcTypeUtils;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class MysqlInsertBatch implements CustomStatement {

    /**
     * The Statement block.
     */
    StatementBlock statementBlock;
    /**
     * The Operator name.
     */
    String operatorName;


    /**
     * Instantiates a new Mysql insert batch.
     */
    public MysqlInsertBatch() {

    }

    /**
     * Init insert batch.
     *
     * @param areaName     the area name
     * @param mappingField the mapping field
     */
    public void initInsertBatch(String areaName, List<TxField> mappingField) {
        String newAreaName = getNewAreaName(areaName);
        // insertBatch
        ResultAppenderFactory appenderFactory = getResultAppenderFactory(mappingField, newAreaName);
        // insert + Batch
        final SyntaxAppender batchAppender =
                CustomAreaAppender.createCustomAreaAppender(newAreaName,
                        ResultAppenderFactory.RESULT,
                        AreaSequence.AREA,
                        AreaSequence.RESULT,
                        appenderFactory);
        appenderFactory.registerAppender(batchAppender);

        StatementBlock statementBlock = new StatementBlock();
        statementBlock.setResultAppenderFactory(appenderFactory);
        statementBlock.setTagName(newAreaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createByOrigin(null, "int"));
        this.statementBlock = statementBlock;

        this.operatorName = newAreaName;
    }

    /**
     * Gets result appender factory.
     *
     * @param mappingField the mapping field
     * @param newAreaName  the new area name
     * @return the result appender factory
     */
    protected ResultAppenderFactory getResultAppenderFactory(List<TxField> mappingField, String newAreaName) {
        ResultAppenderFactory appenderFactory = new InsertBatchResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<TxParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
                // 定制参数
                SyntaxAppender suffixOperator = InsertCustomSuffixAppender.createInsertBySuffixOperator(batchName(),
                        getSuffixOperator(mappingField),
                        AreaSequence.RESULT);
                LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrappers = new LinkedList<>();
                syntaxAppenderWrappers.add(new SyntaxAppenderWrapper(suffixOperator));
                return super.getTemplateText(tableName, entityClass, parameters, syntaxAppenderWrappers, conditionFieldWrapper);
            }
        };

        return appenderFactory;
    }

    /**
     * Batch name string.
     *
     * @return the string
     */
    @NotNull
    protected String batchName() {
        return "Batch";
    }

    /**
     * Gets new area name.
     *
     * @param areaName the area name
     * @return the new area name
     */
    @NotNull
    protected String getNewAreaName(String areaName) {
        return areaName + batchName();
    }

    /**
     * Gets suffix operator.
     *
     * @param mappingField the mapping field
     * @return the suffix operator
     */
    @NotNull
    protected SuffixOperator getSuffixOperator(List<TxField> mappingField) {
        return new InsertBatchSuffixOperator(mappingField);
    }

    @Override
    public StatementBlock getStatementBlock() {
        return statementBlock;
    }

    @Override
    public String operatorName() {
        return operatorName;
    }


    private class InsertBatchResultAppenderFactory extends ResultAppenderFactory {

        /**
         * Instantiates a new Insert batch result appender factory.
         *
         * @param areaPrefix the area prefix
         */
        public InsertBatchResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<TxParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            StringBuilder mapperXml = new StringBuilder("insert into " + tableName);
            for (SyntaxAppenderWrapper syntaxAppenderWrapper : collector) {
                String templateText = syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
                mapperXml.append(templateText);
            }
            return mapperXml.toString();
        }

        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {
            // 遍历定义的类型
            String defineName = Collection.class.getSimpleName() + "<" + entityClass.getName() + ">";
            // 变量名称
            String variableName = StringUtils.lowerCaseFirstChar(entityClass.getName()) + "Collection";

            List<String> importClass = new ArrayList<>();
            importClass.add("java.util.Collection");
            importClass.add(entityClass.getQualifiedName());
            TxParameter parameter = TxParameter.createByOrigin(variableName, defineName, Collection.class.getName(), true, importClass);
            return Collections.singletonList(parameter);
        }
    }


    /**
     * 批量插入
     */
    private class InsertBatchSuffixOperator implements SuffixOperator {

        private List<TxField> mappingField;

        /**
         * Instantiates a new Insert batch suffix operator.
         *
         * @param mappingField the mapping field
         */
        public InsertBatchSuffixOperator(List<TxField> mappingField) {
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
            StringBuilder stringBuilder = new StringBuilder();
            String itemName = "item";
            // 追加列名
            final String columns = mappingField.stream()
                    .map(field -> field.getColumnName())
                    .collect(Collectors.joining(",\n"));
            stringBuilder.append("(").append(columns).append(")").append("\n");
            // values 连接符
            stringBuilder.append("values").append("\n");
            final TxParameter collection = parameters.poll();
            final String collectionName = collection.getName();
            final String fields = mappingField.stream()
                    .map(field -> {
                        String fieldValue = JdbcTypeUtils.wrapperField(itemName + "." + field.getFieldName(), field.getFieldType());
                        fieldValue = conditionFieldWrapper.wrapDefaultDateIfNecessary(field.getColumnName(), fieldValue);
                        return fieldValue;
                    })
                    .collect(Collectors.joining(",\n"));

            stringBuilder.append("<foreach collection=\"").append(collectionName).append("\"");
            stringBuilder.append(" item=\"" + itemName + "\"");
            stringBuilder.append(" separator=\",\">").append("\n");
            stringBuilder.append("(").append(fields).append(")").append("\n");
            stringBuilder.append("</foreach>");

            return stringBuilder.toString();
        }

    }
}
