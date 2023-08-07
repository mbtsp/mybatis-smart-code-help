package com.mybatis.generator.plugin;

import com.mybatis.enums.ServiceMode;
import com.mybatis.generator.MyGeneratedJavaFile;
import com.mybatis.model.CacheModel.GenerateServiceConfig;
import com.mybatis.model.CacheModel.ServiceMethod;
import com.mybatis.model.CacheModel.ServiceMethodParameter;
import com.mybatis.utils.MethodUtils;
import com.mybatis.utils.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.*;
import java.util.stream.Collectors;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class GenerateServicePlugin extends PluginAdapter {
    private String javaServiceInterfacePackage;
    private String javaServicePackage;
    private String javaServiceSourcePackage;
    private String javaMapperPackage;
    private Integer serviceMode;
    private Interface interfaze;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        this.javaServiceInterfacePackage = properties.getProperty("javaServiceInterfacePackage");
        this.javaServicePackage = properties.getProperty("javaServicePackage");
        this.javaServiceSourcePackage = properties.getProperty("javaServiceSourcePackage");
        this.javaMapperPackage = properties.getProperty("javaMapperPackage");
        this.serviceMode = Integer.valueOf(properties.getProperty("serviceMode"));
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
        String serviceName = serviceInterfaceName + "Impl";
        List<Method> newMethods = new ArrayList<>();
        List<Method> methods = interfaze.getMethods();
        List<ServiceMethod> serviceMethodList = new ArrayList<>();
        if (methods != null && !methods.isEmpty()) {
            if (serviceMode.equals(ServiceMode.EASY.getKey())) {
                methods = methods.stream().filter(method -> !method.getName().contains("Example")).collect(Collectors.toList());
            } else if (serviceMode.equals(ServiceMode.PURE.getKey())) {
                methods = new ArrayList<>();
            }
            for (Method method : methods) {
                ServiceMethod serviceMethod = new ServiceMethod();
                serviceMethod.setName(method.getName());
                if (method.getReturnType().isPresent()) {
                    serviceMethod.setShortName(method.getReturnType().get().getShortName());
                }
                List<ServiceMethodParameter> serviceMethodParameters = new ArrayList<>();
                List<Parameter> parameters = method.getParameters();
                for (int i = 0; i < parameters.size(); i++) {
                    ServiceMethodParameter serviceMethodParameter = new ServiceMethodParameter();
                    serviceMethodParameter.setShortName(parameters.get(i).getType().getShortName());
                    serviceMethodParameter.setName(parameters.get(i).getName());
                    if (i == parameters.size() - 1) {
                        serviceMethodParameter.setIs_last(true);
                    }
                    serviceMethodParameters.add(serviceMethodParameter);
                }
                serviceMethod.setParameters(serviceMethodParameters);
                serviceMethodList.add(serviceMethod);
                Method method1 = MethodUtils.copy(method);
                MethodUtils.clearParameterAnnotations(method, method1);
                method1.addBodyLine(generateBodyForServiceImplMethod(StringUtils.lowerCaseFirstChar(shortName), method));
                method1.addAnnotation("@Override");
                method1.setAbstract(false);
                newMethods.add(method1);
            }
        }
        GenerateServiceConfig generateServiceConfig = new GenerateServiceConfig();
        generateServiceConfig.setMethods(serviceMethodList);
        generateServiceConfig.setServiceName(serviceName);
        generateServiceConfig.setDaoName(StringUtils.lowerCaseFirstChar(shortName));
        generateServiceConfig.setDaoType(shortName);
//        Set<FullyQualifiedJavaType> serviceFullyQualifiedJavaTypes = new HashSet<>();
        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypes = interfaze.getImportedTypes();
//        serviceFullyQualifiedJavaTypes.addAll(fullyQualifiedJavaTypes);

//        serviceFullyQualifiedJavaTypes = serviceFullyQualifiedJavaTypes.stream().filter(fullyQualifiedJavaType -> !fullyQualifiedJavaType.getFullyQualifiedName().contains("ibatis")).collect(Collectors.toSet());
        List<String> importList = fullyQualifiedJavaTypes.stream().map(FullyQualifiedJavaType::getFullyQualifiedName).collect(Collectors.toList());
        importList = importList.stream().filter(str -> !str.contains("ibatis") && !str.equals("com.baomidou.mybatisplus.core.mapper.BaseMapper")).collect(Collectors.toList());
        String stringBuilder = "/**" + "\r\n" +
                "* " + MergeConstants.NEW_ELEMENT_TAG + "\r\n" +
                "* generator on " + new Date().toString() + "\r\n" +
                "*/";
        importList.add("org.springframework.stereotype.Service");
        importList.add(this.javaMapperPackage + "." + shortName);
//        serviceFullyQualifiedJavaTypes.add(new FullyQualifiedJavaType(this.javaMapperPackage + "." + shortName));
        generateServiceConfig.setUseServiceInterface(true);
        generateServiceConfig.setServiceInterfaceFullName(this.javaServiceInterfacePackage + "." + serviceInterfaceName);
        generateServiceConfig.setServiceFileHeader(stringBuilder);
        generateServiceConfig.setServiceInterfaceName(serviceInterfaceName);
        generateServiceConfig.setServicePackageName(this.javaServicePackage);
        if (serviceMode.equals(ServiceMode.EASY.getKey())) {
            importList = importList.stream().filter(string -> !string.contains("Example")).collect(Collectors.toList());
        } else if (serviceMode.equals(ServiceMode.PURE.getKey())) {
            importList = importList.stream().filter(str -> str.contains(shortName)).collect(Collectors.toList());
        }
        importList.add(generateServiceConfig.getServiceInterfaceFullName());
        generateServiceConfig.setImports(importList);
        generateServiceConfig.setServicePath(this.javaServiceSourcePackage + "\\" + this.javaServicePackage.replace(".", "\\") + "\\" + serviceName + ".java");

        TopLevelClass topLevelClass = new TopLevelClass(generateServiceConfig.getServicePackageName() + "." + generateServiceConfig.getServiceName());

        newMethods.forEach(topLevelClass::addMethod);
        FullyQualifiedJavaType serviceInterfaceJavaType = new FullyQualifiedJavaType(generateServiceConfig.getServiceInterfaceFullName());
        topLevelClass.addSuperInterface(serviceInterfaceJavaType);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addAnnotation("@Service");
        topLevelClass.setAbstract(false);
        topLevelClass.setFinal(false);
        topLevelClass.setStatic(false);

        topLevelClass.addFileCommentLine(generateServiceConfig.getServiceFileHeader());
        topLevelClass.addImportedTypes(generateServiceConfig.getFullyQualifiedJavaTypes());

        MyGeneratedJavaFile generatedJavaFile = new MyGeneratedJavaFile(topLevelClass, this.javaServiceSourcePackage, context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
        generatedJavaFile.setMergeable(false);
        generatedJavaFiles.add(generatedJavaFile);
        return generatedJavaFiles;
    }

    private String generateBodyForServiceImplMethod(String mapperName, Method m) {
        StringBuilder sbf = new StringBuilder("return ");
        sbf.append(mapperName).append(".").append(m.getName()).append("(");

        boolean singleParam = true;
        for (Parameter parameter : m.getParameters()) {

            if (singleParam)
                singleParam = false;
            else
                sbf.append(", ");
            sbf.append(parameter.getName());

        }

        sbf.append(");");
        return sbf.toString();
    }

    public String getJavaServiceInterfacePackage() {
        return javaServiceInterfacePackage;
    }

    public void setJavaServiceInterfacePackage(String javaServiceInterfacePackage) {
        this.javaServiceInterfacePackage = javaServiceInterfacePackage;
    }

    public String getJavaServicePackage() {
        return javaServicePackage;
    }

    public void setJavaServicePackage(String javaServicePackage) {
        this.javaServicePackage = javaServicePackage;
    }

    public String getJavaServiceSourcePackage() {
        return javaServiceSourcePackage;
    }

    public void setJavaServiceSourcePackage(String javaServiceSourcePackage) {
        this.javaServiceSourcePackage = javaServiceSourcePackage;
    }

    public String getJavaMapperPackage() {
        return javaMapperPackage;
    }

    public void setJavaMapperPackage(String javaMapperPackage) {
        this.javaMapperPackage = javaMapperPackage;
    }
}
