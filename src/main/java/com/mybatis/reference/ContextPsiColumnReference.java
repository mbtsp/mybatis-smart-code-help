package com.mybatis.reference;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.containers.JBIterable;
import com.mybatis.database.util.DatabaseUtils;
import com.mybatis.utils.MapperUtils;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ContextPsiColumnReference extends PsiReferenceBase<XmlAttributeValue> {

    /**
     * The Resolver.
     */
    protected PsiColumnReferenceSetResolver resolver;
    /**
     * The Index.
     */
    protected int index;
    private PsiClass mapperClass;

    /**
     * Instantiates a new Context psi field reference.
     *
     * @param element     the element
     * @param range       the range
     * @param index       the index
     * @param mapperClass
     */
    public ContextPsiColumnReference(XmlAttributeValue element, TextRange range, int index, PsiClass mapperClass) {
        super(element, range, false);
        this.index = index;
        resolver = new PsiColumnReferenceSetResolver(element);
        this.mapperClass = mapperClass;
    }

    /**
     * 如果能找到正确的列, 线条转到正确的列
     * 无法找到数据库的列, 引用当前节点
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public PsiElement resolve() {
        Optional<DbElement> resolved = resolver.resolveColumn(index);
        return resolved.orElse(null);
    }

    /**
     * 获取用于提示的变量列表
     *
     * @return
     */
    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = getElement().getProject();
        String tableName = MapperUtils.getTableName(project, mapperClass);
        if (StringUtils.isEmpty(tableName)) {
            return new Object[0];
        }

        DbPsiFacade dbPsiFacade = DbPsiFacade.getInstance(project);
        List<DbElement> dbElementList = getDbElements(tableName, dbPsiFacade);
        return dbElementList.size() > 0 ? dbElementList.toArray() : PsiReference.EMPTY_ARRAY;
    }

    @NotNull
    private List<DbElement> getDbElements(String tableName, DbPsiFacade dbPsiFacade) {
        for (DbDataSource dataSource : dbPsiFacade.getDataSources()) {
            JBIterable<? extends DasNamespace> schemas = DasUtil.getSchemas(dataSource);
            for (DasNamespace schema : schemas) {
                DasTable dasTable = DasUtil.findChild(schema, DasTable.class, ObjectKind.TABLE, tableName);
                if (dasTable != null) {
                    List<DbElement> dbElementList = new LinkedList<>();
                    JBIterable<? extends DasColumn> columns = DasUtil.getColumns(dasTable);
                    for (DasColumn column : columns) {
                        Optional<DbElement> optional = DatabaseUtils.getDbElement(dbPsiFacade.getProject(), dataSource, column);
                        optional.ifPresent(dbElementList::add);
                    }
                    return dbElementList;
                }
            }
        }
        return Collections.emptyList();
    }


    /**
     * Gets resolver.
     *
     * @return the resolver
     */
    public PsiColumnReferenceSetResolver getResolver() {
        return resolver;
    }

    /**
     * Sets resolver.
     *
     * @param resolver the resolver
     */
    public void setResolver(PsiColumnReferenceSetResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Gets index.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets index.
     *
     * @param index the index
     */
    public void setIndex(int index) {
        this.index = index;
    }
}

