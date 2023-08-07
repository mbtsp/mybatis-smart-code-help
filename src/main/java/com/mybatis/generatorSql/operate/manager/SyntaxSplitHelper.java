package com.mybatis.generatorSql.operate.manager;

import com.mybatis.generatorSql.appender.SyntaxAppender;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 分割符号帮助
 */
public class SyntaxSplitHelper {

    private final List<StatementBlock> statementBlockList;
    /**
     * The String length comparator.
     */
// 文本长度优先排序, 其次按照区域顺序排序
    Comparator<SyntaxAppender> stringLengthComparator = (o1, o2) ->
    {
        // 长度相等, 按照区域顺序排序
        int compare = o2.getText().length() - o1.getText().length();
        if (compare == 0) {
            return o1.getAreaSequence().getSequence() - o2.getAreaSequence().getSequence();
        }
        return compare;
    };

    /**
     * The Syntax appender comparator.
     */
    Comparator<LinkedList<SyntaxAppender>> syntaxAppenderComparator = (o1, o2) ->
    {
        // 长度相等, 按照区域顺序排序
        int listComparor = o2.size() - o1.size();
        if (listComparor == 0 && o2.size() == 1 && o1.size() == 1) {
            return o2.get(0).getText().length() - o1.get(0).getText().length();
        }
        return listComparor;
    };

    /**
     * Instantiates a new Syntax split helper.
     *
     * @param statementBlockList the statement block list
     */
    public SyntaxSplitHelper(final List<StatementBlock> statementBlockList) {
        this.statementBlockList = statementBlockList;
    }

    /**
     * Split appender by text linked list.
     *
     * @param splitText the split text
     * @return linked list
     */
    @NotNull
    public LinkedList<SyntaxAppender> splitAppenderByText(final String splitText) {
        PriorityQueue<LinkedList<SyntaxAppender>> collect = new PriorityQueue<>(syntaxAppenderComparator);
        for (StatementBlock statementBlock : statementBlockList) {
            LinkedList<SyntaxAppender> priority = statementBlock.findPriority(stringLengthComparator, splitText);
            if (priority.size() > 0 && !collect.contains(priority)) {
                collect.add(priority);
            }
        }
        LinkedList<SyntaxAppender> peek = collect.peek();
        return peek != null ? peek : new LinkedList<>();
    }

}
