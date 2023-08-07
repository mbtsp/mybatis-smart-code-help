package com.mybatis.codeInsight;

import com.google.common.base.Function;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.CommonProcessors;
import com.intellij.util.xml.DomElement;
import com.mybatis.service.JavaService;
import com.mybatis.utils.IconUtils;
import com.mybatis.utils.JavaUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class MybatisJavaLineMarkerProvider extends RelatedItemLineMarkerProvider {
    //    @Override
//    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
//        ElementInnerFilter filter = null;
//        if (element instanceof PsiClass) {
//            filter = new PsiClassElementInnerFilter();
//        }
//        if (filter == null && element instanceof PsiMethod) {
//            filter = new PsiMethodElementInnerFilter();
//        }
//        if (filter != null) {
//            filter.collectNavigationMarkers(element, result);
//        }
//    }
//
//
//    /**
//     * 元素内部过滤器
//     */
//    private abstract class ElementInnerFilter {
//        protected abstract Collection<? extends DomElement> getResults(@NotNull PsiElement element);
//
//        private void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
//            final Collection<? extends DomElement> results = getResults(element);
//
//            if (!results.isEmpty()) {
//                String xmlFileName = Objects.requireNonNull(results.stream().findFirst().get().getXmlElement()).getContainingFile().getName();
//                String name = "";
//                if (element instanceof PsiMethodImpl) {
//                    name = ":" + ((PsiMethodImpl) element).getName();
//                }
//                final List<XmlTag> xmlTags = results.stream().map(DomElement::getXmlTag).collect(Collectors.toList());
//                NavigationGutterIconBuilder<PsiElement> builder =
//                        NavigationGutterIconBuilder.create(IconUtils.JAVA_MYBATIS_ICON)
//                                .setAlignment(GutterIconRenderer.Alignment.CENTER)
//                                .setCellRenderer(new MapperXmlProvider.MyRenderer())
//                                .setTargets(xmlTags)
//                                .setTooltipTitle("navigation to mapper xml:" + xmlFileName + name);
//                final PsiElement targetMarkerInfo = Objects.requireNonNull(((PsiNameIdentifierOwner) element).getNameIdentifier());
//                result.add(builder.createLineMarkerInfo(targetMarkerInfo));
//            }
//        }
//    }
//
//    /**
//     * PsiClass过滤器
//     */
//    private class PsiClassElementInnerFilter extends ElementInnerFilter {
//
//        @Override
//        protected Collection<? extends DomElement> getResults(@NotNull PsiElement element) {
//            // 可跳转的节点加入跳转标识
//            CommonProcessors.CollectProcessor<Mapper> processor = new CommonProcessors.CollectProcessor<>();
//            JavaService.getInstance(element.getProject()).processClass((PsiClass) element, processor);
//            return processor.getResults();
//        }
//
//    }
//
//    /**
//     * PsiMethod 过滤器
//     */
//    private class PsiMethodElementInnerFilter extends ElementInnerFilter {
//
//        @Override
//        protected Collection<? extends DomElement> getResults(@NotNull PsiElement element) {
//            CommonProcessors.CollectProcessor<IdDomElement> processor = new CommonProcessors.CollectProcessor<>();
//            JavaService.getInstance(element.getProject()).processMethod(((PsiMethod) element), processor);
//            return processor.getResults();
//        }
//
//    }
    private static final Function<DomElement, XmlTag> FUN = new Function<DomElement, XmlTag>() {
        public XmlTag apply(DomElement domElement) {
            return domElement.getXmlTag();
        }
    };

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!(element instanceof PsiClass || element instanceof PsiMethod)) {
            return;
        }
        if ((element instanceof PsiMethod) && !JavaUtils.isElementWithinInterface(element)) {
            return;
        }
        createLineMarker(element, result);

    }

    private void createLineMarker(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        CommonProcessors.CollectProcessor<DomElement> processor = new CommonProcessors.CollectProcessor<>();
        JavaService.getInstance(element.getProject()).process(element, processor);
        Collection<DomElement> idDomElements = processor.getResults();
        String name = "";
        if (element instanceof PsiMethodImpl) {
            name = ":" + ((PsiMethodImpl) element).getName();
        }
        if (idDomElements.size() > 0) {
            String xmlFileName = Objects.requireNonNull(idDomElements.stream().findFirst().get().getXmlElement()).getContainingFile().getName();
            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(IconUtils.JAVA_MYBATIS_ICON)
                    .setAlignment(GutterIconRenderer.Alignment.CENTER)
                    .setTargets(idDomElements.stream().map(DomElement::getXmlTag).collect(Collectors.toSet()))
                    .setTooltipTitle("navigation to mapper xml" + xmlFileName + name);
            result.add(builder.createLineMarkerInfo(Objects.requireNonNull(((PsiNameIdentifierOwner) element).getNameIdentifier())));
        }
    }
}
