package com.unknown.seleniumplugin.checkers.annotator;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css.CssSelectorChecker;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.settings.SeleniumSettingsParams;
import com.unknown.seleniumplugin.utils.AnnotationChecker;
import com.unknown.seleniumplugin.utils.AnnotationsUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 30.04.14.
 */
public class SeleniumSelectorAnnotator implements Annotator {
    private static final ISelectorChecker cssSelectorChecker = new CssSelectorChecker();
    private ISelectorChecker selectorChecker;

    @Override()
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiAnnotation) {
            PsiAnnotation annotation = (PsiAnnotation) element;
            PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
            if (referenceElement != null) {
                if (AnnotationChecker.isFindByAnnotation(referenceElement.getQualifiedName())) {
                    PropertiesComponent properties = PropertiesComponent.getInstance(element.getProject());
                    if (properties.isValueSet(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED)) {
                        boolean isCheckEnabled = properties.isTrueValue(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED);
                        if (isCheckEnabled) {
                            PsiNameValuePair[] nameValuePairs = annotation.getParameterList().getAttributes();
                            if (nameValuePairs.length > 0) {
                                for (PsiNameValuePair nameValuePair : nameValuePairs) {
                                    setSelectorChecker(nameValuePair);
                                    if (selectorChecker != null) {
                                        PsiAnnotationMemberValue nameValuePairValue = nameValuePair.getValue();
                                        if (nameValuePairValue != null) {
                                            String value = AnnotationsUtils.getClearAnnotationParameterValue(nameValuePairValue);
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
        }

    }

    private void setSelectorChecker(PsiNameValuePair nameValuePair) {
        String name = nameValuePair.getName();
        if (name != null) {
            SelectorMethodValue selectorMethodValue = SelectorMethodValue.getByText(name);
            if (selectorMethodValue != null) {
                switch (selectorMethodValue) {
                    case CSS:
                        selectorChecker = cssSelectorChecker;
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
        }

    }

}
