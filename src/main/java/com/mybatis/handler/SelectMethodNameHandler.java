package com.mybatis.handler;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.mybatis.enums.MethodNameEnums;
import com.mybatis.utils.StringUtils;
import org.apache.commons.compress.utils.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectMethodNameHandler extends AbstractMethodNameHandler {
    private static final Set<MethodNameEnums> METHOD_NAME_SET = Sets.newHashSet(
            MethodNameEnums.BY,
            MethodNameEnums.AND);
    private final List<String> fields;
    private final List<String> methodFields;
    private final MethodNameEnums methodType;
    private List<String> groupList;
    private List<String> orderList;

    public SelectMethodNameHandler() {
        super();
        methodType = MethodNameEnums.SELECT;
        fields = new ArrayList<>();
        methodFields = new ArrayList<>();
        groupList = new ArrayList<>();
        orderList = new ArrayList<>();
    }

    public SelectMethodNameHandler(List<String> methodNames, List<String> contributorMethodNames) {
        super();
        methodType = MethodNameEnums.SELECT;
        fields = new ArrayList<>();
        methodFields = new ArrayList<>();
    }


    @Override
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, CompletionParameters parameters) {
        PsiElement psiElement = parameters.getOriginalPosition();
        methodNames(project, psiClass, psiElement.getText(), CompletionUtil.findJavaIdentifierPrefix(parameters));
    }


    @Override
    public void methodNames(@NotNull Project project, @NotNull PsiClass psiClass, String text, String prefix) {
        PsiField[] psiFields = psiClass.getFields();
        List<String> results = new ArrayList<>();
        if (!text.contains(MethodNameEnums.SELECT.getKey()) && !text.contains(MethodNameEnums.FIND.getKey())) {
            results.add(MethodNameEnums.SELECT.getKey() + MethodNameEnums.ALL.getKey());
        }
        if (!text.contains(MethodNameEnums.FIND.getKey()) && !text.contains(MethodNameEnums.SELECT.getKey())) {
            results.add(MethodNameEnums.FIND.getKey() + MethodNameEnums.ALL.getKey());
        }
        methodNames.add(MethodNameEnums.SELECT.getKey() + MethodNameEnums.ALL.getKey());
        methodNames.add(MethodNameEnums.SELECT.getKey());
        methodNames.add(MethodNameEnums.FIND.getKey() + MethodNameEnums.ALL.getKey());
        methodNames.add(MethodNameEnums.FIND.getKey());
        methodNames.add(MethodNameEnums.BY.getKey());
        results.add(MethodNameEnums.IN.getKey());
        if (psiFields.length == 0) {
            return;
        }
        orderList.add(MethodNameEnums.ORDER.getKey());
        orderList.add(MethodNameEnums.ORDER_BY.getKey());
        orderList.add(MethodNameEnums.DESC.getKey());
        orderList.add(MethodNameEnums.ASC.getKey());
        for (PsiField field : psiFields) {
            groupList.add(MethodNameEnums.GROUP_BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
            groupList.add(MethodNameEnums.GROUP.getKey());
            orderList.add(MethodNameEnums.ORDER_BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
            orderList.add(MethodNameEnums.ORDER_BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.DESC.getKey());
            orderList.add(MethodNameEnums.ORDER_BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.ASC.getKey());
            orderList.add(MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
            orderList.add(MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.DESC.getKey());
            orderList.add(MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.ASC.getKey());
            if (!prefix.contains(MethodNameEnums.GROUP.getKey()) && !prefix.contains(MethodNameEnums.HAVING.getKey())
                    && !prefix.contains(MethodNameEnums.ORDER_BY.getKey()) && !prefix.contains(MethodNameEnums.ORDER.getKey())) {
                List<String> selectList = new ArrayList<>();
                selectList.add(MethodNameEnums.FIND.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
                selectList.add(MethodNameEnums.FIND.getKey() + MethodNameEnums.ALL.getKey() + MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
                selectList.add(MethodNameEnums.SELECT.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
                selectList.add(MethodNameEnums.SELECT.getKey() + MethodNameEnums.ALL.getKey() + MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
                if (!text.contains(MethodNameEnums.SELECT.getKey()) && !text.contains(MethodNameEnums.FIND.getKey())) {
                    results.addAll(selectList);
                }
                List<String> byList = new ArrayList<>();
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.IN.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.NOT_INT.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.LIKE.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.NOT_INT.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.LESS_THAN.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.LESS_THAN_EQUAL.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.GREATER_THAN.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()) + MethodNameEnums.GREATER_THAN_EQUAL.getKey());
                if (prefix.contains(MethodNameEnums.BY.getKey())) {
                    results.addAll(byList);
                    results.addAll(groupList);
                }
                for (MethodNameEnums enums : METHOD_NAME_SET) {
                    if (enums.getType() == 0) {
                        if (enums.equals(MethodNameEnums.BY) && !prefix.contains(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()))) {
                            results.add(enums.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
                        } else if (enums.equals(MethodNameEnums.AND) && !prefix.contains(MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()))) {
                            results.add(enums.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
                        }

                    } else if (enums.getType() == 1) {
                        results.add(StringUtils.upperCaseFirstChar(field.getName()) + enums.getKey());
                    }
                }
                methodNames.addAll(selectList);
                methodNames.addAll(byList);
            } else if (prefix.contains(MethodNameEnums.GROUP_BY.getKey()) && !prefix.contains(MethodNameEnums.HAVING.getKey()) && !prefix.contains(MethodNameEnums.ORDER.getKey())) {
                results.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
            } else if (prefix.contains(MethodNameEnums.HAVING.getKey()) && !prefix.contains(MethodNameEnums.ORDER.getKey())) {

            } else if (prefix.contains(MethodNameEnums.ORDER.getKey())) {
                results.addAll(orderList);
                results.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
                results.add(StringUtils.upperCaseFirstChar(field.getName()));
            }
            fields.add(field.getName());
            methodFields.add(StringUtils.upperCaseFirstChar(field.getName()));
            if (!prefix.contains(MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field.getName()))) {

            }
            methodNames.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field.getName()));
        }
        methodNames.addAll(orderList);
        methodNames.addAll(groupList);
        methodNames.addAll(results);
        methodNames.addAll(methodFields);
        results.addAll(methodFields);
        contributorMethodNames = filterString(results, text, prefix);
    }

    @Override
    public void methodNames(String text, String prefix, List<String> fieldNames) {
        List<String> results = new ArrayList<>();
        if (!text.contains(MethodNameEnums.SELECT.getKey())) {
            results.add(MethodNameEnums.SELECT.getKey() + MethodNameEnums.ALL.getKey());
        }
        if (!text.contains(MethodNameEnums.FIND.getKey())) {
            results.add(MethodNameEnums.FIND.getKey() + MethodNameEnums.ALL.getKey());
        }
        methodNames.add(MethodNameEnums.SELECT.getKey() + MethodNameEnums.ALL.getKey());
        methodNames.add(MethodNameEnums.SELECT.getKey());
        methodNames.add(MethodNameEnums.FIND.getKey() + MethodNameEnums.ALL.getKey());
        methodNames.add(MethodNameEnums.FIND.getKey());
        methodNames.add(MethodNameEnums.BY.getKey());
        results.add(MethodNameEnums.IN.getKey());
        if (fieldNames == null || fieldNames.isEmpty()) {
            return;
        }
        orderList.add(MethodNameEnums.ORDER.getKey());
        orderList.add(MethodNameEnums.ORDER_BY.getKey());
        orderList.add(MethodNameEnums.DESC.getKey());
        orderList.add(MethodNameEnums.ASC.getKey());
        for (String field : fieldNames) {
            groupList.add(MethodNameEnums.GROUP_BY.getKey() + StringUtils.upperCaseFirstChar(field));
            groupList.add(MethodNameEnums.GROUP.getKey());
            orderList.add(MethodNameEnums.ORDER_BY.getKey() + StringUtils.upperCaseFirstChar(field));
            orderList.add(MethodNameEnums.ORDER_BY.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.DESC.getKey());
            orderList.add(MethodNameEnums.ORDER_BY.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.ASC.getKey());
            orderList.add(MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field));
            orderList.add(MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.DESC.getKey());
            orderList.add(MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.ASC.getKey());
            if (!prefix.contains(MethodNameEnums.GROUP.getKey()) && !prefix.contains(MethodNameEnums.HAVING.getKey())
                    && !prefix.contains(MethodNameEnums.ORDER_BY.getKey()) && !prefix.contains(MethodNameEnums.ORDER.getKey())) {
                List<String> selectList = new ArrayList<>();
                selectList.add(MethodNameEnums.SELECT.getKey() + StringUtils.upperCaseFirstChar(field));
                selectList.add(MethodNameEnums.SELECT.getKey() + MethodNameEnums.ALL.getKey() + MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field));
                selectList.add(MethodNameEnums.FIND.getKey() + StringUtils.upperCaseFirstChar(field));
                selectList.add(MethodNameEnums.FIND.getKey() + MethodNameEnums.ALL.getKey() + MethodNameEnums.BY.getKey() + StringUtils.upperCaseFirstChar(field));
                if (!text.contains(MethodNameEnums.SELECT.getKey()) && !text.contains(MethodNameEnums.FIND.getKey())) {
                    results.addAll(selectList);
                }
                List<String> byList = new ArrayList<>();
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.IN.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.NOT_INT.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.LIKE.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.NOT_INT.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.LESS_THAN.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.LESS_THAN_EQUAL.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.GREATER_THAN.getKey());
                byList.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field) + MethodNameEnums.GREATER_THAN_EQUAL.getKey());
                if (prefix.contains(MethodNameEnums.BY.getKey())) {
                    results.addAll(byList);
                    results.addAll(groupList);
                }
                for (MethodNameEnums enums : METHOD_NAME_SET) {
                    if (enums.getType() == 0) {
                        results.add(enums.getKey() + StringUtils.upperCaseFirstChar(field));
                    } else if (enums.getType() == 1) {
                        results.add(StringUtils.upperCaseFirstChar(field) + enums.getKey());
                    }
                }
                methodNames.addAll(selectList);
                methodNames.addAll(byList);
            } else if (prefix.contains(MethodNameEnums.GROUP_BY.getKey()) && !prefix.contains(MethodNameEnums.HAVING.getKey()) && !prefix.contains(MethodNameEnums.ORDER.getKey())) {
                results.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field));
            } else if (prefix.contains(MethodNameEnums.HAVING.getKey()) && !prefix.contains(MethodNameEnums.ORDER.getKey())) {

            } else if (prefix.contains(MethodNameEnums.ORDER.getKey())) {
                results.addAll(orderList);
                results.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field));
                results.add(StringUtils.upperCaseFirstChar(field));
            }
            fields.add(field);
            methodFields.add(StringUtils.upperCaseFirstChar(field));
            methodNames.add(MethodNameEnums.AND.getKey() + StringUtils.upperCaseFirstChar(field));
        }
        methodNames.addAll(orderList);
        methodNames.addAll(groupList);
        methodNames.addAll(results);
        methodNames.addAll(methodFields);
        results.addAll(methodFields);
        contributorMethodNames = filterString(results, text, prefix);
    }


    @Override
    public List<String> getMethodNames() {
        return this.methodNames;
    }

    @Override
    public List<String> getContributorNames() {
        return this.contributorMethodNames;
    }

    @Override
    public MethodNameEnums getMethodType() {
        return this.methodType;
    }

    protected List<String> filterString(List<String> list, String text, String prefix) {
        if (StringUtils.isBlank(text) || list == null || list.isEmpty()) {
            return list;
        }
//        List<String> oldList = new ArrayList<>(list);
        if (!prefix.contains(MethodNameEnums.BY.getKey()) && (prefix.contains(MethodNameEnums.SELECT.getKey()) || prefix.contains(MethodNameEnums.FIND.getKey()))) {
            list = delMethodName(prefix, list);
            if (prefix.contains(MethodNameEnums.ALL.getKey())) {
                list = list.stream().filter(str -> str.contains(MethodNameEnums.BY.getKey()) || str.contains(MethodNameEnums.GROUP.getKey())
                        || str.contains(MethodNameEnums.HAVING.getKey()) || str.contains(MethodNameEnums.ORDER_BY.getKey())
                        || str.contains(MethodNameEnums.ORDER.getKey())).collect(Collectors.toList());
                return list;
            }
            if (prefix.equals(MethodNameEnums.SELECT.getKey()) || prefix.equals(MethodNameEnums.FIND.getKey())) {
                //返回字符
                list = list.stream().filter(this::isMethodName).collect(Collectors.toList());
                return list;
            }
            //排除已经出现的字段
            //1.1 等到所有And开头的字段
            if (text.contains(MethodNameEnums.BY.getKey())) {
                list = list.stream().filter(str -> str.contains(MethodNameEnums.AND.getKey()) && !prefix.contains(str)).collect(Collectors.toList());
            } else {
                list = list.stream().filter(str -> (str.contains(MethodNameEnums.BY.getKey()) || str.contains(MethodNameEnums.AND.getKey()) && !prefix.contains(str))).collect(Collectors.toList());
            }

        } else if (prefix.contains(MethodNameEnums.BY.getKey()) && !prefix.contains(MethodNameEnums.GROUP.getKey())) {
            //处理编辑的时候就有By的字符
            String sub = text.substring(prefix.indexOf(MethodNameEnums.BY.getKey()) + MethodNameEnums.BY.getKey().length());
            if (StringUtils.isBlank(sub) || prefix.endsWith(MethodNameEnums.BY.getKey())) {
                //只加载基础字段
                return methodFields;
            }
            //去除基础字段
            list = delMethodAll(list);
            list = list.stream().filter(str -> !sub.contains(str) && !str.contains(MethodNameEnums.BY.getKey())).collect(Collectors.toList());
        } else if ((prefix.contains(MethodNameEnums.GROUP.getKey()) || prefix.contains(MethodNameEnums.GROUP_BY.getKey())) && !prefix.contains(MethodNameEnums.HAVING.getKey()) && !prefix.contains(MethodNameEnums.ORDER.getKey())) {
            //处理group 之后的数据
            String sub = text.substring(prefix.indexOf(MethodNameEnums.GROUP.getKey()) + MethodNameEnums.GROUP.getKey().length());
            if (StringUtils.isBlank(sub)) {
                return groupList;
            }
            if (prefix.endsWith(MethodNameEnums.BY.getKey())) {
                return methodFields;
            }
            if (sub.trim().endsWith(MethodNameEnums.BY.getKey())) {
                //group by
                return methodFields;
            }
            return list.stream().filter(str -> (str.contains(MethodNameEnums.AND.getKey()) || str.contains(MethodNameEnums.HAVING.getKey()) || str.contains(MethodNameEnums.ORDER.getKey())) && !prefix.contains(str) && !prefix.contains(str.replace(MethodNameEnums.AND.getKey(), "").trim())).collect(Collectors.toList());
        } else if (prefix.contains(MethodNameEnums.HAVING.getKey()) && !prefix.contains(MethodNameEnums.ORDER.getKey())) {
            //暂不处理
        } else if (prefix.contains(MethodNameEnums.ORDER.getKey())) {
            String sub = text.substring(prefix.indexOf(MethodNameEnums.ORDER.getKey()) + MethodNameEnums.ORDER.getKey().length());
            if (StringUtils.isBlank(sub)) {
                return orderList.stream().filter(str -> str.contains(MethodNameEnums.BY.getKey()) && !str.startsWith(MethodNameEnums.ORDER.getKey())).collect(Collectors.toList());
            }
            if (sub.trim().endsWith(MethodNameEnums.BY.getKey())) {
                return methodFields;
            }
            if (prefix.endsWith(MethodNameEnums.DESC.getKey()) || prefix.endsWith(MethodNameEnums.ASC.getKey())) {
                return new ArrayList<>();
            }
            return list.stream().filter(str -> !prefix.contains(str) && (str.contains(MethodNameEnums.AND.getKey()) || str.equals(MethodNameEnums.DESC.getKey()) || str.equals(MethodNameEnums.ASC.getKey()))).collect(Collectors.toList());
        }

        return list;
    }


    private boolean isMethodName(String name) {
        if (StringUtils.isBlank(name) || methodFields.isEmpty()) {
            return false;
        }
        for (String str : methodFields) {
            if (name.equals(str)) {
                return true;
            }
        }
        return false;
    }


    private List<String> delMethodName(String str, List<String> result) {
        if (result == null || result.isEmpty()) {
            return result;
        }
        if (StringUtils.isBlank(str) || methodFields.isEmpty()) {
            return result;
        }
        List<String> results = new ArrayList<>(result);
        for (String name : methodFields) {
            if (name.contains(str)) {
                results.addAll(delListByName(results, name));
            }
        }
        return results;
    }

    private List<String> delListByName(List<String> list, String name) {
        if (StringUtils.isBlank(name) || list == null || list.isEmpty()) {
            return list;
        }
        List<String> result = new ArrayList<>();
        for (String str : list) {
            if (!str.equals(name)) {
                result.add(str);
            }
        }
        return result;
    }

    private List<String> delMethodAll(List<String> result) {
        if (result == null || result.isEmpty()) {
            return result;
        }
        return result.stream().filter(str -> !methodFields.contains(str)).collect(Collectors.toList());
    }


}
