package com.mybatis.generatorSql.mapping.model;

import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 参数描述符
 */
public class TxParameterDescriptor implements TypeDescriptor {

    /**
     * The constant SPACE.
     */
    public static final String SPACE = " ";
    private final Map<String, TxField> fieldColumnNameMapping;
    /**
     * The Parameter list.
     */
    List<TxParameter> parameterList = new ArrayList<>();

    /**
     * Instantiates a new Tx parameter descriptor.
     *
     * @param parameterList the parameter list
     * @param mappingField
     */
    public TxParameterDescriptor(final List<TxParameter> parameterList, List<TxField> mappingField) {
        this.parameterList = parameterList;
        fieldColumnNameMapping = mappingField.stream().collect(Collectors.toMap(TxField::getFieldName, x -> x, (a, b) -> {
            if (!a.getFieldType().equals(b.getFieldType())) {
                throw new RuntimeException("字段类型不匹配, 无法生成SQL");
            }
            return a;
        }));
    }

    /**
     * Add boolean.
     *
     * @param txParameter the tx parameter
     * @return the boolean
     */
    public boolean add(TxParameter txParameter) {
        return parameterList.add(txParameter);
    }

    /**
     * 参数字符串
     * TODO 关于 updateUpdateTimeByUpdateTime 这种情况会导致两个参数都无法传， 事实上可能需要第一个不需要传，第二个需要传
     *
     * @param defaultDateList
     * @return
     */
    @Override
    public String getContent(List<String> defaultDateList) {
        Set<String> addedParamNames = new HashSet<>();
        return parameterList.stream()
                .filter(parameter -> {
                    String fieldType = parameter.getTypeText();
                    // 能通过字段名找到列名， 并且列名不是默认日期字段的，就可以加入到参数中
                    TxField txField = fieldColumnNameMapping.get(parameter.getName());
                    // 字段类型不为空 and 不是默认日期字段
                    return fieldType != null
                            && (txField == null
                            || !defaultDateList.contains(txField.getColumnName())
                            || parameter.getAreaSequence() != AreaSequence.RESULT);
                })
                .map(param -> this.getParameterName(param, addedParamNames))
                .collect(Collectors.joining(",", "(", ");"));
    }

    /**
     * 根据是否需要生成注解字段, 生成注解字段
     *
     * @param txParameter
     * @param addedParamNames
     * @return
     */
    private String getParameterName(TxParameter txParameter, Set<String> addedParamNames) {
        String paramName = txParameter.getName();
        if (!addedParamNames.add(paramName)) {
            paramName = "old" + StringUtils.upperCaseFirstChar(paramName);
        }
        String defineAnnotation = "@Param(\"" + paramName + "\")";
        String defineParam = txParameter.getTypeText() + SPACE + paramName;
        return txParameter.isParamAnnotation() ? defineAnnotation + defineParam : defineParam;
    }

    /**
     * 要导入的类型列表
     *
     * @return
     */
    @Override
    public List<String> getImportList() {
        List<String> collect = parameterList.stream()
                .flatMap(x -> x.getImportClass().stream())
                .collect(Collectors.toList());
        if (collect.size() > 0 || parameterList.size() > 0) {
            collect.add("org.apache.ibatis.annotations.Param");
        }
        return collect;
    }


}
