package ${serviceInterfacePackageName};

<#list serviceInterfaceImport as import>import ${import};
</#list>
<#if serviceInterfaceFileHeader??>${serviceInterfaceFileHeader}
</#if>
public interface ${serviceInterfaceName}{

<#list methods as method>
    ${method.shortName} ${method.name}(<#list method.parameters as parameter>${parameter.shortName} ${parameter.name}<#if parameter?is_last><#else>,</#if></#list>);

</#list>
}