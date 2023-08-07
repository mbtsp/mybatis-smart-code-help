package com.mybatis.definitionsearch;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.searches.DefinitionsScopedSearch;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.mybatis.model.CacheModel.MybatisSettingConfig;
import com.mybatis.service.JavaService;
import com.mybatis.state.MybatisSettingsState;
import org.jetbrains.annotations.NotNull;

public class MapperDefinitionSearch extends QueryExecutorBase<PsiElement, DefinitionsScopedSearch.SearchParameters> {

    /**
     * Instantiates a new Mapper definition search.
     */
    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull DefinitionsScopedSearch.SearchParameters queryParameters, @NotNull Processor<? super PsiElement> consumer) {
        if (!(queryParameters.getElement() instanceof PsiMethod)) {
            return;
        }
        MybatisSettingConfig mybatisSettingConfig = MybatisSettingsState.getInstance().getState();
        if (mybatisSettingConfig != null && !mybatisSettingConfig.isEnableMapperJumpXml()) {
            return;
        }
        Processor<DomElement> processor = domElement -> consumer.process(domElement.getXmlElement());
        JavaService.getInstance(queryParameters.getProject()).process(queryParameters.getElement(), processor);
    }
}

