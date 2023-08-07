package com.mybatis.provider;

import com.intellij.ide.IconProvider;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.mybatis.dom.model.Mapper;
import com.mybatis.model.CacheModel.MybatisSettingConfig;
import com.mybatis.state.MybatisSettingsState;
import com.mybatis.utils.IconUtils;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

public class MapperAndXmlIconProvider extends IconProvider {

    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement element, int flags) {
        MybatisSettingConfig mybatisSettingConfig = MybatisSettingsState.getInstance().getState();
        if (mybatisSettingConfig != null && !mybatisSettingConfig.isShowFileBirdIcon()) {
            return null;
        }
        Language language = element.getLanguage();
        if (language.is(JavaLanguage.INSTANCE)) {
            if (element instanceof PsiClass) {
                Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(element.getProject(), (PsiClass) element);
                if (firstMapper.isPresent()) {
                    return IconUtils.JAVA_MYBATIS_ICON;
                }
            }
        }
        if (MapperUtils.isElementWithinMybatisFile(element)) {
            return IconUtils.XML_MYBATIS_ICON;
        }
        return null;
    }
}
