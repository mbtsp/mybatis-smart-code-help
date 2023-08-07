package com.mybatis.generator;

import com.google.common.collect.Lists;
import com.intellij.openapi.ui.Messages;
import com.mybatis.common.Common;
import com.mybatis.freemarker.TemplateUtil;
import com.mybatis.model.CacheModel.GenerateServiceInterfaceConfig;
import com.mybatis.model.FileBuildResult;
import com.mybatis.utils.FileUtils;

import java.util.List;

public class GenServiceInterfaceService {
    public static boolean generatorServiceInterfaceByFtl(GenerateServiceInterfaceConfig generateServiceInterfaceConfig) {
        List<String> retList = Lists.newArrayList();
        String generateServiceString = TemplateUtil.processToString("generatorServiceInterface.ftl", generateServiceInterfaceConfig);
        retList.add(generateServiceString);
        FileBuildResult fileBuildResult = FileUtils.writeFiles(generateServiceInterfaceConfig.getServiceInterfacePath(), retList);
        if (!fileBuildResult.isSuccess()) {
            Messages.showErrorDialog(fileBuildResult.getMsg(), "Error");
            Common.isStop = true;
            return false;
        }
        return fileBuildResult.isSuccess();
    }

//    private void reFormatCode(@NotNull Project project){
//        PsiJavaFile fileFromText = (PsiJavaFile) PsiFileFactory.getInstance(project).createFileFromText(projectState.getModelName(), JavaFileType.INSTANCE, newFileSource);
//        WriteCommandAction.runWriteCommandAction(project,()->{
//            CodeStyleManager.getInstance(project).reformat(classFromText);
//        });
//    }
}
