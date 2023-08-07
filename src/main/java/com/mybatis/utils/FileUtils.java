package com.mybatis.utils;

import com.mybatis.model.FileBuildResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static FileBuildResult writeFiles(String path, List<String> retList) {
        FileBuildResult fileBuildResult = new FileBuildResult();
        fileBuildResult.setSuccess(false);
        File file = new File(path);
        if (file.exists() && !file.isFile()) {
            fileBuildResult.setMsg("path is not a file address");
            return fileBuildResult;
        }
        if (file.exists()) {
            boolean del = file.delete();
            if (!del) {
                log.warn("del {} file is fail", path);
            }
        }
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            boolean flag = file.getParentFile().mkdirs();
            if (!flag) {
                log.warn("file mkdirs is fail,path:{}", file.getParentFile());
            }
        }
        try {
            Files.write(Paths.get(path), retList, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("can't write file  to path " + path, e);
        }
        fileBuildResult.setSuccess(true);
        return fileBuildResult;
    }
}
