package com.mybatis.generatorSql.operate;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.mybatis.generator.AbstractStatementGenerator;
import com.mybatis.generatorSql.appender.CustomAreaAppender;
import com.mybatis.generatorSql.appender.CustomSuffixAppender;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.factory.ResultAppenderFactory;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.mapping.model.TxReturnDescriptor;
import com.mybatis.generatorSql.operate.manager.StatementBlock;
import com.mybatis.generatorSql.operate.suffix.SuffixOperator;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;
import com.mybatis.utils.JdbcTypeUtils;
import com.mybatis.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InsertOperator extends BaseOperatorManager {


    private static final Logger logger = LoggerFactory.getLogger(InsertOperator.class);


    /**
     * Instantiates a new Insert operator.
     *
     * @param mappingField the mapping field
     */
    public InsertOperator(final List<TxField> mappingField) {
        Set<String> patterns = AbstractStatementGenerator.INSERT_GENERATOR.getPatterns();
        this.init(mappingField, patterns);
    }

    /**
     * Init.
     *
     * @param mappingField the mapping field
     * @param patterns     the patterns
     */
    public void init(final List<TxField> mappingField, Set<String> patterns) {
        for (final String areaName : patterns) {
            // insertSelective
            this.initInsertSelectiveAppender(areaName, mappingField);
            // insertOne
            this.initInsertAllAppender(areaName, mappingField);
            // insertBatch
//            this.initInsertBatch(areaName, mappingField);

            this.initCustomArea(areaName, mappingField);
        }
    }

    private void initInsertSelectiveAppender(String areaName, List<TxField> mappingField) {
        String newAreaName = areaName + "Selective";
        final ResultAppenderFactory insertResultAppenderFactory = new InsertResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName,
                                          PsiClass entityClass,
                                          LinkedList<TxParameter> parameters,
                                          LinkedList<SyntaxAppenderWrapper> collector,
                                          ConditionFieldWrapper conditionFieldWrapper) {
                // 定制参数
                SyntaxAppender selective = InsertCustomSuffixAppender.createInsertBySuffixOperator("Selective",
                        new InsertSelectiveSuffixOperator(mappingField),
                        AreaSequence.RESULT);
                LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrappers = new LinkedList<>();
                syntaxAppenderWrappers.add(new SyntaxAppenderWrapper(selective));
                return super.getTemplateText(tableName, entityClass, parameters, syntaxAppenderWrappers, conditionFieldWrapper);
            }
        };

        // insert + Selective
        final SyntaxAppender selectiveAppender =
                CustomAreaAppender.createCustomAreaAppender(newAreaName, ResultAppenderFactory.RESULT, AreaSequence.AREA, AreaSequence.RESULT, insertResultAppenderFactory);
        insertResultAppenderFactory.registerAppender(selectiveAppender);

        StatementBlock statementBlock = new StatementBlock();
        statementBlock.setResultAppenderFactory(insertResultAppenderFactory);
        statementBlock.setTagName(newAreaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createByOrigin(null, "int"));
        this.registerStatementBlock(statementBlock);

        this.addOperatorName(newAreaName);
    }


    private void initInsertAllAppender(final String areaName, List<TxField> mappingField) {
        String newAreaName = areaName + "All";
        final ResultAppenderFactory insertResultAppenderFactory = new InsertResultAppenderFactory(newAreaName) {
            @Override
            public String getTemplateText(String tableName,
                                          PsiClass entityClass,
                                          LinkedList<TxParameter> parameters,
                                          LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
                // 定制参数
                SyntaxAppender insertAll = InsertCustomSuffixAppender.createInsertBySuffixOperator("All",
                        new InsertAllSuffixOperator(mappingField),
                        AreaSequence.RESULT);
                LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrappers = new LinkedList<>();
                syntaxAppenderWrappers.add(new SyntaxAppenderWrapper(insertAll));
                return super.getTemplateText(tableName, entityClass, parameters, syntaxAppenderWrappers, conditionFieldWrapper);
            }


        };
        // insert + All
        final SyntaxAppender allAppender =
                CustomAreaAppender.createCustomAreaAppender(newAreaName,
                        ResultAppenderFactory.RESULT,
                        AreaSequence.AREA,
                        AreaSequence.RESULT,
                        insertResultAppenderFactory);
        insertResultAppenderFactory.registerAppender(allAppender);

        StatementBlock statementBlock = new StatementBlock();
        statementBlock.setResultAppenderFactory(insertResultAppenderFactory);
        statementBlock.setTagName(newAreaName);
        statementBlock.setReturnWrapper(TxReturnDescriptor.createByOrigin(null, "int"));
        this.registerStatementBlock(statementBlock);

        this.addOperatorName(newAreaName);
    }

    @Override
    public String getTagName() {
        return "insert";
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
        mybatisXmlGenerator.generateInsert(id, mapperXml);
    }

    /**
     * The type Insert custom suffix appender.
     */
    static class InsertCustomSuffixAppender extends CustomSuffixAppender {

        /**
         * Instantiates a new Insert custom suffix appender.
         *
         * @param tipName        the tip name
         * @param suffixOperator the suffix operator
         * @param areaSequence   the area sequence
         */
        public InsertCustomSuffixAppender(String tipName, SuffixOperator suffixOperator, AreaSequence areaSequence) {
            super(tipName, suffixOperator, areaSequence);
        }

        /**
         * Create insert by suffix operator syntax appender.
         *
         * @param all            the all
         * @param suffixOperator the suffix operator
         * @param areaSequence   the area sequence
         * @return the syntax appender
         */
        public static SyntaxAppender createInsertBySuffixOperator(String all, SuffixOperator suffixOperator, AreaSequence areaSequence) {
            return new InsertCustomSuffixAppender(all, suffixOperator, areaSequence);
        }

        @Override
        public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
            syntaxAppenderWrapper.addWrapper(new SyntaxAppenderWrapper(this));
        }

    }

    private static class InsertResultAppenderFactory extends ResultAppenderFactory {

        /**
         * Instantiates a new Insert result appender factory.
         *
         * @param areaPrefix the area prefix
         */
        public InsertResultAppenderFactory(String areaPrefix) {
            super(areaPrefix);
        }

        @Override
        public String getTemplateText(String tableName,
                                      PsiClass entityClass,
                                      LinkedList<TxParameter> parameters,
                                      LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
            StringBuilder mapperXml = new StringBuilder();
            mapperXml.append("insert into ").append(tableName).append("\n");
            for (SyntaxAppenderWrapper syntaxAppenderWrapper : collector) {
                String templateText = syntaxAppenderWrapper.getAppender().getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
                mapperXml.append(templateText);
            }
            return mapperXml.toString();
        }

        @Override
        public List<TxParameter> getMxParameter(PsiClass entityClass, LinkedList<SyntaxAppenderWrapper> jpaStringList) {
            String defineName = entityClass.getName();
            String variableName = StringUtils.lowerCaseFirstChar(defineName);
            List<String> importClass = Collections.singletonList(entityClass.getQualifiedName());
            TxParameter parameter = TxParameter.createByOrigin(variableName,
                    defineName,
                    entityClass.getQualifiedName(),
                    false,
                    importClass);
            return Collections.singletonList(parameter);
        }

    }

    private static class InsertAllSuffixOperator implements SuffixOperator {

        private final List<TxField> mappingField;

        /**
         * Instantiates a new Insert all suffix operator.
         *
         * @param mappingField the mapping field
         */
        public InsertAllSuffixOperator(List<TxField> mappingField) {
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
            TxParameter parameter = parameters.poll();
            StringBuilder stringBuilder = new StringBuilder();
            // 追加列名
            final String columns = mappingField.stream()
                    .map(TxField::getColumnName)
                    .collect(Collectors.joining(",\n"));
            stringBuilder.append("(").append("\n").append(columns).append("\n").append(")").append("\n");
            // values 连接符
            stringBuilder.append("values").append("\n");
            final String fields = mappingField.stream()
                    .map(field -> {
                        String fieldValue = JdbcTypeUtils.wrapperField(field.getFieldName(), field.getFieldType());
                        return conditionFieldWrapper.wrapDefaultDateIfNecessary(field.getColumnName(), fieldValue);
                    }).collect(Collectors.joining(",\n"));
            stringBuilder.append("(\n");
            stringBuilder.append(fields).append("\n");
            stringBuilder.append(")").append("\n");

            return stringBuilder.toString();
        }

    }

    private static class InsertSelectiveSuffixOperator implements SuffixOperator {

        private final List<TxField> mappingField;

        /**
         * Instantiates a new Insert selective suffix operator.
         *
         * @param mappingField the mapping field
         */
        public InsertSelectiveSuffixOperator(List<TxField> mappingField) {
            this.mappingField = mappingField;
        }

        @Override
        public String getTemplateText(String fieldName, LinkedList<TxParameter> parameters, ConditionFieldWrapper conditionFieldWrapper) {
            StringBuilder stringBuilder = new StringBuilder();
            // 追加列名
            final String columns = mappingField.stream()
                    .map(field -> selective(field.getFieldName(), field.getColumnName()))
                    .collect(Collectors.joining("\n"));

            stringBuilder.append(trimFieldStart()).append(columns).append(trimEnd()).append("\n");
            // values 连接符
            stringBuilder.append("values").append("\n");
            final String fields = mappingField.stream()
                    .map(field -> {
                        String fieldValue = JdbcTypeUtils.wrapperField(field.getFieldName(), field.getFieldType());
                        fieldValue = conditionFieldWrapper.wrapDefaultDateIfNecessary(field.getColumnName(), fieldValue);
                        return selective(field.getFieldName(), fieldValue);
                    })
                    .collect(Collectors.joining("\n"));

            stringBuilder.append(trimFieldStart());
            stringBuilder.append(fields).append("\n");
            stringBuilder.append(trimEnd());
            return stringBuilder.toString();
        }

        private String trimEnd() {
            return "</trim>";
        }

        private String trimFieldStart() {
            return "<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">";
        }

        private String selective(String paramName, String origin) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<if test=\"").append(paramName).append(" != null").append("\">").append("\n");
            stringBuilder.append(origin);
            stringBuilder.append(",").append("\n");
            stringBuilder.append("</if>").append("\n");
            return stringBuilder.toString();
        }


    }
}
