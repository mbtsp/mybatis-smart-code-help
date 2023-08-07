package com.mybatis.intentionAction;

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.mybatis.database.util.DatabaseUtils;
import com.mybatis.dom.model.Mapper;
import com.mybatis.generatorSql.MapperClassGenerateFactory;
import com.mybatis.generatorSql.PlatformSimpleGenerator;
import com.mybatis.generatorSql.iftest.ConditionFieldWrapper;
import com.mybatis.generatorSql.iftest.ConditionIfTestWrapper;
import com.mybatis.generatorSql.mapping.EntityMappingHolder;
import com.mybatis.generatorSql.mapping.EntityMappingResolverFactory;
import com.mybatis.generatorSql.mapping.model.TxField;
import com.mybatis.generatorSql.mapping.model.TypeDescriptor;
import com.mybatis.generatorSql.operate.PlatformDbGenerator;
import com.mybatis.generatorSql.operate.PlatformGenerator;
import com.mybatis.generatorSql.ui.ChooseIfTestDialog;
import com.mybatis.messages.MybatisSmartCodeHelpBundle;
import com.mybatis.utils.CommonDataTableUtils;
import com.mybatis.utils.IconUtils;
import com.mybatis.utils.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GenerateCodeIFTestByMethodNameIntentionAction extends BaseElementAtCaretIntentionAction implements IntentionAction, Iconable {
    private static final Logger logger = LoggerFactory.getLogger(GenerateCodeIFTestByMethodNameIntentionAction.class);

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        String name = element.getContainingFile().getFileType().getName();
        if (!JavaFileType.INSTANCE.getName().equals(name)) {
            return false;
        }
        if (!element.getText().contains("By")) {
            return false;
        }
        PsiMethod parentMethodOfType = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (parentMethodOfType != null) {
            return false;
        }
        PsiClass parentClassOfType = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (parentClassOfType == null || (!parentClassOfType.isInterface())) {
            return false;
        }

        // 查找最近的有效节点
        PsiTypeElement statementElement = PsiTreeUtil.getParentOfType(element, PsiTypeElement.class);
        if (statementElement == null) {
            statementElement = PsiTreeUtil.getPrevSiblingOfType(element, PsiTypeElement.class);
        }
        // 当前节点的父类不是mapper类就返回
        PsiClass mapperClass = PsiTreeUtil.getParentOfType(statementElement, PsiClass.class);
        if (mapperClass == null) {
            return false;
        }
        boolean hasDatabaseComponent = CommonDataTableUtils.isIU();
        String text = statementElement.getText();

        PlatformSimpleGenerator platformSimpleGenerator = new PlatformSimpleGenerator();
        if (hasDatabaseComponent) {
            platformSimpleGenerator = new PlatformDbGenerator();
        }
        EntityMappingResolverFactory entityMappingResolverFactory = new EntityMappingResolverFactory(project);
        EntityMappingHolder entityMappingHolder = entityMappingResolverFactory.searchEntity(mapperClass);
        if (entityMappingHolder == null || entityMappingHolder.getEntityClass() == null) {
            return false;
        }
        PlatformGenerator platformGenerator = platformSimpleGenerator.getPlatformGenerator(project, element, entityMappingHolder, text);

        return platformGenerator.getConditionFields() != null && !platformGenerator.getConditionFields().isEmpty();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        try {
            PsiTypeElement statementElement = PsiTreeUtil.getParentOfType(element, PsiTypeElement.class);
            if (statementElement == null) {
                statementElement = PsiTreeUtil.getPrevSiblingOfType(element, PsiTypeElement.class);
            }
            PsiClass mapperClass = PsiTreeUtil.getParentOfType(statementElement, PsiClass.class);
            if (mapperClass == null) {
                logger.info("未找到mapper类");
                Messages.showErrorDialog(project, MybatisSmartCodeHelpBundle.message("not.found.entry.class"), "Error");
                return;
            }
            EntityMappingResolverFactory entityMappingResolverFactory = new EntityMappingResolverFactory(project);
            EntityMappingHolder entityMappingHolder = entityMappingResolverFactory.searchEntity(mapperClass);
            PsiClass entityClass = entityMappingHolder.getEntityClass();
            if (entityClass == null) {
                logger.info("未找到实体类");
                Messages.showErrorDialog(project, MybatisSmartCodeHelpBundle.message("not.found.entry.class"), "Error");
                return;
            }

            boolean hasDatabaseComponent = CommonDataTableUtils.isIU();
            String text = statementElement.getText();

            PlatformSimpleGenerator platformSimpleGenerator = new PlatformSimpleGenerator();
            if (hasDatabaseComponent) {
                platformSimpleGenerator = new PlatformDbGenerator();
            }
            PlatformGenerator platformGenerator = platformSimpleGenerator.getPlatformGenerator(project, element, entityMappingHolder, text);
            // 不仅仅是参数的字符串拼接， 还需要导入的对象
            TypeDescriptor parameterDescriptor = platformGenerator.getParameter();

            // 插入到编辑器
            TypeDescriptor returnDescriptor = platformGenerator.getReturn();
            if (returnDescriptor == null) {
                logger.info("Unsupported syntax");
                Messages.showErrorDialog(project, MybatisSmartCodeHelpBundle.message("java.method.name.format.error"), "Error");
                return;
            }
            boolean isSelect = returnDescriptor.getImportList().size() != 0;

            Optional<ConditionFieldWrapper> conditionFieldWrapperOptional = getConditionFieldWrapper(project,
                    platformGenerator.getDefaultDateWord(),
                    platformGenerator.getAllFields(),
                    platformGenerator.getResultFields(),
                    platformGenerator.getConditionFields(),
                    platformGenerator.getEntityClass(),
                    isSelect);
            if (conditionFieldWrapperOptional.isEmpty()) {
                logger.info("No suitable conditional wrapper was found, mapperClass: {}", mapperClass.getName());
                return;
            }
            ConditionFieldWrapper conditionFieldWrapper = conditionFieldWrapperOptional.get();
            // 找到 mapper.xml 的 Mapper 对象
            Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(project, mapperClass);
            final Ref<Boolean> saveFile = new Ref<Boolean>();
            if (firstMapper.isPresent()) {
                Mapper mapper = firstMapper.get();
                conditionFieldWrapper.setMapper(mapper);
                build(project, editor, statementElement, mapperClass, platformGenerator, parameterDescriptor, returnDescriptor, conditionFieldWrapper);
            } else {
                Messages.showErrorDialog(project, MybatisSmartCodeHelpBundle.message("not.found.xml.file"), "Error");
//                Collection<Mapper> mappers = MapperUtils.findMappers(project);
//                if (mappers.isEmpty()) {
//                    Messages.showErrorDialog(project, MybatisSmartCodeHelpBundle.message("not.found.xml.file"), "Error");
//                    return;
//                }
//                List<FilePath> filePaths = new ArrayList<>();
//                for (Mapper mapper : mappers) {
//                    XmlElement xmlElement = mapper.getParent().getXmlElement();
//                    if (xmlElement != null) {
//                        VirtualFile virtualFile = xmlElement.getContainingFile().getVirtualFile();
//                        filePaths.add(VcsUtil.getFilePath(virtualFile));
//                    }
//                }
//                PsiTypeElement finalStatementElement = statementElement;
//                ListPopup listPopup=JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<>(MybatisSmartCodeHelpBundle.message("choose.xml.file.title",mapperClass.getName()),filePaths){
//                    @Override
//                    public Icon getIconFor(FilePath value) {
//                        return IconUtils.XML_MYBATIS_ICON;
//                    }
//
//                    @Override
//                    public @Nullable PopupStep<?> onChosen(FilePath selectedValue, boolean finalChoice) {
//
//                        JavaUtils.findClazz(project,selectedValue.getPath());
//                        return this.doFinalStep(()->{
//                            Mapper selectMapper =null;
//                            for(Mapper mapper :mappers){
//                                XmlElement xmlElement = mapper.getParent().getXmlElement();
//                                if (xmlElement != null) {
//                                    VirtualFile virtualFile = xmlElement.getContainingFile().getVirtualFile();
//                                    if(virtualFile.getPath().equals(selectedValue.getPath())){
//                                        selectMapper=mapper;
//                                        break;
//                                    }
//                                }
//                            }
//                            if(selectMapper==null){
//                                Messages.showErrorDialog(project, MybatisSmartCodeHelpBundle.message("not.found.xml.file"), "Error");
//                                return;
//                            }
//                            conditionFieldWrapper.setMapper(selectMapper);
//                            WriteCommandAction.runWriteCommandAction(project,()->{
//                                build(project, editor, finalStatementElement, mapperClass, platformGenerator, parameterDescriptor, returnDescriptor, conditionFieldWrapper);
//                            });
//
//                        });
//                    }
//                },10);
//                listPopup.showInBestPositionFor(editor);
            }


        } catch (ProcessCanceledException e) {
            logger.info("generate error", e);
            Messages.showErrorDialog(project, MybatisSmartCodeHelpBundle.message("error"), "Error");
        }
        ;

    }

    private void build(@NotNull Project project, Editor editor, PsiTypeElement statementElement, PsiClass mapperClass, PlatformGenerator platformGenerator, TypeDescriptor parameterDescriptor, TypeDescriptor returnDescriptor, ConditionFieldWrapper conditionFieldWrapper) {
        MapperClassGenerateFactory mapperClassGenerateFactory = new MapperClassGenerateFactory(project,
                editor,
                statementElement,
                mapperClass,
                parameterDescriptor,
                conditionFieldWrapper,
                returnDescriptor);

        String newMethodString = mapperClassGenerateFactory.generateMethodStr(conditionFieldWrapper.getResultType());
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        final PsiMethod psiMethod = factory.createMethodFromText(newMethodString, mapperClass);

        List<TxField> resultTxFields = conditionFieldWrapper.getResultTxFields();
        platformGenerator.generateMapperXml(mapperClassGenerateFactory,
                psiMethod,
                conditionFieldWrapper,
                resultTxFields);
    }

    protected Mapper chooseMapperXmlFile(Project project, List<FilePath> filePaths, Editor editor, PsiClass psiClass) {

        return null;

    }


    protected Optional<ConditionFieldWrapper> getConditionFieldWrapper(Project project,
                                                                       String defaultDateWord,
                                                                       List<TxField> allFields,
                                                                       List<String> resultFields,
                                                                       List<String> conditionFields,
                                                                       PsiClass entityClass,
                                                                       boolean isSelect) {
        ChooseIfTestDialog chooseIfTestDialog = new ChooseIfTestDialog(project, true, conditionFields);
        chooseIfTestDialog.show();
        if (chooseIfTestDialog.getExitCode() != Messages.YES) {
            return Optional.empty();
        }
        Set<String> selectConditionFields = chooseIfTestDialog.getConditionFields();
        ConditionIfTestWrapper conditionIfTestWrapper = new ConditionIfTestWrapper(project, selectConditionFields, resultFields, allFields, defaultDateWord);
        if (isSelect) {
            conditionIfTestWrapper.setResultTypeClass(entityClass.getQualifiedName());
        } else {
            conditionIfTestWrapper.setResultTypeClass(null);
        }

        return Optional.of(conditionIfTestWrapper);
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Mybatis smart code help";
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return "[mybatis smart code help] Generate mybatis sql based on conditions";
    }

    @Override
    public Icon getIcon(int flags) {
        return IconUtils.JAVA_MYBATIS_ICON;
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
