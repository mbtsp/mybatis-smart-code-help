//package com.mybatis.zip;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.io.File;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.function.Predicate;
//
//public class ZipUtil {
//    public static void extract(@NotNull Path file, @NotNull Path outputDir, @Nullable FilenameFilter filter, boolean overwrite)
//            throws IOException {
//        new Decompressor.Zip(file.toFile()).filter(FileFilterAdapter.wrap(outputDir, filter)).overwrite(overwrite).extract(outputDir);
//    }
//
//
//    private static final class FileFilterAdapter implements Predicate<String> {
//        private final File myOutputDir;
//        private final FilenameFilter myFilter;
//        private FileFilterAdapter(@NotNull Path outputDir, FilenameFilter filter) {
//            myOutputDir = outputDir.toFile();
//            myFilter = filter;
//        }
//
//        private static FileFilterAdapter wrap(@NotNull Path outputDir, @Nullable FilenameFilter filter) {
//            return filter == null ? null : new FileFilterAdapter(outputDir, filter);
//        }
//
//        @Override
//        public boolean test(String entryName) {
//            File outputFile = new File(myOutputDir, entryName);
//            return myFilter.accept(outputFile.getParentFile(), outputFile.getName());
//        }
//    }
//}
