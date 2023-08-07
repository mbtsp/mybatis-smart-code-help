package com.mybatis.generatorSql.factory;

import com.intellij.psi.PsiClass;
import com.mybatis.generatorSql.SyntaxAppenderFactory;
import com.mybatis.generatorSql.appender.CompositeAppender;
import com.mybatis.generatorSql.appender.CustomAreaAppender;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.command.AppendTypeCommand;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseAppenderFactory implements SyntaxAppenderFactory {


    @Override
    public String getFactoryTemplateText(LinkedList<SyntaxAppender> jpaStringList,
                                         PsiClass entityClass,
                                         LinkedList<TxParameter> parameters,
                                         String tableName,
                                         ConditionFieldWrapper conditionFieldWrapper) {
        if (jpaStringList.isEmpty()) {
            return "";
        }
        // 按照区域维度转换成一棵树
        CompositeAppender compositeAppender = new CompositeAppender();
        SyntaxAppenderWrapper rootSyntaxWrapper = new SyntaxAppenderWrapper(null);
        compositeAppender.toTree(jpaStringList, rootSyntaxWrapper);

        // 加入 mybatis-xml 的 if-test 支持
        // 遍历区域, 生成字符串
        return "\n" + rootSyntaxWrapper.getCollector().stream().map(syntaxAppenderWrapper -> {
            LinkedList<SyntaxAppenderWrapper> collector = syntaxAppenderWrapper.getCollector();
            return syntaxAppenderWrapper.getAppender().getTemplateText(tableName,
                    entityClass,
                    parameters,
                    collector,
                    conditionFieldWrapper
            );
        }).filter(StringUtils::isNotBlank).collect(Collectors.joining("\n"));
    }


    /**
     * 在字段后面追加一波符号追加器
     * <p>
     * 例如 and + field
     * 实际上是 and +field + equals
     * 默认追加 equals
     *
     * @param syntaxAppender the syntax appender
     * @param current        the current
     */
    protected void appendDefault(SyntaxAppender syntaxAppender, LinkedList<SyntaxAppender> current) {

    }

    private SyntaxAppender getLastAppendType(List<SyntaxAppender> splitList) {
        if (splitList.size() == 0) {
            return SyntaxAppender.EMPTY;
        }
        return splitList.get(splitList.size() - 1);
    }

    /**
     * @param syntaxAppender
     * @param splitList      之前所有有效的 符号追加列表
     * @return
     */
    @Override
    public Optional<String> mappingAppend(SyntaxAppender syntaxAppender, List<SyntaxAppender> splitList) {

        List<SyntaxAppender> appendTypes = new ArrayList<>();
        List<AppendTypeCommand> list = syntaxAppender.getCommand(this.getTipText(), splitList);

        for (AppendTypeCommand appendTypeCommand : list) {
            Optional<SyntaxAppender> syntaxAppenderOptional = appendTypeCommand.execute();
            if (syntaxAppenderOptional.isPresent()) {
                appendTypes.add(syntaxAppenderOptional.get());
            }
        }


        // 无效的就返回 null
        if (!isValid(splitList, appendTypes)) {
            return Optional.empty();
        }
        String resultStr = appendTypes.stream().map(x -> x.getText()).collect(Collectors.joining());
        return Optional.of(resultStr);
    }

    /**
     * Is valid boolean.
     *
     * @param splitList   前面已经存在数据
     * @param appendTypes 当前可选的数据
     * @return boolean
     */
    protected boolean isValid(List<SyntaxAppender> splitList, List<SyntaxAppender> appendTypes) {
        if (appendTypes.size() == 0) {
            return false;
        }
        SyntaxAppender lastAppender = getLastAppendType(splitList);
        // 检查所有标签的区域不能超过2次
        Set<String> syntaxAppenders = new HashSet<>();
        // 检查之前的标签
        if (!checkSameArea(splitList, syntaxAppenders)) {
            return false;
        }
        // 检查当前标签
        if (!checkSameArea(appendTypes, syntaxAppenders)) {
            return false;
        }
        AreaSequence areaSequence = getAreaSequence();
        // 检查当前标签允许的方式
        for (SyntaxAppender currentAppender : appendTypes) {
            if (!lastAppender.checkAfter(currentAppender, areaSequence)) {
                return false;
            }
            lastAppender = currentAppender;
            areaSequence = currentAppender.getAreaSequence();
        }
        return true;
    }

    private boolean checkSameArea(List<SyntaxAppender> splitList, Set<String> syntaxAppenders) {
        for (SyntaxAppender syntaxAppender : splitList) {
            if (!syntaxAppender.checkDuplicate(syntaxAppenders)) {
                return false;
            }
        }
        return true;
    }


    /**
     * @param priorityQueue
     * @param existsSyntaxAppenderList 已经存在的前缀
     * @param splitStr
     */
    @Override
    public void findPriority(final PriorityQueue<SyntaxAppender> priorityQueue, LinkedList<SyntaxAppender> existsSyntaxAppenderList, final String splitStr) {
        SyntaxAppender lastSyntaxAppender = existsSyntaxAppenderList.peekLast();

        // 把自己内部的符号加入候选
        for (final SyntaxAppender syntaxAppender : getSyntaxAppenderList()) {
            if (lastSyntaxAppender == null
                    || lastSyntaxAppender.checkAfter(syntaxAppender, getAreaSequence())) {
                syntaxAppender.findPriority(priorityQueue, splitStr);
            }
        }
        // 把自己加入候选
        final String factorySyntaxPrefix = getTipText();
        if (StringUtils.isNotBlank(factorySyntaxPrefix) && splitStr.startsWith(factorySyntaxPrefix)) {
            CustomAreaAppender customAreaAppender = CustomAreaAppender.createCustomAreaAppender(factorySyntaxPrefix,
                    getTipText(),
                    AreaSequence.AREA,
                    getAreaSequence(),
                    this);
            priorityQueue.add(customAreaAppender);
        }
    }

    /**
     * Gets area sequence.
     *
     * @return the area sequence
     */
    protected abstract AreaSequence getAreaSequence();


}
