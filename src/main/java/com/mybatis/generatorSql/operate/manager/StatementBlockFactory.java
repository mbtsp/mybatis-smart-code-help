package com.mybatis.generatorSql.operate.manager;

import com.mybatis.generatorSql.SyntaxAppenderFactory;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 符号追加器 工厂管理器
 */
public class StatementBlockFactory {

    private static final Logger logger = LoggerFactory.getLogger(StatementBlockFactory.class);
    private final List<StatementBlock> blockList = new ArrayList<>();
    private Map<String, StatementBlock> appenderFactoryMap = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Statement block factory.
     */
    public StatementBlockFactory() {
    }

    /**
     * Split appender by text linked list.
     *
     * @param splitParam the split param
     * @return the linked list
     */
    @NotNull
    public LinkedList<SyntaxAppender> splitAppenderByText(String splitParam) {
        SyntaxSplitHelper syntaxSplitHelper = new SyntaxSplitHelper(this.blockList);
        return syntaxSplitHelper.splitAppenderByText(splitParam);

    }

    /**
     * Register statement block.
     *
     * @param statementBlock the statement block
     */
    public void registerStatementBlock(final StatementBlock statementBlock) {
        this.blockList.add(statementBlock);

        appenderFactoryMap.put(statementBlock.getTagName(), statementBlock);

    }

    /**
     * Find area list by jpa list.
     *
     * @param jpaList the jpa list
     * @return the list
     */
    public List<SyntaxAppenderFactory> findAreaListByJpa(LinkedList<SyntaxAppender> jpaList) {
        List<SyntaxAppenderFactory> appenderFactories = new ArrayList<>();
        SyntaxAppender peek = jpaList.peek();

        StatementBlock statementBlock = appenderFactoryMap.get(peek.getText());
        appenderFactories.add(statementBlock.getResultAppenderFactory());
        if (statementBlock.getConditionAppenderFactory() != null) {
            appenderFactories.add(statementBlock.getConditionAppenderFactory());
        }
        if (statementBlock.getSortAppenderFactory() != null) {
            appenderFactories.add(statementBlock.getSortAppenderFactory());
        }
        return appenderFactories;
    }

    /**
     * Gets all block.
     *
     * @return the all block
     */
    public Collection<StatementBlock> getAllBlock() {
        return blockList;
    }

    /**
     * Find block by text statement block.
     *
     * @param text the text
     * @return the statement block
     */
    public StatementBlock findBlockByText(String text) {
        return appenderFactoryMap.get(text);
    }
}
