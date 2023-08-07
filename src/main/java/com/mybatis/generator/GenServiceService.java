package com.mybatis.generator;

import com.google.common.collect.Lists;
import com.intellij.openapi.ui.Messages;
import com.mybatis.common.Common;
import com.mybatis.freemarker.TemplateUtil;
import com.mybatis.model.CacheModel.GenerateServiceConfig;
import com.mybatis.model.FileBuildResult;
import com.mybatis.utils.FileUtils;

import java.util.List;

public class GenServiceService {
    public static boolean generatorServiceInterfaceByFtl(GenerateServiceConfig generateServiceConfig) {
        List<String> retList = Lists.newArrayList();
        String generateServiceString = TemplateUtil.processToString("generatorServiceImpl.ftl", generateServiceConfig);
        retList.add(generateServiceString);
        FileBuildResult fileBuildResult = FileUtils.writeFiles(generateServiceConfig.getServicePath(), retList);
        if (!fileBuildResult.isSuccess()) {
            Messages.showErrorDialog(fileBuildResult.getMsg(), "Error");
            Common.isStop = true;
            return false;
        }
        return fileBuildResult.isSuccess();
    }
}
