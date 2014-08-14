package com.unknown.seleniumplugin.checkers.annotator;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.unknown.seleniumplugin.checkers.selectorscheckers.CheckResult;
import com.unknown.seleniumplugin.checkers.selectorscheckers.ISelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.exceptions.NotParsebleSelectorException;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.classnamechecker.ClassNameSelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.css.CssSelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.id.IDSelectorChecker;
import com.unknown.seleniumplugin.checkers.selectorscheckers.impl.tagname.TagNameSelectorChecker;
import com.unknown.seleniumplugin.domain.SelectorMethodValue;
import com.unknown.seleniumplugin.settings.SeleniumSettingsParams;
import com.unknown.seleniumplugin.utils.AnnotationChecker;
import com.unknown.seleniumplugin.utils.AnnotationsUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Created by mike-sid on 30.04.14.
 */
public class SeleniumSelectorAnnotator implements Annotator {
    private static final ISelectorChecker CSS_SELECTOR_CHECKER = new CssSelectorChecker();
    private static final ISelectorChecker ID_SELECTOR_CHECKER = new IDSelectorChecker();
    private static final ISelectorChecker TAG_NAME_SELECTOR_CHECKER = new TagNameSelectorChecker();
    private static final ISelectorChecker CLASS_NAME_SELECTOR_CHECKER = new ClassNameSelectorChecker();
    private ISelectorChecker selectorChecker;


    @Override()
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof PsiAnnotation) {
            checkAnnotation(element, holder);
        }
    }

    private void checkAnnotation(PsiElement element, AnnotationHolder holder) {
        PsiAnnotation annotation = (PsiAnnotation) element;
        PsiJavaCodeReferenceElement referenceElement = annotation.getNameReferenceElement();
        if (referenceElement != null) {
            PropertiesComponent properties = PropertiesComponent.getInstance(element.getProject());
            if (properties.isValueSet(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED)) {
                boolean isCheckEnabled = properties.isTrueValue(SeleniumSettingsParams.IS_SELECTOR_CHECK_ENABLED);
                if (isCheckEnabled) {
                    if (AnnotationChecker.isFindByAnnotation(referenceElement.getQualifiedName()) ||
                            AnnotationChecker.isFindBysAnnotation(referenceElement.getQualifiedName())) {
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
                                            checkError(value, nameValuePairValue, holder);
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

    private void checkError(String value, PsiElement element, AnnotationHolder holder) {
        try {
            CheckResult checkResult = selectorChecker.checkSelectorValid(value);
            if (!checkResult.isResultSuccess()) {
                int startOffset = element.getTextRange().getStartOffset() + checkResult.getPosition();
                int endOffset = startOffset + 2;
                TextRange range = new TextRange(startOffset, endOffset);
                holder.createErrorAnnotation(range, checkResult.getMessage());
            }
        } catch (NotParsebleSelectorException e) {
            TextRange range = new TextRange(element.getTextRange().getStartOffset(),
                    element.getTextRange().getEndOffset());
            holder.createWarningAnnotation(range, "Not parseble selector");
        } finally {
            selectorChecker = null;
        }
    }

    private void setSelectorChecker(PsiNameValuePair nameValuePair) {
        String name = nameValuePair.getName();
        if (name != null) {
            SelectorMethodValue selectorMethodValue = SelectorMethodValue.getByText(name);
            if (selectorMethodValue != null) {
                switch (selectorMethodValue) {
                    case CSS:
                        selectorChecker = CSS_SELECTOR_CHECKER;
                        break;
                    case ID:
                        selectorChecker = ID_SELECTOR_CHECKER;
                        break;
                    case TAG_NAME:
                        selectorChecker = TAG_NAME_SELECTOR_CHECKER;
                        break;
                    case CLASS_NAME:
                        selectorChecker = CLASS_NAME_SELECTOR_CHECKER;
                        break;
                    default:
                        //do nothing
                        break;
                }
            }
        }

    }

}
