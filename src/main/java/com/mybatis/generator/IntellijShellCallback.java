package com.mybatis.generator;

import com.google.common.collect.Lists;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntellijShellCallback extends DefaultShellCallback {
    private static final Set<String> methodSet = new HashSet<>();
    private final Project project;
    private String existingMapperPath;
    private boolean isMergeSupported;

    public IntellijShellCallback(boolean overwrite, boolean isMergeSupported, Project project) {
        super(overwrite);
        this.project = project;
        this.isMergeSupported = isMergeSupported;
        methodSet.add("countByExample");
        methodSet.add("deleteByExample");
        methodSet.add("deleteByPrimaryKey");
        methodSet.add("insert");
        methodSet.add("insertSelective");
        methodSet.add("selectByExample");
        methodSet.add("selectByPrimaryKey");
        methodSet.add("updateByExampleSelective");
        methodSet.add("updateByExample");
        methodSet.add("updateByPrimaryKeySelective");
        methodSet.add("updateByPrimaryKey");
    }

    /**
     * Return true if the callback supports Java merging, otherwise false.
     * The <code>mergeJavaFile()</code> method will be called only if this
     * method returns <code>true</code>.
     *
     * @return a boolean specifying whether Java merge is supported or not
     */
    @Override
    public boolean isMergeSupported() {
        return isMergeSupported;
    }

    /**
     * This method is called if a newly generated Java file would
     * overwrite an existing file. This method should return the merged source
     * (formatted). The generator will write the merged source as-is to the file
     * system.
     *
     * <p>A merge typically follows these steps:
     * <ol>
     * <li>Delete any methods/fields in the existing file that have the
     * specified JavaDoc tag</li>
     * <li>Add any new super interfaces from the new file into the existing file
     * </li>
     * <li>Make sure that the existing file's super class matches the new file</li>
     * <li>Make sure that the existing file is of the same type as the existing
     * file (either interface or class)</li>
     * <li>Add any new imports from the new file into the existing file</li>
     * <li>Add all methods and fields from the new file into the existing file</li>
     * <li>Format the resulting source string</li>
     * </ol>
     *
     * <p>This method is called only if you return <code>true</code> from
     * <code>isMergeSupported()</code>.
     *
     * @param newFileSource the source of the newly generated Java file
     * @param existingFile  the existing Java file
     * @param javadocTags   the JavaDoc tags that denotes which methods and fields in the
     *                      old file to delete (if the Java element has any of these tags,
     *                      the element is eligible for merge)
     * @param fileEncoding  the file encoding for reading existing Java files.  Can be null,
     *                      in which case the platform default encoding will be used.
     * @return the merged source, properly formatted. The source will be saved
     * exactly as returned from this method.
     * @throws ShellException if the file cannot be merged for some reason. If this
     *                        exception is thrown, nothing will be saved and the
     *                        existing file will remain undisturbed. The generator will add the
     *                        exception message to the list of warnings automatically.
     */
    @Override
    public String mergeJavaFile(String newFileSource, File existingFile, String[] javadocTags, String fileEncoding) throws ShellException {
        if (existingFile == null) {
            return newFileSource;
        }
        String oldFilePath = existingFile.getPath();
        File file = new File(oldFilePath);
        if (!file.exists()) {
            return newFileSource;
        }
        String formattedPath = oldFilePath.replaceAll("\\\\", "/");

        String[] split = formattedPath.split("/");
        if (split.length == 0) {
            return newFileSource;
        }

        String fileName = split[split.length - 1];

        if (!fileName.endsWith(".java")) {
            return newFileSource;
        }

        String className = fileName.substring(0, fileName.length() - ".java".length());
        PsiClass[] classesByName = PsiShortNamesCache.getInstance(this.project).getClassesByName(className,
                GlobalSearchScope.projectScope(this.project));
        if (classesByName.length == 0) {
            return newFileSource;
        }

        PsiClass theClass = null;
        for (PsiClass psiClass : classesByName) {
            String psiClassPath = psiClass.getContainingFile().getVirtualFile().getPath();
            if (isSameFilePath(psiClassPath, oldFilePath)) {
                theClass = psiClass;
            }
        }

        if (theClass == null) {
            return newFileSource;
        }
        //如果是service 或者service 实现类,咋 只保存和mapper 方法不一样的名字
        if (theClass.isInterface()) {
            this.existingMapperPath = oldFilePath;
            if (StringUtils.isNotBlank(theClass.getName()) && theClass.getName().contains("Service")) {
                //service 实现类
                return getMergedMapperString(newFileSource, theClass, true);
            }

            return getMergedMapperString(newFileSource, theClass);
        }
        if (StringUtils.isNotBlank(theClass.getName()) && theClass.getName().contains("Service")) {
            //service 实现类
            return getMergedMapperString(newFileSource, theClass, true);
        }
        return newFileSource;
    }

    private String getMergedMapperString(String newFileSource, PsiClass originalClass, boolean mgb) {
        try {
            newFileSource = newFileSource.replace("\r\n", "\n");
            JavaFileType javaFileType = JavaFileType.INSTANCE;
            PsiJavaFile fileFromText = (PsiJavaFile) PsiFileFactory.getInstance(this.project).createFileFromText(originalClass.getName(), javaFileType, newFileSource);
            final PsiClass classFromText = fileFromText.getClasses()[0];
            PsiMethod[] newMethods = classFromText.getMethods();
            PsiMethod[] originalClassMethods = originalClass.getMethods();
            List<PsiMethod> newClassMissedMethods = extractMethodNotInNew(newMethods, originalClassMethods, mgb);

            for (PsiMethod missedMethod : newClassMissedMethods) {
                classFromText.add(missedMethod);
                WriteCommandAction.runWriteCommandAction(this.project, () -> {
                    CodeStyleManager.getInstance(this.project).reformat(classFromText);
                    JavaCodeStyleManager.getInstance(this.project).shortenClassReferences(classFromText);
                });
            }
            try {
                JavaCodeStyleManager.getInstance(project).optimizeImports(originalClass.getContainingFile());
            } catch (Exception ignored) {
            }

            return classFromText.getContainingFile().getText();
        } catch (Exception e) {
            return originalClass.getContainingFile().getText();
        }
    }

    private String getMergedMapperString(String newFileSource, PsiClass originalClass) {
        return getMergedMapperString(newFileSource, originalClass, false);
    }

    @NotNull
    private List<PsiMethod> extractMethodNotInNew(PsiMethod[] newMethods, PsiMethod[] originalClassMethods, boolean mgb) {
        List<PsiMethod> missedMethods = Lists.newArrayList();
        for (PsiMethod originMethods : originalClassMethods) {
            boolean ifContainOriginMethod = false;
            for (PsiMethod newMethod : newMethods) {
                if (newMethod.getName().equals(originMethods.getName())) {
                    ifContainOriginMethod = true;
                    break;
                }
            }
            if (!ifContainOriginMethod) {
                if (mgb && !methodSet.contains(originMethods.getName())) {
                    missedMethods.add(originMethods);
                } else if (!mgb) {
                    missedMethods.add(originMethods);
                }

            }
        }
        return missedMethods;
    }

    @NotNull
    private List<PsiMethod> extractMethodNotInNew(PsiMethod[] newMethods, PsiMethod[] originalClassMethods) {
        List<PsiMethod> missedMethods = Lists.newArrayList();
        for (PsiMethod originMethods : originalClassMethods) {
            boolean ifContainOriginMethod = false;
            String text = originMethods.getText();

            for (PsiMethod newMethod : newMethods) {
                if (newMethod.getName().equals(originMethods.getName())) {
                    ifContainOriginMethod = true;
                    break;
                }
            }
            if (!ifContainOriginMethod) {
                missedMethods.add(originMethods);
            }
        }
        return missedMethods;
    }

    private boolean isSameFilePath(String psiClassPath, String existingFileFullPath) {
        String classFormattedPath = psiClassPath.replace("\\", "/");
        String formattedExistingFullPath = existingFileFullPath.replace("\\", "/");
        return classFormattedPath.equals(formattedExistingFullPath);
    }
}
