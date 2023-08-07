package com.mybatis.generatorSql.appender;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.mybatis.generatorSql.command.AppendTypeCommand;
import com.mybatis.generatorSql.command.FieldAppendTypeCommand;
import com.mybatis.generatorSql.enums.AppendTypeEnum;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;
import com.mybatis.utils.FieldUtil;
import com.mybatis.utils.JdbcTypeUtils;
import com.mybatis.utils.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomFieldAppender implements SyntaxAppender {

    private static final Logger logger = LoggerFactory.getLogger(CustomFieldAppender.class);
    /**
     * The Tip name.
     */
    protected String tipName;
    /**
     * The Field name.
     */
    protected String fieldName;
    /**
     * The Column name.
     */
    protected String columnName;
    private AreaSequence areaSequence;

    /**
     * Instantiates a new Custom field appender.
     *
     * @param field        the field
     * @param areaSequence the area sequence
     */
    public CustomFieldAppender(TxField field, AreaSequence areaSequence) {
        this.fieldName = field.getFieldName();
        this.tipName = field.getTipName();
        this.columnName = field.getColumnName();
        this.areaSequence = areaSequence;
    }

    @Override
    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    /**
     * Sets area sequence.
     *
     * @param areaSequence the area sequence
     */
    public void setAreaSequence(AreaSequence areaSequence) {
        this.areaSequence = areaSequence;
    }

    /**
     * Gets field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return this.fieldName;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public String getText() {
        return this.tipName;
    }

    @Override
    public AppendTypeEnum getType() {
        return AppendTypeEnum.FIELD;
    }

    @Override
    public List<AppendTypeCommand> getCommand(String areaPrefix, List<SyntaxAppender> splitList) {
        return Collections.singletonList(new FieldAppendTypeCommand(this));
    }

    @Override
    public boolean getCandidateAppender(LinkedList<SyntaxAppender> result) {
        SyntaxAppender lastAppender = result.peekLast();
        if (result.isEmpty() || lastAppender != null
                && lastAppender.getType() == AppendTypeEnum.JOIN) {
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
        String defaultDateValue = wrapFieldValueInTemplateText(columnName, conditionFieldWrapper, null);
        TxParameter parameter = null;
        if (StringUtils.isEmpty(defaultDateValue)) {
            parameter = parameters.poll();
        }
        String fieldValue = defaultDateValue;
        if (parameter != null) {
            fieldValue = JdbcTypeUtils.wrapperField(parameter.getName(), parameter.getCanonicalTypeText());
        }
        return columnName + " = " + wrapFieldValueInTemplateText(columnName, conditionFieldWrapper, fieldValue);
    }

    protected String wrapFieldValueInTemplateText(String columnName, ConditionFieldWrapper conditionFieldWrapper, String fieldValue) {
        return fieldValue;
    }


    @Override
    public List<TxParameter> getMxParameter(LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrapperLinkedList, PsiClass entityClass) {
        Map<String, PsiField> fieldMap = FieldUtil.getStringPsiFieldMap(entityClass);
        String text = this.getText();
        text = StringUtils.lowerCaseFirstChar(text);
        PsiField psiField = fieldMap.get(text);
        if (psiField == null) {
            logger.info("查找映射字段失败, text: {}", text);
            return Collections.emptyList();
        }
        return Collections.singletonList(TxParameter.createByPsiField(psiField, areaSequence));
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("tipName", tipName)
                .append("fieldName", fieldName)
                .append("columnName", columnName)
                .append("areaSequence", areaSequence)
                .toString();
    }

    /**
     * 啥也做不了,  只能把自己加到树里面
     *
     * @param jpaStringList
     * @param syntaxAppenderWrapper
     */
    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        syntaxAppenderWrapper.getCollector().add(new SyntaxAppenderWrapper(this));
    }

    @Override
    public boolean checkAfter(SyntaxAppender secondAppender, AreaSequence areaSequence) {
        boolean hasAreaCheck = secondAppender.getAreaSequence() == AreaSequence.AREA;
        boolean typeCheck = getType().checkAfter(secondAppender.getType());
        boolean fieldAreaCheck = getAreaSequence() == areaSequence;
        return hasAreaCheck || (typeCheck && fieldAreaCheck);
    }
}
