package com.mybatis.generatorSql.appender;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.command.AppendTypeCommand;
import com.mybatis.generatorSql.command.FieldSuffixAppendTypeService;
import com.mybatis.generatorSql.enums.AppendTypeEnum;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.operate.suffix.FixedSuffixOperator;
import com.mybatis.generatorSql.operate.suffix.ParamAroundSuffixOperator;
import com.mybatis.generatorSql.operate.suffix.ParamBeforeSuffixOperator;
import com.mybatis.generatorSql.operate.suffix.SuffixOperator;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CustomSuffixAppender implements SyntaxAppender {

    private static final Logger logger = LoggerFactory.getLogger(CustomSuffixAppender.class);
    private final String tipName;
    /**
     * The Suffix operator.
     */
    protected SuffixOperator suffixOperator;
    private MxParameterChanger mxParameterFinder;
    private AreaSequence areaSequence;


    /**
     * Instantiates a new Custom suffix appender.
     *
     * @param tipName the tip name
     */
    protected CustomSuffixAppender(String tipName) {
        this.tipName = tipName;
    }

    /**
     * Instantiates a new Custom suffix appender.
     *
     * @param tipName        the tip name
     * @param suffixOperator the suffix operator
     * @param areaSequence   the area sequence
     */
    protected CustomSuffixAppender(String tipName, SuffixOperator suffixOperator, AreaSequence areaSequence) {
        this.tipName = tipName;
        this.suffixOperator = suffixOperator;
        this.areaSequence = areaSequence;
    }

    /**
     * Create by param join custom suffix appender.
     *
     * @param tipName         the tip name
     * @param compareOperator the compare operator
     * @param areaSequence    the area sequence
     * @return the custom suffix appender
     */
    public static CustomSuffixAppender createByParamJoin(String tipName, String compareOperator, AreaSequence areaSequence) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName);
        customSuffixAppender.suffixOperator = new ParamBeforeSuffixOperator(compareOperator);
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }

    /**
     * Create by param around custom suffix appender.
     *
     * @param tipName      the tip name
     * @param prefix       the prefix
     * @param suffix       the suffix
     * @param areaSequence the area sequence
     * @return the custom suffix appender
     */
    public static CustomSuffixAppender createByParamAround(String tipName, String prefix, String suffix, AreaSequence areaSequence) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName);
        customSuffixAppender.suffixOperator = new ParamAroundSuffixOperator(prefix, suffix);
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }

    /**
     * Create by suffix operator custom suffix appender.
     *
     * @param tipName        the tip name
     * @param suffixOperator the suffix operator
     * @param areaSequence   the area sequence
     * @return the custom suffix appender
     */
    public static CustomSuffixAppender createBySuffixOperator(String tipName, SuffixOperator suffixOperator, AreaSequence areaSequence) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName);
        customSuffixAppender.suffixOperator = suffixOperator;
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }

    /**
     * Create by parameter changer custom suffix appender.
     *
     * @param tipName           the tip name
     * @param mxParameterFinder the mx parameter finder
     * @param areaSequence      the area sequence
     * @return the custom suffix appender
     */
    public static CustomSuffixAppender createByParameterChanger(String tipName, MxParameterChanger mxParameterFinder, AreaSequence areaSequence) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName);
        customSuffixAppender.mxParameterFinder = mxParameterFinder;
        customSuffixAppender.suffixOperator = mxParameterFinder;
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }

    /**
     * 根据固定后缀
     *
     * @param tipName      the tip name
     * @param suffix       the suffix
     * @param areaSequence the area sequence
     * @param mappingField
     * @return syntax appender
     */
    public static SyntaxAppender createByFixed(String tipName, String suffix, AreaSequence areaSequence, List<TxField> mappingField) {
        CustomSuffixAppender customSuffixAppender = new CustomSuffixAppender(tipName) {
            @Override
            public List<TxParameter> getMxParameter(LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrapperLinkedList, PsiClass entityClass) {
                return Collections.emptyList();
            }

        };
        customSuffixAppender.suffixOperator = new FixedSuffixOperator(suffix, mappingField);
        customSuffixAppender.areaSequence = areaSequence;
        return customSuffixAppender;
    }

    @Override
    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    @Override
    public String getText() {
        return this.tipName;
    }

    @Override
    public AppendTypeEnum getType() {
        return AppendTypeEnum.SUFFIX;
    }

    @Override
    public List<AppendTypeCommand> getCommand(String areaPrefix, List<SyntaxAppender> splitList) {
        return Collections.singletonList(new FieldSuffixAppendTypeService(this));
    }

    /**
     * 后缀后面一定不能添加任何东西了
     *
     * @param result
     * @return
     */
    @Override
    public boolean getCandidateAppender(LinkedList<SyntaxAppender> result) {
        SyntaxAppender lastAppender = result.peekLast();
        if (!result.isEmpty() &&
                lastAppender != null) {
            // && lastAppender.getType() == AppendTypeEnum.FIELD  在insert 的时候, 后缀不需要字段
            result.add(this);
            return true;
        }
        return false;
    }

    @Override
    public String getTemplateText(String tableName,
                                  PsiClass entityClass,
                                  LinkedList<TxParameter> parameters,
                                  LinkedList<SyntaxAppenderWrapper> collector,
                                  ConditionFieldWrapper conditionFieldWrapper) {
        if (collector.size() == 0) {
            logger.info("这个后缀没有参数, suffix: {}", this.getText());
        }

        StringBuilder stringBuilder = new StringBuilder();
        String fieldName = null;
        String columnName = null;
        int i = 0;
        for (SyntaxAppenderWrapper syntaxAppenderWrapper : collector) {

            SyntaxAppender appender = syntaxAppenderWrapper.getAppender();
            // 兼容insert 语句生成,后缀没有字段的情况
            if (appender instanceof CustomFieldAppender) {
                CustomFieldAppender field = (CustomFieldAppender) appender;
                fieldName = field.getFieldName();
                columnName = field.getColumnName();
                break;
            }
            if (appender instanceof CustomJoinAppender) {
                String joinTemplateText =
                        getFieldTemplateText(tableName,
                                entityClass,
                                parameters,
                                collector,
                                conditionFieldWrapper,
                                appender);
                if (i > 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(joinTemplateText);
                i++;
            }
        }
        String suffixTemplateText = suffixOperator.
                getTemplateText(columnName, parameters, conditionFieldWrapper);
        stringBuilder.append(suffixTemplateText);

        return conditionFieldWrapper.wrapConditionText(fieldName, stringBuilder.toString());
    }

    protected String getFieldTemplateText(String tableName,
                                          PsiClass entityClass,
                                          LinkedList<TxParameter> parameters,
                                          LinkedList<SyntaxAppenderWrapper> collector,
                                          ConditionFieldWrapper conditionFieldWrapper,
                                          SyntaxAppender appender) {
        return appender.getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
    }

    /**
     * 对于前一个追加器是字段类型的,  就把字段弹出来, 加到自己里面
     *
     * @param jpaStringList
     * @param syntaxAppenderWrapper
     */
    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        LinkedList<SyntaxAppenderWrapper> collector = new LinkedList<>();

        addField(syntaxAppenderWrapper, collector);
        addJoin(syntaxAppenderWrapper, collector);

        syntaxAppenderWrapper.addWrapper(new SyntaxAppenderWrapper(this, collector));
    }

    private void addJoin(SyntaxAppenderWrapper syntaxAppenderWrapper, LinkedList<SyntaxAppenderWrapper> collector) {
        SyntaxAppenderWrapper peek = syntaxAppenderWrapper.getCollector().peekLast();
        if (peek == null) {
            return;
        }
        if (peek.getAppender().getType() == AppendTypeEnum.JOIN) {
            SyntaxAppenderWrapper lastField = syntaxAppenderWrapper.getCollector().pollLast();
            collector.addFirst(lastField);
        }
    }

    private void addField(SyntaxAppenderWrapper syntaxAppenderWrapper, LinkedList<SyntaxAppenderWrapper> collector) {
        SyntaxAppenderWrapper peek = syntaxAppenderWrapper.getCollector().peekLast();
        assert peek != null;
        if (peek.getAppender().getType() == AppendTypeEnum.FIELD) {
            SyntaxAppenderWrapper lastField = syntaxAppenderWrapper.getCollector().pollLast();
            collector.add(lastField);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("tipName", tipName)
                .append("suffixOperator", suffixOperator)
                .append("mxParameterFinder", mxParameterFinder)
                .append("areaSequence", areaSequence)
                .toString();
    }

    @Override
    public boolean checkAfter(SyntaxAppender secondAppender, AreaSequence areaSequence) {
        boolean hasAreaCheck = secondAppender.getAreaSequence() == AreaSequence.AREA;
        boolean typeCheck = getType().checkAfter(secondAppender.getType());
        boolean sequenceCheck = getAreaSequence().getSequence() == secondAppender.getAreaSequence().getSequence();
        return hasAreaCheck || (typeCheck && sequenceCheck);
    }

    @Override
    public List<TxParameter> getMxParameter(LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrapperLinkedList,
                                            PsiClass entityClass) {
        List<TxParameter> txParameters = new ArrayList<>();
        for (SyntaxAppenderWrapper syntaxAppenderWrapper : syntaxAppenderWrapperLinkedList) {
            List<TxParameter> mxParameter = syntaxAppenderWrapper.getMxParameter(entityClass);
            if (mxParameter.size() > 0) {
                for (TxParameter txParameter : mxParameter) {
                    txParameters.addAll(getParameter(txParameter));
                }
            }
        }
        return txParameters;
    }

    public List<TxParameter> getParameter(TxParameter txParameter) {
        if (mxParameterFinder == null) {
            return Collections.singletonList(txParameter);
        }
        return mxParameterFinder.getParameter(txParameter);
    }


    /**
     * Gets suffix operator.
     *
     * @return the suffix operator
     */
    public SuffixOperator getSuffixOperator() {
        return suffixOperator;
    }
}
