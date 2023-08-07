package ${servicePackageName};

import org.springframework.stereotype.Service;
<#list imports as import>import ${import};
</#list>
<#if useServiceInterface>import ${serviceInterfaceFullName};
</#if>
<#if serviceFileHeader??>${serviceFileHeader}
</#if>
@Service
public class ${serviceName}<#if useServiceInterface> implements ${serviceInterfaceName}</#if>{


private final ${daoType} ${daoName};

public ${serviceName}(${daoType} ${daoName}) {
this.${daoName} = ${daoName};
}

<#list methods as method>
    <#if useServiceInterface>@Override</#if>
    public ${method.shortName} ${method.name}(<#list method.parameters as parameter>${parameter.shortName} ${parameter.name}<#if parameter?is_last><#else>,</#if></#list>) {
    return ${daoName}.${method.name}(<#list method.parameters as parameter>${parameter.name}<#if parameter?is_last><#else>,</#if></#list>);
    }

</#list>
}
