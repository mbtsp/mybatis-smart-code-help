package com.mybatis.generatorSql.operate;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.mybatis.generatorSql.SyntaxAppenderFactory;
import com.mybatis.generatorSql.appender.CompositeAppender;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.mapping.model.TypeDescriptor;
import com.mybatis.generatorSql.operate.manager.AreaOperateManager;
import com.mybatis.generatorSql.operate.manager.StatementBlock;
import com.mybatis.generatorSql.operate.manager.StatementBlockFactory;
import com.mybatis.generatorSql.util.SyntaxAppenderWrapper;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseOperatorManager implements AreaOperateManager {

    private final StatementBlockFactory syntaxAppenderFactoryManager = new StatementBlockFactory();
    private Set<String> operatorNameList = new HashSet<>();

    /**
     * Gets operator name list.
     *
     * @return the operator name list
     */
    protected Set<String> getOperatorNameList() {
        return operatorNameList;
    }

    /**
     * Sets operator name list.
     *
     * @param nameSet the name set
     */
    protected void setOperatorNameList(final Set<String> nameSet) {
        operatorNameList = nameSet;
    }


    @Override
    public boolean support(String operatorText) {
        return operatorNameList.contains(operatorText);
    }

    @Override
    public TypeDescriptor getReturnWrapper(String text, PsiClass entityClass, LinkedList<SyntaxAppender> linkedList) {
        StatementBlock statementBlock = syntaxAppenderFactoryManager.findBlockByText(text);
        return statementBlock.getReturnDescriptor();
    }

    /**
     * Add operator name.
     *
     * @param operatorName the operator name
     */
    protected void addOperatorName(String operatorName) {
        operatorNameList.add(operatorName);
    }

    @NotNull
    @Override
    public LinkedList<SyntaxAppender> splitAppenderByText(final String splitParam) {
        return this.syntaxAppenderFactoryManager.splitAppenderByText(splitParam);
    }

    /**
     * Can execute boolean.
     *
     * @param areaName the area name
     * @return the boolean
     */
    protected boolean canExecute(final String areaName) {
        return this.operatorNameList.contains(areaName);
    }


    @Override
    public List<String> getCompletionContent(final LinkedList<SyntaxAppender> jpaList) {
        SyntaxAppender firstAreaAppender = jpaList.peek();
        if (firstAreaAppender != null && !this.canExecute(firstAreaAppender.getText())) {
            return Collections.emptyList();
        }
        List<SyntaxAppenderFactory> areaListByJpa = syntaxAppenderFactoryManager.findAreaListByJpa(jpaList);
        return areaListByJpa.stream().flatMap(x -> x.getCompletionContent(jpaList).stream()).collect(Collectors.toList());
    }

    @Override
    public List<String> getCompletionContent() {
        Collection<StatementBlock> allBlock = syntaxAppenderFactoryManager.getAllBlock();
        return allBlock.stream().map(StatementBlock::getResultAppenderFactory)
                .filter(Objects::nonNull)
                .flatMap(x -> x.getCompletionContent(new ArrayList<>()).stream())
                .collect(Collectors.toList());
    }

    /**
     * 获取参数列表
     *
     * @param entityClass
     * @return
     */
    @Override
    public List<TxParameter> getParameters(PsiClass entityClass,
                                           LinkedList<SyntaxAppender> jpaList) {
        SyntaxAppender firstAreaAppender = jpaList.peek();
        if (firstAreaAppender != null && !this.canExecute(firstAreaAppender.getText())) {
            return Collections.emptyList();
        }

        // 转成树形结构
        CompositeAppender compositeAppender = new CompositeAppender();
        SyntaxAppenderWrapper rootSyntaxWrapper = new SyntaxAppenderWrapper(null);
        compositeAppender.toTree(new LinkedList<>(jpaList), rootSyntaxWrapper);
        // 树形结构 根据区域转成map
        LinkedList<SyntaxAppenderWrapper> collector = rootSyntaxWrapper.getCollector();
        Map<String, SyntaxAppenderWrapper> areaAppenderWrapperMap = collector.stream().collect(Collectors.toMap(k -> k.getAppender().getText(), v -> v));
        // 根据区域工厂处理所有区域下的所有符号追加器
        List<SyntaxAppenderFactory> areaListByJpa = syntaxAppenderFactoryManager.findAreaListByJpa(jpaList);
        return areaListByJpa.stream().flatMap(appenderFactory -> {
            SyntaxAppenderWrapper poll = areaAppenderWrapperMap.get(appenderFactory.getTipText());
            LinkedList<SyntaxAppenderWrapper> jpaStringList = poll == null ? new LinkedList<>() : poll.getCollector();
            return appenderFactory.getMxParameter(entityClass, jpaStringList).stream();
        }).collect(Collectors.toList());
    }


    /**
     * Register statement block.
     *
     * @param statementBlock the statement block
     */
    public void registerStatementBlock(StatementBlock statementBlock) {
        this.syntaxAppenderFactoryManager.registerStatementBlock(statementBlock);
    }

    /**
     * Gets tag name.
     *
     * @return the tag name
     */
    protected abstract String getTagName();

    /**
     * 生成xml
     *
     * @param jpaList               the jpa list
     * @param entityClass           the entity class
     * @param psiMethod             the psi method
     * @param tableName             the table name
     * @param conditionFieldWrapper the condition field wrapper
     * @return string
     */
    protected String generateXml(LinkedList<SyntaxAppender> jpaList,
                                 PsiClass entityClass,
                                 PsiMethod psiMethod,
                                 String tableName,
                                 ConditionFieldWrapper conditionFieldWrapper) {
        SyntaxAppender firstAreaAppender = jpaList.peek();
        if (firstAreaAppender != null && !this.canExecute(firstAreaAppender.getText())) {
            return null;
        }

        List<SyntaxAppenderFactory> areaListByJpa = syntaxAppenderFactoryManager.findAreaListByJpa(jpaList);

        LinkedList<TxParameter> parameters = Arrays.stream(psiMethod.getParameterList().getParameters())
                .map(TxParameter::createByPsiParameter)
                .collect(Collectors.toCollection(LinkedList::new));

        return areaListByJpa.stream().map(syntaxAppenderFactory -> {
            // 区域生成的xml内容
            return syntaxAppenderFactory.getFactoryTemplateText(jpaList,
                    entityClass,
                    parameters,
                    tableName,
                    conditionFieldWrapper
            );
        }).filter(StringUtils::isNotBlank).collect(Collectors.joining("\n")) + "\n";

    }


    /**
     * Init custom area.
     *
     * @param areaName     the area name
     * @param mappingField the mapping field
     */
    protected void initCustomArea(String areaName, List<TxField> mappingField) {

    }


}
