package com.mybatis.generatorSql.operate;

import com.intellij.openapi.application.WriteAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.mybatis.generatorSql.MapperClassGenerateFactory;
import com.mybatis.generatorSql.appender.CustomFieldAppender;
import com.mybatis.generatorSql.appender.SyntaxAppender;
import com.mybatis.generatorSql.db.adaptor.DasTableAdaptor;
import com.mybatis.generatorSql.db.adaptor.DbmsAdaptor;
import com.mybatis.generatorSql.enums.AppendTypeEnum;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TxParameter;
import com.mybatis.generatorSql.mapping.model.TxParameterDescriptor;
import com.mybatis.generatorSql.mapping.model.TypeDescriptor;
import com.mybatis.generatorSql.operate.manager.AreaOperateManager;
import com.mybatis.generatorSql.operate.manager.AreaOperateManagerFactory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class CommonGenerator implements PlatformGenerator {
    /**
     * The Appender manager.
     */
    final AreaOperateManager appenderManager;
    private final String defaultDateWord;
    private @NotNull
    final LinkedList<SyntaxAppender> jpaList;
    private final List<TxField> mappingField;
    private final String tableName;
    private final PsiClass entityClass;
    private final String text;
    private final Set<String> notNeedsResult = new HashSet<>() {
        {
            add("update");
            add("insert");
            add("delete");
        }
    };

    private CommonGenerator(PsiClass entityClass,
                            String text,
                            DbmsAdaptor dbms,
                            DasTableAdaptor dasTable,
                            String tableName,
                            List<TxField> fields) {
        this.entityClass = entityClass;
        this.text = text;
        mappingField = fields;
        defaultDateWord = dbms.getDefaultDateWord();
        this.tableName = tableName;

        appenderManager = AreaOperateManagerFactory.getAreaOperateManagerByDbms(dbms, mappingField, entityClass, dasTable, this.tableName);
        jpaList = appenderManager.splitAppenderByText(text);
    }

    /**
     * Create editor auto completion common generator.
     *
     * @param entityClass the entity class
     * @param text        the text
     * @param dbms        the dbms
     * @param dasTable    the das table
     * @param tableName   the table name
     * @param fields      the fields
     * @return common generator
     */
    public static CommonGenerator createEditorAutoCompletion(PsiClass entityClass, String text,
                                                             @NotNull DbmsAdaptor dbms,
                                                             DasTableAdaptor dasTable,
                                                             String tableName,
                                                             List<TxField> fields) {
        return new CommonGenerator(entityClass, text, dbms, dasTable, tableName, fields);
    }

    @Override
    public String getDefaultDateWord() {
        return defaultDateWord;
    }

    @Override
    public TypeDescriptor getParameter() {
        List<TxParameter> parameters = appenderManager.getParameters(entityClass, new LinkedList<>(jpaList));
        return new TxParameterDescriptor(parameters, mappingField);
    }

    @Override
    public TypeDescriptor getReturn() {
        LinkedList<SyntaxAppender> linkedList = new LinkedList<>(jpaList);
        return appenderManager.getReturnWrapper(text, entityClass, linkedList);
    }

    @Override
    public void generateMapperXml(MapperClassGenerateFactory mapperClassGenerateFactory,
                                  PsiMethod psiMethod,
                                  ConditionFieldWrapper conditionFieldWrapper,
                                  List<TxField> resultFields) {
        WriteAction.run(() -> {
            // 生成完整版的内容
            Generator generator = conditionFieldWrapper.getGenerator(mapperClassGenerateFactory);
            appenderManager.generateMapperXml(
                    text,
                    new LinkedList<>(jpaList),
                    entityClass,
                    psiMethod,
                    tableName,
                    generator,
                    conditionFieldWrapper,
                    resultFields);
        });
    }

    @Override
    public List<String> getConditionFields() {
        return jpaList.stream()
                .filter(syntaxAppender -> syntaxAppender.getAreaSequence() == AreaSequence.CONDITION
                        && syntaxAppender.getType() == AppendTypeEnum.FIELD &&
                        syntaxAppender instanceof CustomFieldAppender)
                .flatMap(x -> Arrays.stream(((CustomFieldAppender) x).getFieldName().split(",")))
                .collect(Collectors.toList());
    }

    @Override
    public List<TxField> getAllFields() {
        return mappingField;
    }

    @Override
    public PsiClass getEntityClass() {
        return entityClass;
    }

    @Override
    public List<String> getResultFields() {
        SyntaxAppender peek = jpaList.peek();
        if (peek == null || notNeedsResult.contains(peek.getText())) {
            return Collections.emptyList();
        }
        return jpaList.stream()
                .filter(syntaxAppender -> syntaxAppender.getAreaSequence() == AreaSequence.RESULT
                        && syntaxAppender.getType() == AppendTypeEnum.FIELD &&
                        syntaxAppender instanceof CustomFieldAppender)
                .flatMap(x -> Arrays.stream(x.getText().split(",")))
                .collect(Collectors.toList());
    }
}
