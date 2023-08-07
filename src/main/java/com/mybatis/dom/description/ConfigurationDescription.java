package com.mybatis.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import com.mybatis.dom.model.Configuration;
import com.mybatis.utils.DomUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigurationDescription extends DomFileDescription<Configuration> {
    /**
     * Instantiates a new Configuration description.
     */
    public ConfigurationDescription() {
        super(Configuration.class, "configuration");
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return DomUtils.isMybatisConfigurationFile(file);
    }
}
