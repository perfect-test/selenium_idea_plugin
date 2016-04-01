package com.unknown.seleniumplugin.linemaker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.*;
import com.unknown.seleniumplugin.utils.AnnotationChecker;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by mike-sid on 11.06.14.
 */
public class SeleniumFieldsLineMakerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if(element instanceof PsiAnnotation) {
            PsiAnnotation annotation = (PsiAnnotation) element;
            PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
            if (referenceElement != null && referenceElement.getQualifiedName() != null) {
                if (AnnotationChecker.isFindByAnnotation(referenceElement.getQualifiedName())) {
                    NavigationGutterIconBuilder<PsiElement> builder =
                            NavigationGutterIconBuilder.create(PluginIcons.SELENIUM_LOGO).setTarget(element).setTooltipText("Selenium element field");
                    result.add(builder.createLineMarkerInfo(element));
                }
            }
        }
    }
}
