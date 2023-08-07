package com.mybatis.generator;


import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.DomElement;
import com.mybatis.dom.model.IdDomElement;
import com.mybatis.dom.model.Mapper;
import com.mybatis.dom.model.ResultMap;
import com.mybatis.dom.model.Select;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Select 代码生成器
 * </p>
 *
 * @author yanglin jobob
 * @since 2018 -07-30
 */
public class SelectGenerator extends AbstractStatementGenerator {

    /**
     * Instantiates a new Select generator.
     *
     * @param patterns the patterns
     */
    public SelectGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @NotNull
    @Override
    protected IdDomElement getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        Select select = mapper.addSelect();
        setupResultType(method, select);
        return select;
    }

    private void setupResultType(PsiMethod method, Select select) {
        Optional<PsiClass> clazz = AbstractStatementGenerator.getSelectResultType(method);
        if (clazz.isEmpty()) {
            return;
        }
        DomElement domElement = select.getParent();
        boolean flag = false;
        if (domElement != null) {
            Mapper mapper = (Mapper) domElement;
            List<ResultMap> resultMaps = mapper.getResultMaps();
            if (!resultMaps.isEmpty()) {
                for (ResultMap resultMap : resultMaps) {
                    PsiClass psiClass = resultMap.getType().getValue();
                    if (psiClass != null && psiClass.equals(clazz.get())) {
                        select.getResultMap().setStringValue(resultMap.getId().getStringValue());
                        flag = true;
                        break;
                    }
                }

            }
        }
        if (!flag) {
            clazz.ifPresent(psiClass -> select.getResultType().setValue(psiClass));
        }
    }

    @NotNull
    @Override
    public String getId() {
        return "SelectGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Select Statement";
    }
}
