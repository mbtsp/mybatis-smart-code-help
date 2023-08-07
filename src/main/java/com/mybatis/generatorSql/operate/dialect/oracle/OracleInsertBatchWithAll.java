package com.mybatis.generatorSql.operate.dialect.oracle;

import com.intellij.database.model.DasTableKey;
import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.db.adaptor.DasTableAdaptor;
import com.mybatis.generatorSql.exception.GenerateException;
import com.mybatis.generatorSql.factory.ResultAppenderFactory;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.operate.dialect.mysql.MysqlInsertBatch;
import com.mybatis.generatorSql.operate.suffix.SuffixOperator;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;
import com.mybatis.utils.JdbcTypeUtils;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class OracleInsertBatchWithAll extends MysqlInsertBatch {


    private DasTableAdaptor dasTable;
    private String tableName;


    /**
     * Instantiates a new Oracle insert batch with all.
     *
     * @param dasTable  the das table
     * @param tableName the table name
     */
    public OracleInsertBatchWithAll(DasTableAdaptor dasTable, String tableName) {
        this.dasTable = dasTable;
        this.tableName = tableName;

    }

    @Override
    protected @NotNull
    String batchName() {
        return "BatchWithAll";
    }

    @Override
    @NotNull
    protected SuffixOperator getSuffixOperator(List<TxField> mappingField) {
        return new InsertBatchSuffixOperator(tableName, mappingField);
    }


    @Override
    protected ResultAppenderFactory getResultAppenderFactory(List<TxField> mappingField, String newAreaName) {
        ResultAppenderFactory appenderFactory = new InsertBatchResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<TxParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
                // 定制参数
                SyntaxAppender suffixOperator = InsertCustomSuffixAppender.createInsertBySuffixOperator("BatchWithAll",
                        getSuffixOperator(mappingField),
                        AreaSequence.RESULT);
                LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrappers = new LinkedList<>();
                syntaxAppenderWrappers.add(new SyntaxAppenderWrapper(suffixOperator));
                return super.getTemplateText(tableName, entityClass, parameters, syntaxAppenderWrappers, conditionFieldWrapper);
            }
        };
        return appenderFactory;
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
                                      LinkedList<SyntaxAppenderWrapper> collector,
                                      ConditionFieldWrapper conditionFieldWrapper) {
            StringBuilder mapperXml = new StringBuilder("insert all ");
            for (SyntaxAppenderWrapper syntaxAppenderWrapper : collector) {
                String templateText = syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
                mapperXml.append(templateText);
            }
            mapperXml.append("select 1 from dual");
            return mapperXml.toString();
        }

        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {
            // 遍历定义的类型
            String defineName = Collection.class.getSimpleName() + "<" + entityClass.getName() + ">";
            // 变量名称
            String variableName = StringUtils.lowerCaseFirstChar(entityClass.getName()) + "Collection";

            List<String> importClass = new ArrayList<>();
            importClass.add(Collection.class.getName());
            importClass.add(entityClass.getQualifiedName());
            TxParameter parameter = TxParameter.createByOrigin(variableName,
                    defineName,
                    Collection.class.getName(),
                    true,
                    importClass);
            return Collections.singletonList(parameter);
        }
    }


    /**
     * 批量插入
     */
    private class InsertBatchSuffixOperator implements SuffixOperator {
        private final String tableName;
        @NotNull
        private final List<TxField> mappingField;

        /**
         * Instantiates a new Insert batch suffix operator.
         *
         * @param tableName    the table name
         * @param mappingField the mapping field
         */
        public InsertBatchSuffixOperator(String tableName, List<TxField> mappingField) {
            this.tableName = tableName;
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
            Optional<String> sequenceName = dasTable.findSequenceName(tableName);

            StringBuilder stringBuilder = new StringBuilder();
            String itemName = "item";

            // 追加列名
            final String columns = mappingField.stream()
                    .map(TxField::getColumnName)
                    .collect(Collectors.joining(",\n"));

            final TxParameter collection = parameters.poll();
            if (collection == null) {
                throw new GenerateException("oracle insertBatch 生成失败, 无法获取集合名称");
            }

            final String collectionName = collection.getName();
            final String fields = mappingField.stream()
                    .map(field -> {
                        String fieldStr = JdbcTypeUtils.wrapperField(itemName + "." + field.getFieldName(), field.getFieldType());
                        // 第一版写死字段变更, 后续重构
                        // 变更主键生成规则为自定义函数
                        if (sequenceName.isPresent() && dasTable != null) {
                            DasTableKey primaryKey = dasTable.getPrimaryKey();
                            // 当前字段是主键, 使用自定义函数替换主键
                            if (primaryKey != null && primaryKey.getColumnsRef().size() == 1) {
                                String pkFieldName = primaryKey.getColumnsRef().iterate().next();
                                if (pkFieldName.equals(field.getColumnName())) {
                                    fieldStr = "GET_SEQ_NO('" + sequenceName.get() + "')";
                                }
                            }
                        }
                        fieldStr = conditionFieldWrapper.wrapDefaultDateIfNecessary(field.getColumnName(), fieldStr);
                        return fieldStr;
                    })
                    .collect(Collectors.joining(",\n"));

            stringBuilder.append("<foreach collection=\"").append(collectionName).append("\"");
            stringBuilder.append(" item=\"").append(itemName).append("\"").append(">").append("\n");
            stringBuilder.append("INTO ").append(tableName).append("\n");
            stringBuilder.append("(").append(columns).append(")").append("\n");
            stringBuilder.append("VALUES").append("\n");
            // values 连接符
            stringBuilder.append("(").append("\n");
            stringBuilder.append(fields).append("\n").append(")").append("\n");
            stringBuilder.append("</foreach>").append("\n");

            return stringBuilder.toString();
        }

    }


}
