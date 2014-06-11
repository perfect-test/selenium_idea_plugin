package com.unknown.seleniumplugin.checkers.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css.CssSelectorChecker;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 30.04.14.
 */
public class SeleniumSelectorAnnotator implements Annotator {

    private ISelectorChecker selectorChecker = new CssSelectorChecker();
    private static final String SELENIUM_FIND_BY_ANNOTATION_CLASS_NAME = "org.openqa.selenium.support.FindBy";

    @Override()
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiAnnotation) {
            PsiAnnotation annotation = (PsiAnnotation) element;
            PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
            if (referenceElement != null) {
                if (referenceElement.getQualifiedName().equals(SELENIUM_FIND_BY_ANNOTATION_CLASS_NAME)) {
                    PsiNameValuePair[] nameValuePairs = annotation.getParameterList().getAttributes();
                    if(nameValuePairs.length > 0) {
                        for(PsiNameValuePair nameValuePair : nameValuePairs) {
                            if(isCssParameter(nameValuePair)){
                                String value = getNameValuePairStringValue(nameValuePair);
                                System.out.println("ЗНачение : '" + getNameValuePairStringValue(nameValuePair) + "'");
                                if(value != null ) {
                                    TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                                            element.getTextRange().getEndOffset());
                                    try {
                                        CheckResult checkResult = selectorChecker.checkSelectorValid(value);
                                        if(!checkResult.isResultSuccess()) {
                                            holder.createErrorAnnotation(range, checkResult.getMessage());
                                        }
                                    } catch (NotParsebleSelectorException e) {
                                        holder.createWarningAnnotation(range, "Not parseble selector");
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

    }

    private boolean isCssParameter(PsiNameValuePair nameValuePair){
        String name = nameValuePair.getName();
        return name != null && name.equals("css");
    }

    private String getNameValuePairStringValue(PsiNameValuePair nameValuePair) {
        PsiAnnotationMemberValue value = nameValuePair.getValue();
        if(value != null) {
            return value.getText().replaceAll("\"","");
        }
        return null;
    }
}
