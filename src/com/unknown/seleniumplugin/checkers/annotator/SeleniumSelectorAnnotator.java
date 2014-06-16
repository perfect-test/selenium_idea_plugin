package com.unknown.seleniumplugin.checkers.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css.CssSelectorChecker;
import com.unknown.seleniumplugin.utils.AnnotationChecker;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 30.04.14.
 */
public class SeleniumSelectorAnnotator implements Annotator {

    private ISelectorChecker selectorChecker = new CssSelectorChecker();

    @Override()
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiAnnotation) {
            PsiAnnotation annotation = (PsiAnnotation) element;
            PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
            if (referenceElement != null) {
                if (AnnotationChecker.isFindByAnnotation(referenceElement.getQualifiedName())) {
                    PsiNameValuePair[] nameValuePairs = annotation.getParameterList().getAttributes();
                    if (nameValuePairs.length > 0) {
                        for (PsiNameValuePair nameValuePair : nameValuePairs) {
                            if (isCssParameter(nameValuePair)) {
                                PsiAnnotationMemberValue nameValuePairValue = nameValuePair.getValue();
                                if(nameValuePairValue != null) {
                                    String value = getNameValuePairStringValue(nameValuePairValue);
                                    System.out.println("Значение : '" + value + "'");
                                    if (value != null) {
                                        try {
                                            CheckResult checkResult = selectorChecker.checkSelectorValid(value);
                                            if (!checkResult.isResultSuccess()) {
                                                int startOffset = nameValuePairValue.getTextRange().getStartOffset() + checkResult.getPosition();
                                                int endOffset = startOffset + 2;
                                                TextRange range = new TextRange(startOffset, endOffset);
                                                holder.createErrorAnnotation(range, checkResult.getMessage());
                                            }
                                        } catch (NotParsebleSelectorException e) {
                                            TextRange range = new TextRange(nameValuePairValue.getTextRange().getStartOffset(),
                                                    nameValuePairValue.getTextRange().getEndOffset());
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

    }

    private boolean isCssParameter(PsiNameValuePair nameValuePair) {
        String name = nameValuePair.getName();
        return name != null && name.equals("css");
    }

    private String getNameValuePairStringValue(PsiAnnotationMemberValue value) {
        return value.getText().replaceAll("\"", "");
    }
}
