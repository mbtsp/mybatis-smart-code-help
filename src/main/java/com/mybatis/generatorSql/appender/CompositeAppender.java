package com.mybatis.generatorSql.appender;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.TemplateResolver;
import com.mybatis.generatorSql.command.AppendTypeCommand;
import com.mybatis.generatorSql.enums.AppendTypeEnum;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CompositeAppender implements SyntaxAppender {

    private static final Logger logger = LoggerFactory.getLogger(CompositeAppender.class);
    /**
     * The Appender list.
     */
    protected final LinkedList<SyntaxAppender> appenderList;
    /**
     * The Template resolver.
     */
    TemplateResolver templateResolver = new TemplateResolver();

    /**
     * Instantiates a new Composite appender.
     *
     * @param appenderList the appender list
     */
    public CompositeAppender(final SyntaxAppender... appenderList) {
        this.appenderList = new LinkedList<>(Arrays.asList(appenderList));
    }

    @Override
    public AreaSequence getAreaSequence() {
        return appenderList.peek().getAreaSequence();
    }

    @Override
    public String getText() {
        return this.appenderList.stream().map(SyntaxAppender::getText).collect(Collectors.joining());
    }

    /**
     * 返回最后一个的类型
     *
     * @return
     */
    @Override
    public AppendTypeEnum getType() {
        return this.appenderList.getFirst().getType();
    }

    @Override
    public boolean checkAfter(final SyntaxAppender secondAppender, AreaSequence areaSequence) {
        return this.appenderList.getLast().getType().checkAfter(secondAppender.getType());
    }

    @Override
    public List<AppendTypeCommand> getCommand(final String areaPrefix, final List<SyntaxAppender> splitList) {
        final List<AppendTypeCommand> appendTypeCommands = new ArrayList<>();
        for (final SyntaxAppender appender : this.appenderList) {
            appendTypeCommands.addAll(appender.getCommand(areaPrefix, splitList));
        }
        return appendTypeCommands;
    }

    @Override
    public boolean checkDuplicate(Set<String> syntaxAppenders) {
        for (SyntaxAppender appender : appenderList) {
            if (!appender.checkDuplicate(syntaxAppenders)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void findPriority(PriorityQueue<SyntaxAppender> priorityQueue, String splitStr) {
        for (SyntaxAppender appender : appenderList) {
            appender.findPriority(priorityQueue, splitStr);
        }
    }

    @Override
    public boolean getCandidateAppender(LinkedList<SyntaxAppender> result) {
        boolean flag = false;
        for (SyntaxAppender appender : appenderList) {
            if (!appender.getCandidateAppender(result)) {
                return false;
            }
        }
        return flag;
    }

    @Override
    public String getTemplateText(String tableName,
                                  PsiClass entityClass,
                                  LinkedList<TxParameter> parameters,
                                  LinkedList<SyntaxAppenderWrapper> collector,
                                  ConditionFieldWrapper conditionFieldWrapper) {

        // 第一个是 and, or
        if (appenderList.peek().getType() == AppendTypeEnum.JOIN
                || appenderList.peek().getType() == AppendTypeEnum.AREA) {
            SyntaxAppender lastAppender = appenderList.poll();
            // 先执行 连接符的获取模板文本, 然后把后续的内容拼接起来
            String joinTemplateText = lastAppender.getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
            return joinTemplateText + templateResolver.getTemplateText(appenderList, tableName, entityClass, parameters, collector, conditionFieldWrapper);
        }
        // 最后一个是后缀
        if (appenderList.peekLast().getType() == AppendTypeEnum.SUFFIX) {
            SyntaxAppender lastAppender = appenderList.pollLast();
            // 由后缀去处理前面所有的内容
            return lastAppender.getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);

        }
        logger.info("组合字段操作: {}", appenderList.size());
        StringBuilder stringBuilder = new StringBuilder();
        for (SyntaxAppender appender : appenderList) {
            String templateText = appender.getTemplateText(tableName, entityClass, parameters, collector, conditionFieldWrapper);
            stringBuilder.append(templateText);
        }

        return stringBuilder.toString();
    }

    @Override
    public List<TxParameter> getMxParameter(LinkedList<SyntaxAppenderWrapper> syntaxAppenderWrapperLinkedList, PsiClass entityClass) {
        return appenderList.stream().flatMap(appender -> appender.getMxParameter(syntaxAppenderWrapperLinkedList, entityClass).stream()).collect(Collectors.toList());
    }

    @Override
    public void toTree(LinkedList<SyntaxAppender> jpaStringList, SyntaxAppenderWrapper syntaxAppenderWrapper) {
        SyntaxAppender syntaxAppender;
        while ((syntaxAppender = jpaStringList.poll()) != null) {
            SyntaxAppenderWrapper syntaxAppenderWrapperItem = new SyntaxAppenderWrapper(syntaxAppender);
            syntaxAppender.toTree(jpaStringList, syntaxAppenderWrapperItem);
            syntaxAppenderWrapper.addWrapper(syntaxAppenderWrapperItem);
        }
    }

    @Override
    public String toString() {
        return "CompositeAppender{" +
                "appenders=" + appenderList +
                ", templateResolver=" + templateResolver +
                '}';
    }
}
