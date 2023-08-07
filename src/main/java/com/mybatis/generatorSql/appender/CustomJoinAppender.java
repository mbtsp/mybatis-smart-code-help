package com.mybatis.generatorSql.appender;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.command.AppendTypeCommand;
import com.mybatis.generatorSql.command.JoinAppendTypeCommand;
import com.mybatis.generatorSql.enums.AppendTypeEnum;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CustomJoinAppender implements SyntaxAppender {

    /**
     * The constant SPACE.
     */
    public static final String SPACE = " ";
    private static final Logger logger = LoggerFactory.getLogger(CustomJoinAppender.class);
    private final String tipText;
    private final String sqlText;
    private final AreaSequence areaSequence;

    /**
     * Instantiates a new Custom join appender.
     *
     * @param tipText      the tip text
     * @param sqlText      the sql text
     * @param areaSequence the area sequence
     */
    public CustomJoinAppender(String tipText, String sqlText, AreaSequence areaSequence) {
        this.tipText = tipText;
        this.sqlText = sqlText;
        this.areaSequence = areaSequence;
    }

    @Override
    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    @Override
    public String getText() {
        return this.tipText;
    }

    @Override
    public AppendTypeEnum getType() {
        return AppendTypeEnum.JOIN;
    }

    @Override
    public List<AppendTypeCommand> getCommand(String areaPrefix, List<SyntaxAppender> splitList) {
        return Arrays.asList(new JoinAppendTypeCommand(this));
    }

    @Override
    public Optional<SyntaxAppender> pollLast(LinkedList<SyntaxAppender> splitList) {
        final Optional<SyntaxAppender> syntaxAppender = Optional.of(splitList.pollLast());
        if (syntaxAppender.isPresent() && splitList.size() > 0) {
            final SyntaxAppender last = splitList.getLast();
            return last.pollLast(splitList);
        }
        return syntaxAppender;
    }

    @Override
    public boolean getCandidateAppender(LinkedList<SyntaxAppender> result) {
        if (result.isEmpty()) {
            result.add(this);
            return true;
        }
        return false;
    }

    @Override
    public String getTemplateText(String tableName, PsiClass entityClass, LinkedList<TxParameter> parameters, LinkedList<SyntaxAppenderWrapper> collector, ConditionFieldWrapper conditionFieldWrapper) {
        return sqlText + SPACE;
    }

    @Override
    public List<TxParameter> getMxParameter(LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrapperLinkedList, PsiClass entityClass) {
        return Collections.emptyList();
    }

    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        syntaxAppenderWrapper.addWrapper(new SyntaxAppenderWrapper(this));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
                .append("tipText", tipText)
                .append("sqlText", sqlText)
                .append("areaSequence", areaSequence)
                .toString();
    }

    @Override
    public boolean checkAfter(SyntaxAppender secondAppender, AreaSequence areaSequence) {
        boolean sequenceCheck = getAreaSequence().getSequence() == secondAppender.getAreaSequence().getSequence();
        return sequenceCheck;
    }
}
