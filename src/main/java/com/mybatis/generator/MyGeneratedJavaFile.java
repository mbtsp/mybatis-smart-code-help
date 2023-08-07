package com.mybatis.generator;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.CompilationUnit;

public class MyGeneratedJavaFile extends GeneratedJavaFile {

    private final CompilationUnit compilationUnit;

    private final String fileEncoding;

    private final JavaFormatter javaFormatter;
    private boolean isMergeable = true;

    public MyGeneratedJavaFile(CompilationUnit compilationUnit,
                               String targetProject,
                               String fileEncoding,
                               JavaFormatter javaFormatter) {
        super(compilationUnit, targetProject, fileEncoding, javaFormatter);
        this.compilationUnit = compilationUnit;
        this.fileEncoding = fileEncoding;
        this.javaFormatter = javaFormatter;
    }

    public MyGeneratedJavaFile(CompilationUnit compilationUnit,
                               String targetProject,
                               JavaFormatter javaFormatter) {
        this(compilationUnit, targetProject, null, javaFormatter);
    }

    @Override
    public String getFormattedContent() {
        return javaFormatter.getFormattedContent(compilationUnit);
    }

    @Override
    public String getFileName() {
        return compilationUnit.getType().getShortNameWithoutTypeArguments() + ".java"; //$NON-NLS-1$
    }

    @Override
    public String getTargetPackage() {
        return compilationUnit.getType().getPackageName();
    }

    /**
     * This method is required by the Eclipse Java merger. If you are not
     * running in Eclipse, or some other system that implements the Java merge
     * function, you may return null from this method.
     *
     * @return the CompilationUnit associated with this file, or null if the
     * file is not mergeable.
     */
    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    /**
     * A Java file is mergeable if the getCompilationUnit() method returns a valid compilation unit.
     *
     * @return true, if is mergeable
     */
    @Override
    public boolean isMergeable() {
        return isMergeable;
    }

    public void setMergeable(boolean mergeable) {
        isMergeable = mergeable;
    }

    public String getFileEncoding() {
        return fileEncoding;
    }
}
