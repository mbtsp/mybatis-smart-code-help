package com.mybatis.generator.plugin;

import com.intellij.openapi.project.Project;
import com.mybatis.enums.ServiceMode;
import com.mybatis.generator.MyGeneratedJavaFile;
import com.mybatis.model.CacheModel.GenerateServiceInterfaceConfig;
import com.mybatis.model.CacheModel.ServiceInterfaceMethod;
import com.mybatis.model.CacheModel.ServiceInterfaceMethodParameter;
import com.mybatis.utils.MethodUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.*;
import java.util.stream.Collectors;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class GenerateServiceInterfacePlugin extends PluginAdapter {
    private String javaServiceInterfacePackage;
    private String javaServiceInterfaceSourcePackage;
    private Project project;
    private Integer serviceInterfaceMode;
    private Interface interfaze;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        this.javaServiceInterfacePackage = properties.getProperty("javaServiceInterfacePackage");
        this.javaServiceInterfaceSourcePackage = properties.getProperty("javaServiceInterfaceSourcePackage");
        this.serviceInterfaceMode = Integer.valueOf(properties.getProperty("serviceInterfaceMode"));
    }

    /**
     * This method is called after all the setXXX methods are called, but before
     * any other method is called. This allows the plugin to determine whether
     * it can run or not. For example, if the plugin requires certain properties
     * to be set, and the properties are not set, then the plugin is invalid and
     * will not run.
     *
     * @param warnings add strings to this list to specify warnings. For example, if
     *                 the plugin is invalid, you should specify why. Warnings are
     *                 reported to users after the completion of the run.
     * @return true if the plugin is in a valid state. Invalid plugins will not
     * be called
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * This method is called when the entire client has been generated.
     * Implement this method to add additional methods or fields to a generated
     * client interface or implementation.
     *
     * @param interfaze         the generated interface if any, may be null
     * @param introspectedTable The class containing information about the table as
     *                          introspected from the database
     * @return true if the interface should be generated, false if the generated
     * interface should be ignored. In the case of multiple plugins, the
     * first plugin returning false will disable the calling of further
     * plugins.
     */
    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        this.interfaze = interfaze;
//        if(interfaze==null){
//            return false;
//        }
//        String shortName =interfaze.getType().getShortName();
//        String serviceInterfaceName="";
//        if (shortName.contains("Mapper")) {
//            serviceInterfaceName = shortName.substring(0, shortName.length() - "Mapper".length()) + "Service";
//        } else {
//            serviceInterfaceName = shortName + "Service";
//        }
//        List<Method> methods=interfaze.getMethods();
//        List<ServiceInterfaceMethod> serviceInterfaceMethodList = new ArrayList<>();
//        if(methods!=null && !methods.isEmpty()){
//            if(serviceInterfaceMode.equals(ServiceMode.EASY.getKey())){
//                methods=methods.stream().filter(method -> !method.getName().contains("Example")).collect(Collectors.toList());
//            }else if(serviceInterfaceMode.equals(ServiceMode.PURE.getKey())){
//                methods= new ArrayList<>();
//            }
//            for(Method method:methods){
//                ServiceInterfaceMethod serviceInterfaceMethod = new ServiceInterfaceMethod();
//                serviceInterfaceMethod.setName(method.getName());
//                if(method.getReturnType().isPresent()){
//                    serviceInterfaceMethod.setShortName(method.getReturnType().get().getShortName());
//                }
//                List<ServiceInterfaceMethodParameter> serviceInterfaceMethodParameters = new ArrayList<>();
//                List<Parameter> parameters=method.getParameters();
//                for(int i=0 ;i<parameters.size();i++){
//                    ServiceInterfaceMethodParameter serviceInterfaceMethodParameter = new ServiceInterfaceMethodParameter();
//                    serviceInterfaceMethodParameter.setShortName(parameters.get(i).getType().getShortName());
//                    serviceInterfaceMethodParameter.setName(parameters.get(i).getName());
//                    if(i==parameters.size()-1){
//                        serviceInterfaceMethodParameter.setIs_last(true);
//                    }
//                    serviceInterfaceMethodParameters.add(serviceInterfaceMethodParameter);
//                }
//                serviceInterfaceMethod.setParameters(serviceInterfaceMethodParameters);
//                serviceInterfaceMethodList.add(serviceInterfaceMethod);
//            }
//        }
//        GenerateServiceInterfaceConfig generateServiceInterfaceConfig = new GenerateServiceInterfaceConfig();
//
//        generateServiceInterfaceConfig.setMethods(serviceInterfaceMethodList);
//        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes= interfaze.getImportedTypes();
//        List<String> importList=fullyQualifiedJavaTypes.stream().map(FullyQualifiedJavaType::getFullyQualifiedName).collect(Collectors.toList());
//        importList=importList.stream().filter(str-> !str.contains("ibatis")).collect(Collectors.toList());
//        String stringBuilder = "/**" + "\r\n" +
//                "* " + MergeConstants.NEW_ELEMENT_TAG + "\r\n" +
//                "* generator on " + new Date().toString() + "\r\n" +
//                "*/";
//        generateServiceInterfaceConfig.setServiceInterfaceFileHeader(stringBuilder);
//        generateServiceInterfaceConfig.setServiceInterfaceName(serviceInterfaceName);
//        generateServiceInterfaceConfig.setServiceInterfacePackageName(this.javaServiceInterfacePackage);
//
//        if(serviceInterfaceMode.equals(ServiceMode.EASY.getKey())){
//            importList=importList.stream().filter(string -> !string.contains("Example")).collect(Collectors.toList());
//        }else if(serviceInterfaceMode.equals(ServiceMode.PURE.getKey())){
//            importList= new ArrayList<>();
//        }
//
//        generateServiceInterfaceConfig.setServiceInterfaceImport(importList);
//        generateServiceInterfaceConfig.setServiceInterfacePath(this.javaServiceInterfaceSourcePackage+"\\"+this.javaServiceInterfacePackage.replace(".","\\")+"\\"+serviceInterfaceName+".java");
//        boolean flag=GenServiceInterfaceService.generatorServiceInterfaceByFtl(generateServiceInterfaceConfig);
//        if(flag){
//            context.get
//            PsiFile[] psiFiles=FilenameIndex.getFilesByName(project,serviceInterfaceName, GlobalSearchScope.allScope(project));
//            for(PsiFile file:psiFiles){
//                JavaCodeStyleManager.getInstance(project).optimizeImports(file);
//                CodeStyleManager.getInstance(project).reformat(file);
//            }

//        }

        return true;
    }

    /**
     * This method can be used to generate additional Java files needed by your
     * implementation that might be related to a specific table. This method is
     * called once for every table in the configuration.
     *
     * @param introspectedTable The class containing information about the table as
     *                          introspected from the database
     * @return a List of GeneratedJavaFiles - these files will be saved
     * with the other files from this run.
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<>();
        if (interfaze == null) {
            return generatedJavaFiles;
        }
        String shortName = interfaze.getType().getShortName();
        String serviceInterfaceName = "";
        if (shortName.contains("Mapper")) {
            serviceInterfaceName = shortName.substring(0, shortName.length() - "Mapper".length()) + "Service";
        } else {
            serviceInterfaceName = shortName + "Service";
        }
        List<Method> newMethods = new ArrayList<>();
        List<Method> methods = interfaze.getMethods();
        List<ServiceInterfaceMethod> serviceInterfaceMethodList = new ArrayList<>();
        if (methods != null && !methods.isEmpty()) {
            if (serviceInterfaceMode.equals(ServiceMode.EASY.getKey())) {
                methods = methods.stream().filter(method -> !method.getName().contains("Example")).collect(Collectors.toList());
            } else if (serviceInterfaceMode.equals(ServiceMode.PURE.getKey())) {
                methods = new ArrayList<>();
            }
            for (Method method : methods) {
                ServiceInterfaceMethod serviceInterfaceMethod = new ServiceInterfaceMethod();
                serviceInterfaceMethod.setName(method.getName());
                if (method.getReturnType().isPresent()) {
                    serviceInterfaceMethod.setShortName(method.getReturnType().get().getShortName());
                }
                List<ServiceInterfaceMethodParameter> serviceInterfaceMethodParameters = new ArrayList<>();
                List<Parameter> parameters = method.getParameters();
                for (int i = 0; i < parameters.size(); i++) {
                    ServiceInterfaceMethodParameter serviceInterfaceMethodParameter = new ServiceInterfaceMethodParameter();
                    serviceInterfaceMethodParameter.setShortName(parameters.get(i).getType().getShortName());
                    serviceInterfaceMethodParameter.setName(parameters.get(i).getName());
                    if (i == parameters.size() - 1) {
                        serviceInterfaceMethodParameter.setIs_last(true);
                    }
                    serviceInterfaceMethodParameters.add(serviceInterfaceMethodParameter);
                }
                serviceInterfaceMethod.setParameters(serviceInterfaceMethodParameters);
                serviceInterfaceMethodList.add(serviceInterfaceMethod);
                Method method1 = MethodUtils.copy(method);
                MethodUtils.clearParameterAnnotations(method, method1);
                newMethods.add(method1);
            }

        }
        GenerateServiceInterfaceConfig generateServiceInterfaceConfig = new GenerateServiceInterfaceConfig();

        generateServiceInterfaceConfig.setMethods(serviceInterfaceMethodList);
        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes = interfaze.getImportedTypes();
        Set<FullyQualifiedJavaType> serviceInterfaceFullyQualifiedJavaTypes = new HashSet<>();
        serviceInterfaceFullyQualifiedJavaTypes.addAll(fullyQualifiedJavaTypes);
        serviceInterfaceFullyQualifiedJavaTypes = serviceInterfaceFullyQualifiedJavaTypes.stream().filter(fullyQualifiedJavaType -> !fullyQualifiedJavaType.getFullyQualifiedName().contains("ibatis")).collect(Collectors.toSet());
        List<String> importList = fullyQualifiedJavaTypes.stream().map(FullyQualifiedJavaType::getFullyQualifiedName).collect(Collectors.toList());
        importList = importList.stream().filter(str -> !str.contains("ibatis") && !str.equals("com.baomidou.mybatisplus.core.mapper.BaseMapper")).collect(Collectors.toList());
        String stringBuilder = "/**" + "\r\n" +
                "* " + MergeConstants.NEW_ELEMENT_TAG + "\r\n" +
                "* generator on " + new Date().toString() + "\r\n" +
                "*/";
        generateServiceInterfaceConfig.setServiceInterfaceFileHeader(stringBuilder);
        generateServiceInterfaceConfig.setServiceInterfaceName(serviceInterfaceName);
        generateServiceInterfaceConfig.setServiceInterfacePackageName(this.javaServiceInterfacePackage);
        if (serviceInterfaceMode.equals(ServiceMode.EASY.getKey())) {
            importList = importList.stream().filter(string -> !string.contains("Example")).collect(Collectors.toList());
        } else if (serviceInterfaceMode.equals(ServiceMode.PURE.getKey())) {
            importList = new ArrayList<>();
        }

        generateServiceInterfaceConfig.setServiceInterfaceImport(importList);
        generateServiceInterfaceConfig.setServiceInterfacePath(this.javaServiceInterfaceSourcePackage + "\\" + this.javaServiceInterfacePackage.replace(".", "\\") + "\\" + serviceInterfaceName + ".java");
//        boolean flag=GenServiceInterfaceService.generatorServiceInterfaceByFtl(generateServiceInterfaceConfig);

        Interface serviceInterServiceClass = new Interface(generateServiceInterfaceConfig.getServiceInterfacePackageName() + "." + generateServiceInterfaceConfig.getServiceInterfaceName());
        serviceInterServiceClass.addImportedTypes(generateServiceInterfaceConfig.getFullyQualifiedJavaTypes());
        serviceInterServiceClass.setStatic(false);
        serviceInterServiceClass.addFileCommentLine(generateServiceInterfaceConfig.getServiceInterfaceFileHeader());
        serviceInterServiceClass.setVisibility(JavaVisibility.PUBLIC);
        newMethods.forEach(serviceInterServiceClass::addMethod);

        MyGeneratedJavaFile generatedJavaFile = new MyGeneratedJavaFile(serviceInterServiceClass, this.javaServiceInterfaceSourcePackage, context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
        generatedJavaFile.setMergeable(false);
        generatedJavaFiles.add(generatedJavaFile);
        return generatedJavaFiles;
    }

    public String getJavaServiceInterfacePackage() {
        return javaServiceInterfacePackage;
    }

    public void setJavaServiceInterfacePackage(String javaServiceInterfacePackage) {
        this.javaServiceInterfacePackage = javaServiceInterfacePackage;
    }

    public String getJavaServiceInterfaceSourcePackage() {
        return javaServiceInterfaceSourcePackage;
    }

    public void setJavaServiceInterfaceSourcePackage(String javaServiceInterfaceSourcePackage) {
        this.javaServiceInterfaceSourcePackage = javaServiceInterfaceSourcePackage;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
