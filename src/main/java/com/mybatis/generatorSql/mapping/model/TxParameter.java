package com.mybatis.generatorSql.mapping.model;

import com.intellij.psi.*;
import com.mybatis.generatorSql.mapping.AreaSequence;
import com.mybatis.utils.JdbcTypeUtils;
import com.mybatis.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The type Tx parameter.
 */
public class TxParameter {
    public static final String JAVA_LANG = "java.lang";
    private static Set<String> primitiveType = new HashSet<>() {
        {
            add("boolean");
            add("byte");
            add("short");
            add("int");
            add("long");
            add("float");
            add("double");
        }
    };
    private AreaSequence areaSequence;
    private String typeText;
    private String canonicalTypeText;
    private String name;
    private boolean paramAnnotation;
    //
    private List<String> importClass = Collections.emptyList();
    private String itemContent;

    /**
     * Create by psi field tx parameter.
     *
     * @param psiField     the psi field
     * @param areaSequence
     * @return the tx parameter
     */
    public static TxParameter createByPsiField(PsiField psiField,
                                               AreaSequence areaSequence) {
        TxParameter txParameter = new TxParameter();
        final PsiType type = psiField.getType();
        txParameter.typeText = type.getPresentableText();
        txParameter.canonicalTypeText = type.getCanonicalText();
        txParameter.name = psiField.getName();
        txParameter.paramAnnotation = true;
        txParameter.areaSequence = areaSequence;
        txParameter.itemContent = JdbcTypeUtils.wrapperField(psiField.getName(), type.getCanonicalText());
        if (!typeIsPrimitive(type)) {
            txParameter.importClass = findImportClass(psiField, type);
        }
        return txParameter;
    }

    private static List<String> findImportClass(PsiField psiField, PsiType type) {
        PsiTypeElement typeElement = psiField.getTypeElement();
        if (typeElement != null) {
            PsiJavaCodeReferenceElement innermostComponentReferenceElement = typeElement.getInnermostComponentReferenceElement();
            // import List<User> ,  add java.lang.User and com.xx.model.User
            if (innermostComponentReferenceElement != null) {
                List<String> importClasses = new ArrayList<>();
                importClasses.add(innermostComponentReferenceElement.getQualifiedName());
                @NotNull PsiType[] typeParameters = innermostComponentReferenceElement.getTypeParameters();
                for (PsiType typeParameter : typeParameters) {
                    if (!determinePrimitive(typeParameter.getCanonicalText())) {
                        importClasses.add(typeParameter.getCanonicalText());
                    }
                }
                return importClasses;
            }
        }
        return Collections.emptyList();
    }

    private static boolean typeIsPrimitive(PsiType type) {
        return determinePrimitive(type.getCanonicalText()) ||
                (type.getArrayDimensions() > 0 && determinePrimitive(type.getDeepComponentType().getCanonicalText()));
    }

    private static boolean determinePrimitive(String canonicalText) {
        if (canonicalText.startsWith(JAVA_LANG)) {
            return true;
        }
        if (primitiveType.contains(canonicalText)) {
            return true;
        }
        return false;
    }

    public static TxParameter createCollectionByTxParameter(TxParameter txParameter) {
        return createByOrigin(txParameter.getName() + "List",
                "Collection<" + txParameter.getTypeText() + ">",
                "java.util.Collection");
    }

    public static TxParameter createByPsiParameter(PsiParameter psiParameter) {
        TxParameter byOrigin = createByOrigin(psiParameter.getName(), psiParameter.getType().getCanonicalText(), psiParameter.getType().getCanonicalText());

        PsiTypeElement typeElement = psiParameter.getTypeElement();
        if (typeElement != null) {
            PsiJavaCodeReferenceElement innermostComponentReferenceElement = typeElement.getInnermostComponentReferenceElement();
            if (innermostComponentReferenceElement != null) {
                final @NotNull PsiType[] typeParameters = innermostComponentReferenceElement.getTypeParameters();
                if (typeParameters.length > 0) {
                    final PsiType typeParameter = typeParameters[0];
                    byOrigin.itemContent = JdbcTypeUtils.wrapperField("item", typeParameter.getCanonicalText());
                }
            }
        }
        return byOrigin;
    }

    /**
     * Create by origin tx parameter.
     *
     * @param name              变量名
     * @param typeText          定义类型简称
     * @param canonicalTypeText 定义类型的全称,用于导入
     * @return tx parameter
     */
    public static TxParameter createByOrigin(String name, String typeText, String canonicalTypeText) {
        return createByOrigin(name, typeText, canonicalTypeText, true, Collections.singletonList(canonicalTypeText));
    }


    /**
     * Create by origin tx parameter.
     *
     * @param name              变量名, 例如:  blogCollection
     * @param typeText          定义类型简称,例如:  java.util.Collection
     * @param canonicalTypeText 定义类型的全称,用于导入;  例如: java.util.Collection
     * @param paramAnnotation   the param annotation
     * @return tx parameter
     */
    public static TxParameter createByOrigin(String name, String typeText, String canonicalTypeText, boolean paramAnnotation, List<String> importClass) {
        TxParameter txParameter = new TxParameter();
        txParameter.name = name;
        txParameter.typeText = typeText;
        txParameter.canonicalTypeText = canonicalTypeText;
        txParameter.paramAnnotation = paramAnnotation;
        txParameter.importClass = importClass;
//        txParameter.itemContent = JdbcTypeUtils.wrapperField(name, canonicalTypeText);
        return txParameter;
    }

    public static TxParameter createByEntityClass(PsiClass entityClass) {
        String name = StringUtils.lowerCaseFirstChar(entityClass.getName());
        return TxParameter.createByOrigin(name,
                entityClass.getQualifiedName(),
                entityClass.getQualifiedName(),
                true,
                Collections.singletonList(entityClass.getQualifiedName())
        );
    }

    public AreaSequence getAreaSequence() {
        return areaSequence;
    }

    /**
     * 字段类型简称
     *
     * @return type text
     */
    public String getTypeText() {
        return typeText;
    }

    /**
     * Gets canonical type text.
     *
     * @return the canonical type text
     */
    public String getCanonicalTypeText() {
        return canonicalTypeText;
    }

    /**
     * 字段名称
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Is param annotation boolean.
     *
     * @return the boolean
     */
    public boolean isParamAnnotation() {
        return paramAnnotation;
    }

    public List<String> getImportClass() {
        return importClass;
    }

    public String getItemContent(String item) {
        return itemContent;
    }
}
